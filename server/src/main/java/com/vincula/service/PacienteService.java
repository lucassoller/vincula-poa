package com.vincula.service;

import com.vincula.dto.paciente.PacienteDTO;
import com.vincula.dto.paciente.PacienteResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.Sexo;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;

    public PacienteService(PacienteRepository pacienteRepository,
                           UnidadeSaudeRepository unidadeSaudeRepository,
                           EnderecoMapper enderecoMapper,
                           AuditoriaFacade auditoriaFacade) {
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    public PacienteResponseDTO criar(PacienteDTO dto) {
        validarCpfECnsCreate(dto);

        Paciente entity = toEntity(dto);

        Paciente salvo = pacienteRepository.save(entity);

        auditoriaFacade.pacienteCriado(salvo.getId());

        return toDTO(salvo);
    }

    public List<PacienteResponseDTO> listarTodos() {
        auditoriaFacade.pacienteVisualizado(0L);
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PacienteResponseDTO buscarPorId(Long id) {
        Paciente paciente = buscarPacientePorId(id);

        auditoriaFacade.pacienteVisualizado(paciente.getId());

        return toDTO(paciente);
    }

    public PacienteResponseDTO buscarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        auditoriaFacade.pacienteVisualizado(paciente.getId());

        return toDTO(paciente);
    }

    public PacienteResponseDTO buscarPorCns(String cns) {
        Paciente paciente = pacienteRepository.findByCns(cns)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        auditoriaFacade.pacienteVisualizado(paciente.getId());

        return toDTO(paciente);
    }

    public PacienteResponseDTO atualizar(Long id, PacienteDTO dto) {
        Paciente paciente = buscarPacientePorId(id);

        validarCpfECnsUpdate(id, dto);

        UnidadeSaude unidadeSaude = buscarUnidadeSaudePorId(dto.getUnidadeSaudeId());

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("Data de nascimento não pode ser futura");
        }

        String descricaoLog = AuditoriaDescricaoUtil.pacienteAtualizado(paciente, dto);

        paciente.setUnidadeSaude(unidadeSaude);
        paciente.setNomeCompleto(dto.getNomeCompleto());
        paciente.setTelefone(dto.getTelefone());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setCpf(dto.getCpf());
        paciente.setCns(dto.getCns());
        paciente.setEmail(dto.getEmail());
        paciente.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), paciente.getEndereco());

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

    private void validarCpfECnsCreate(PacienteDTO dto) {
        if ((dto.getCpf() == null || dto.getCpf().isBlank()) &&
                (dto.getCns() == null || dto.getCns().isBlank())) {
            throw new BusinessException("Paciente deve informar CPF ou CNS");
        }

        if (pacienteRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF já cadastrado");
        }

        if (pacienteRepository.existsByCns(dto.getCns())) {
            throw new ConflictException("CNS já cadastrado");
        }
    }

    private void validarCpfECnsUpdate(Long id, PacienteDTO dto) {
        if ((dto.getCpf() == null || dto.getCpf().isBlank()) &&
                (dto.getCns() == null || dto.getCns().isBlank())) {
            throw new BusinessException("Paciente deve informar CPF ou CNS");
        }

        if (pacienteRepository.existsByCpfAndIdNot(dto.getCpf(), id)) {
            throw new ConflictException("CPF já cadastrado");
        }

        if (pacienteRepository.existsByCnsAndIdNot(dto.getCns(), id)) {
            throw new ConflictException("CNS já cadastrado");
        }
    }

    private Paciente buscarPacientePorId(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    private UnidadeSaude buscarUnidadeSaudePorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    private Paciente toEntity(PacienteDTO dto) {
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        UnidadeSaude unidadeSaude = buscarUnidadeSaudePorId(dto.getUnidadeSaudeId());

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(ChronoLocalDate.from(LocalDate.now()))) {
            throw new BusinessException("Data de nascimento não pode ser futura");
        }

        Paciente entity = new Paciente();
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setTelefone(dto.getTelefone());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setCpf(dto.getCpf());
        entity.setCns(dto.getCns());
        entity.setEndereco(endereco);
        entity.setUnidadeSaude(unidadeSaude);
        entity.setEmail(dto.getEmail());
        entity.setSexo(dto.getSexo() != null ? dto.getSexo() : Sexo.NAO_INFORMADO);

        return entity;
    }

    private PacienteResponseDTO toDTO(Paciente entity) {

        PacienteResponseDTO dto = new PacienteResponseDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setCpf(entity.getCpf());
        dto.setCns(entity.getCns());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        dto.setEmail(entity.getEmail());
        dto.setSexo(entity.getSexo());

        return dto;
    }
}