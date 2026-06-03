package com.vincula.service;

import com.vincula.dto.paciente.PacienteDTO;
import com.vincula.dto.paciente.PacienteResponseDTO;
import com.vincula.dto.paciente.PacienteShortResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.Sexo;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.PacienteRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;
    private final TerritorializacaoService territorializacaoService;
    private final GeocodingService geocodingService;

    public PacienteService(PacienteRepository pacienteRepository,
                           EnderecoMapper enderecoMapper,
                           AuditoriaFacade auditoriaFacade,
                           TerritorializacaoService territorializacaoService,
                           GeocodingService geocodingService) {
        this.pacienteRepository = pacienteRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
        this.territorializacaoService = territorializacaoService;
        this.geocodingService = geocodingService;
    }

    public PacienteResponseDTO criar(PacienteDTO dto) {
        validarDocumentoCreate(dto);

        Paciente entity = toEntity(dto);

        Paciente salvo = pacienteRepository.save(entity);

        auditoriaFacade.pacienteCriado(salvo.getId());

        return toDTO(salvo);
    }

    public Page<PacienteResponseDTO> listarTodos(Pageable pageable) {
        return pacienteRepository.findAllByOrderByNomeCompletoAsc(pageable)
                .map(this::toDTO);
    }

    public List<PacienteShortResponseDTO> listarTodos() {
        return pacienteRepository.findAllByOrderByNomeCompletoAsc()
                .stream()
                .map(this::toShortDTO)
                .toList();
    }

    public Page<PacienteResponseDTO> listarTodosPorUnidade(Long unidadeSaudeId, Pageable pageable) {
        return pacienteRepository.findByUnidadeSaudeIdOrderByNomeCompletoAsc(unidadeSaudeId, pageable)
                .map(this::toDTO);
    }

    public Page<PacienteResponseDTO> listarTodosFiltrados(String filtro, Pageable pageable) {
        return pacienteRepository.findFiltrados(filtro, pageable)
                .map(this::toDTO);
    }

    public Page<PacienteResponseDTO> listarTodosPorUnidadeFiltrados(Long unidadeSaudeId, String filtro, Pageable pageable) {
        return pacienteRepository.findFiltradosByUnidade(unidadeSaudeId, filtro, pageable)
                .map(this::toDTO);
    }

    public PacienteResponseDTO buscarPorId(Long id) {
        Paciente paciente = buscarPacientePorId(id);
        return toDTO(paciente);
    }

    public PacienteResponseDTO buscarPorDocumento(String documento) {
        Paciente paciente = pacienteRepository.findByDocumento(documento)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        return toDTO(paciente);
    }

    public PacienteResponseDTO atualizar(Long id, PacienteDTO dto) {
        Paciente paciente = buscarPacientePorId(id);

        validarDocumentoUpdate(id, dto);

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("Data de nascimento não pode ser futura");
        }

        String descricaoLog = AuditoriaDescricaoUtil.pacienteAtualizado(paciente, dto);

        paciente.setNomeCompleto(dto.getNomeCompleto());
        paciente.setTelefone(dto.getTelefone());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setDocumento(dto.getDocumento());
        paciente.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), paciente.getEndereco());

        geocodingService.preencherCoordenadas(paciente.getEndereco());

        if(paciente.getEndereco().getLatitude() == null || paciente.getEndereco().getLongitude() == null){
            throw new BusinessException("Unidade de Saúde não encontrada para esse endereço");
        }

        UnidadeSaude unidade = territorializacaoService.buscarUbsPorCoordenada(
                paciente.getEndereco().getLatitude(),
                paciente.getEndereco().getLongitude());

        if(unidade == null){
            throw new BusinessException("Unidade de Saúde não encontrada para esse endereço");
        }

        paciente.setUnidadeSaude(unidade);

        Paciente atualizado = pacienteRepository.save(paciente);

        auditoriaFacade.pacienteAtualizado(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Paciente paciente = buscarPacientePorId(id);

        Long pacienteId = paciente.getId();

        pacienteRepository.delete(paciente);

        auditoriaFacade.pacienteDeletado(pacienteId);
    }

    private void validarDocumentoCreate(PacienteDTO dto) {
        if (pacienteRepository.existsByDocumento(dto.getDocumento())) {
            throw new ConflictException("CPF já cadastrado");
        }
    }

    private void validarDocumentoUpdate(Long id, PacienteDTO dto) {
        if (pacienteRepository.existsByDocumentoAndIdNot(dto.getDocumento(), id)) {
            throw new ConflictException("CPF já cadastrado");
        }
    }

    private Paciente buscarPacientePorId(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    private Paciente toEntity(PacienteDTO dto) {
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("Data de nascimento não pode ser futura");
        }

        Paciente entity = new Paciente();
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setTelefone(dto.getTelefone());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setDocumento(dto.getDocumento());
        entity.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);
        entity.setEndereco(endereco);
        entity.setIdUsuarioCadastro(dto.getIdUsuarioCadastro());

        geocodingService.preencherCoordenadas(entity.getEndereco());

        if(entity.getEndereco().getLatitude() == null || entity.getEndereco().getLongitude() == null){
            throw new BusinessException("Unidade de Saúde não encontrada para esse endereço");
        }

        UnidadeSaude unidade = territorializacaoService.buscarUbsPorCoordenada(
                entity.getEndereco().getLatitude(),
                entity.getEndereco().getLongitude());

        if(unidade == null){
            throw new BusinessException("Unidade de Saúde não encontrada para esse endereço");
        }
        entity.setUnidadeSaude(unidade);

        return entity;
    }

    private PacienteResponseDTO toDTO(Paciente entity) {

        PacienteResponseDTO dto = new PacienteResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setDocumento(entity.getDocumento());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        dto.setUnidadeSaudeNome(entity.getUnidadeSaude().getNome());
        dto.setSexo(entity.getSexo());
        dto.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());

        return dto;
    }

    private PacienteShortResponseDTO toShortDTO(Paciente entity) {

        PacienteShortResponseDTO dto = new PacienteShortResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setDocumento(entity.getDocumento());
        dto.setUnidadeSaudeNome(entity.getUnidadeSaude().getNome());
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());

        return dto;
    }
}