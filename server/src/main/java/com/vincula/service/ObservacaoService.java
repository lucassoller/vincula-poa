package com.vincula.service;

import com.vincula.dto.observacao.ObservacaoDTO;
import com.vincula.dto.observacao.ObservacaoResponseDTO;
import com.vincula.entity.Observacao;
import com.vincula.entity.Paciente;
import com.vincula.entity.Usuario;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ObservacaoRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ObservacaoService {

    private final ObservacaoRepository observacaoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaFacade auditoriaFacade;

    public ObservacaoService(ObservacaoRepository observacaoRepository,
                             PacienteRepository pacienteRepository,
                             UsuarioService usuarioService,
                             AuditoriaFacade auditoriaFacade) {
        this.observacaoRepository = observacaoRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioService = usuarioService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public ObservacaoResponseDTO criar(ObservacaoDTO dto) {

        Observacao entity = toEntity(dto);

        Observacao salvo = observacaoRepository.save(entity);

        auditoriaFacade.observacaoCriada(salvo.getId(), salvo.getPaciente().getId());

        return toDTO(salvo);
    }

    public List<ObservacaoResponseDTO> listarTodas() {
        auditoriaFacade.observacaoVisualizada(0L);
        return observacaoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ObservacaoResponseDTO> listarPorPaciente(Long pacienteId) {
        auditoriaFacade.observacaoVisualizada(0L);
        return observacaoRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ObservacaoResponseDTO> listarPorUsuario(Long usuarioId) {
        auditoriaFacade.observacaoVisualizada(0L);
        return observacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ObservacaoResponseDTO buscarPorId(Long id) {
        Observacao entity = buscarObservacaoPorId(id);

        auditoriaFacade.observacaoVisualizada(entity.getId());

        return toDTO(entity);
    }

    public ObservacaoResponseDTO atualizar(Long id, ObservacaoDTO dto) {
        Observacao entity = buscarObservacaoPorId(id);

        Paciente paciente = buscarPacientePorId(dto.getPacienteId());

        String descricaoLog = AuditoriaDescricaoUtil.observacaoAtualizada(entity, dto);

        entity.setDescricao(dto.getDescricao());
        entity.setPaciente(paciente);

        Observacao atualizado = observacaoRepository.save(entity);

        auditoriaFacade.observacaoAtualizada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Observacao entity = buscarObservacaoPorId(id);

        Long observacaoId = entity.getId();

        observacaoRepository.delete(entity);

        auditoriaFacade.observacaoDeletada(observacaoId);
    }

    private Observacao buscarObservacaoPorId(Long id) {
        return observacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Observação não encontrada"));
    }

    private Paciente buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    private Observacao toEntity(ObservacaoDTO dto){
        Paciente paciente = buscarPacientePorId(dto.getPacienteId());

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        Observacao entity = new Observacao();
        entity.setDescricao(dto.getDescricao());
        entity.setDataHora(LocalDateTime.now());
        entity.setPaciente(paciente);
        entity.setUsuario(usuario);
        return entity;
    }

    private ObservacaoResponseDTO toDTO(Observacao entity) {
        ObservacaoResponseDTO dto = new ObservacaoResponseDTO();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setDataHora(entity.getDataHora());
        dto.setPacienteId(entity.getPaciente().getId());
        dto.setUsuarioId(entity.getUsuario().getId());
        dto.setUsuarioNome(entity.getUsuario().getNome());
        return dto;
    }
}