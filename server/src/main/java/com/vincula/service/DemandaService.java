package com.vincula.service;

import com.vincula.dto.DemandaDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioService usuarioService;

    public DemandaService(DemandaRepository demandaRepository,
                          PacienteRepository pacienteRepository,
                          UnidadeSaudeRepository unidadeSaudeRepository,
                          UsuarioService usuarioService) {
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioService = usuarioService;
    }

    public DemandaDTO criar(DemandaDTO dto) {
        Demanda entity = toEntity(dto);

        Demanda salvo = demandaRepository.save(entity);
        return toDTO(salvo);
    }

    public DemandaDTO atualizar(Long id, DemandaDTO dto) {

        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar uma demanda finalizada");
        }

        UnidadeSaude unidadeSaude = buscarUnidadeSaudePorId(dto.getUnidadeSaudeId());

        entity.setMotivo(dto.getMotivo());
        entity.setUnidadeSaude(unidadeSaude);

        Demanda atualizado = demandaRepository.save(entity);
        return toDTO(atualizado);
    }

    public DemandaDTO encerrar(Long id, DesfechoDemanda desfecho, String descricao) {

        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Demanda já está finalizada");
        }

        if (desfecho == null) {
            throw new BusinessException("Desfecho é obrigatório");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new BusinessException("Descrição é obrigatória para desfecho");
        }

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        entity.setStatus(StatusDemanda.FINALIZADA);
        entity.setDesfecho(desfecho);
        entity.setDescricaoDesfecho(descricao);
        entity.setDataHoraFinalizacao(LocalDateTime.now());
        entity.setUsuarioEncerramento(usuario);

        Demanda atualizado = demandaRepository.save(entity);
        return toDTO(atualizado);
    }

    public List<DemandaDTO> listarTodas() {
        return demandaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public DemandaDTO buscarPorId(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        return toDTO(entity);
    }

    public List<DemandaDTO> listarPorPaciente(Long pacienteId) {
        return demandaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaDTO> listarPorUnidadeSaude(Long unidadeSaudeId) {
        return demandaRepository.findByUnidadeSaudeId(unidadeSaudeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaDTO> listarPorUsuarioCriador(Long usuarioId) {
        return demandaRepository.findByUsuarioCriadorId(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaDTO> listarPorPacienteEStatus(Long pacienteId, StatusDemanda status) {
        return demandaRepository.findByPacienteIdAndStatus(pacienteId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaDTO> listarPorUnidadeSaudeEStatus(Long unidadeSaudeId, StatusDemanda status) {
        return demandaRepository.findByUnidadeSaudeIdAndStatus(unidadeSaudeId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaDTO> listarPorUsuarioCriadorEStatus(Long usuarioId, StatusDemanda status) {
        return demandaRepository.findByUsuarioCriadorIdAndStatus(usuarioId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void deletar(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        demandaRepository.delete(entity);
    }

    private Demanda buscarDemandaPorId(Long id) {
        return demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));
    }

    private Paciente buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    private UnidadeSaude buscarUnidadeSaudePorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    private Demanda toEntity(DemandaDTO dto) {
        Paciente paciente = buscarPacientePorId(dto.getPacienteId());

        UnidadeSaude unidadeSaude = buscarUnidadeSaudePorId(dto.getUnidadeSaudeId());

        Usuario usuarioCriador = usuarioService.buscarUsuarioAutenticado();

        Demanda entity = new Demanda();
        entity.setPaciente(paciente);
        entity.setUnidadeSaude(unidadeSaude);
        entity.setUsuarioCriador(usuarioCriador);
        entity.setMotivo(dto.getMotivo());
        entity.setStatus(StatusDemanda.ABERTA);
        entity.setDataHoraCriacao(LocalDateTime.now());
        entity.setPaciente(paciente);

        return entity;
    }

    private DemandaDTO toDTO(Demanda entity) {
        DemandaDTO dto = new DemandaDTO();

        dto.setId(entity.getId());
        dto.setPacienteId(entity.getPaciente().getId());
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        dto.setMotivo(entity.getMotivo());
        dto.setStatus(entity.getStatus());
        dto.setDataHoraCriacao(entity.getDataHoraCriacao());
        dto.setUsuarioCriadorId(entity.getUsuarioCriador().getId());
        dto.setUsuarioCriadorNome(entity.getUsuarioCriador().getNome());

        dto.setDesfecho(entity.getDesfecho());
        dto.setDescricaoDesfecho(entity.getDescricaoDesfecho());
        dto.setDataHoraFinalizacao(entity.getDataHoraFinalizacao());

        if (entity.getUsuarioEncerramento() != null) {
            dto.setUsuarioEncerramentoId(entity.getUsuarioEncerramento().getId());
            dto.setUsuarioEncerramentoNome(entity.getUsuarioEncerramento().getNome());
        }

        return dto;
    }
}