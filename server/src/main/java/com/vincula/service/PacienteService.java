package com.vincula.service;

import com.vincula.dto.PacienteDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.Sexo;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;


@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaService auditoriaService;

    public PacienteService(PacienteRepository pacienteRepository, UnidadeSaudeRepository unidadeSaudeRepository, EnderecoMapper enderecoMapper, AuditoriaService auditoriaService) {
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaService = auditoriaService;
    }

    public PacienteDTO criar(PacienteDTO dto) {
        validarCpfECnsCreate(dto);

        Paciente entity = toEntity(dto);

        Paciente salvo = pacienteRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.PACIENTE_CRIADO,
                "Paciente",
                salvo.getId(),
                "Paciente criado: " + salvo.getNomeCompleto()
        );
        return toDTO(salvo);
    }

    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PacienteDTO buscarPorId(Long id) {
        Paciente paciente = buscarPacientePorId(id);

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCns(String cns) {
        Paciente paciente = pacienteRepository.findByCns(cns)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        return toDTO(paciente);
    }

    public PacienteDTO atualizar(Long id, PacienteDTO dto) {
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

        auditoriaService.registrar(
                TipoAcaoAuditoria.PACIENTE_ATUALIZADO,
                "Paciente",
                atualizado.getId(),
                descricaoLog
        );

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Paciente paciente = buscarPacientePorId(id);

        Long pacienteId = paciente.getId();

        pacienteRepository.delete(paciente);

        auditoriaService.registrar(
                TipoAcaoAuditoria.PACIENTE_DELETADO,
                "Paciente",
                pacienteId,
                "Paciente deletado"
        );
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

    private PacienteDTO toDTO(Paciente entity) {

        PacienteDTO dto = new PacienteDTO();

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