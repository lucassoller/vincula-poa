package com.vincula.service;

import com.vincula.dto.DemandaDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.StatusDemanda;
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
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        UnidadeSaude unidadeSaude = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));

        Usuario usuarioCriador = usuarioService.buscarUsuarioLogado();

        Demanda entity = new Demanda();
        entity.setPaciente(paciente);
        entity.setUnidadeSaude(unidadeSaude);
        entity.setUsuarioCriador(usuarioCriador);
        entity.setMotivo(dto.getMotivo());
        entity.setStatus(StatusDemanda.ABERTA);
        entity.setDataHoraCriacao(LocalDateTime.now());
        entity.setDataHoraFinalizacao(null);

        Demanda salvo = demandaRepository.save(entity);
        return toDTO(salvo);
    }

    public DemandaDTO atualizar(Long id, DemandaDTO dto) {

        Demanda entity = demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));

        UnidadeSaude unidadeSaude = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));

        entity.setMotivo(dto.getMotivo());
        entity.setUnidadeSaude(unidadeSaude);

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
        Demanda entity = demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));

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

    public DemandaDTO atualizarStatus(Long id, StatusDemanda status) {
        Demanda entity = demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));

        entity.setStatus(status);

        if(status.equals(StatusDemanda.FINALIZADA)){
            entity.setDataHoraFinalizacao(LocalDateTime.now());
        }

        Demanda atualizado = demandaRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Demanda entity = demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));

        demandaRepository.delete(entity);
    }

    private DemandaDTO toDTO(Demanda entity) {
        DemandaDTO dto = new DemandaDTO();

        dto.setId(entity.getId());
        dto.setPacienteId(entity.getPaciente().getId());
        dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        dto.setMotivo(entity.getMotivo());
        dto.setStatus(entity.getStatus());
        dto.setDataHoraCriacao(entity.getDataHoraCriacao());
        dto.setDataHoraFinalizacao(entity.getDataHoraFinalizacao());
        dto.setUsuarioCriadorId(entity.getUsuarioCriador().getId());
        dto.setUsuarioCriadorNome(entity.getUsuarioCriador().getNome());

        return dto;
    }
}