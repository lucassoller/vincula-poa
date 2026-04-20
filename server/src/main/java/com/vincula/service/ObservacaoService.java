package com.vincula.service;

import com.vincula.dto.ObservacaoDTO;
import com.vincula.entity.Observacao;
import com.vincula.entity.Paciente;
import com.vincula.entity.Usuario;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ObservacaoRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ObservacaoService {

    private final ObservacaoRepository observacaoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ObservacaoService(ObservacaoRepository observacaoRepository,
                             PacienteRepository pacienteRepository,
                             UsuarioRepository usuarioRepository) {
        this.observacaoRepository = observacaoRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ObservacaoDTO criar(ObservacaoDTO dto) {

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Observacao entity = new Observacao();
        entity.setDescricao(dto.getDescricao());
        entity.setDataHora(LocalDateTime.now());
        entity.setPaciente(paciente);
        entity.setUsuario(usuario);

        Observacao salvo = observacaoRepository.save(entity);
        return toDTO(salvo);
    }

    public List<ObservacaoDTO> listarTodas() {
        return observacaoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ObservacaoDTO> listarPorPaciente(Long pacienteId) {
        return observacaoRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ObservacaoDTO> listarPorUsuario(Long usuarioId) {
        return observacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ObservacaoDTO buscarPorId(Long id) {
        Observacao entity = observacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Observação não encontrada"));

        return toDTO(entity);
    }

    public ObservacaoDTO atualizar(Long id, ObservacaoDTO dto) {
        Observacao entity = observacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Observação não encontrada"));

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        entity.setDescricao(dto.getDescricao());
        entity.setPaciente(paciente);
        entity.setUsuario(usuario);

        Observacao atualizado = observacaoRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Observacao entity = observacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Observação não encontrada"));

        observacaoRepository.delete(entity);
    }

    private ObservacaoDTO toDTO(Observacao entity) {
        ObservacaoDTO dto = new ObservacaoDTO();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setDataHora(entity.getDataHora());
        dto.setPacienteId(entity.getPaciente().getId());
        dto.setUsuarioId(entity.getUsuario().getId());
        return dto;
    }
}