package com.vincula.service;

import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaFacade auditoriaFacade;

    public DemandaService(DemandaRepository demandaRepository,
                          PacienteRepository pacienteRepository,
                          UnidadeSaudeRepository unidadeSaudeRepository,
                          UsuarioService usuarioService,
                          AuditoriaFacade auditoriaFacade) {
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioService = usuarioService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public DemandaResponseDTO criar(DemandaDTO dto) {
        Demanda entity = toEntity(dto);

        Demanda salvo = demandaRepository.save(entity);

        auditoriaFacade.demandaCriada(salvo.getId(), salvo.getPaciente().getId());

        return toDTO(salvo);
    }

    public DemandaResponseDTO atualizar(Long id, DemandaDTO dto) {
        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar uma demanda finalizada");
        }

        validarMotivoBusca(dto);

        String descricaoLog = AuditoriaDescricaoUtil.demandaAtualizada(entity, dto);

        entity.setMotivoBuscaAtiva(dto.getMotivoBuscaAtiva());
        entity.setDescricaoBusca(dto.getDescricaoBusca());
        entity.setPrazoDemanda(dto.getPrazoDemanda());
        entity.setDataHoraLimite(calcularDataLimite(entity.getDataHoraCriacao(), dto.getPrazoDemanda()));

        Demanda atualizado = demandaRepository.save(entity);

        auditoriaFacade.demandaAtualizada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public DemandaResponseDTO encerrar(Long id, EncerrarDemandaDTO dto) {
        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Demanda já está finalizada");
        }

        if (dto.getDesfechoDemanda() == null) {
            throw new BusinessException("Desfecho é obrigatório");
        }

        String descricaoLog = AuditoriaDescricaoUtil.demandaEncerrada(entity);

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        entity.setStatus(StatusDemanda.FINALIZADA);
        entity.setDesfecho(dto.getDesfechoDemanda());
        entity.setDescricaoDesfecho(dto.getDescricaoDesfecho());
        entity.setDataHoraFinalizacao(LocalDateTime.now());
        entity.setUsuarioEncerramento(usuario);

        Demanda atualizado = demandaRepository.save(entity);

        auditoriaFacade.demandaEncerrada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public DemandaResponseDTO redirecionar(Long id, RedirecionarDemandaDTO dto) {
        Demanda demanda = buscarDemandaPorId(id);

        if (demanda.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível redirecionar uma demanda finalizada");
        }

        UnidadeSaude novaUnidade = buscarUnidadeSaudePorId(dto.getNovaUnidadeResponsavelId());

        if (demanda.getUnidadeResponsavel().getId().equals(novaUnidade.getId())) {
            throw new BusinessException("A nova unidade responsável deve ser diferente da atual");
        }

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        demanda.setUnidadeResponsavelAnterior(demanda.getUnidadeResponsavel());
        demanda.setUnidadeResponsavel(novaUnidade);
        demanda.setFoiRedirecionada(true);
        demanda.setMotivoRedirecionamento(dto.getMotivoRedirecionamento());
        demanda.setDataHoraRedirecionamento(LocalDateTime.now());
        demanda.setUsuarioRedirecionamento(usuario);

        Demanda atualizada = demandaRepository.save(demanda);

        String descricaoLog = AuditoriaDescricaoUtil.demandaRedirecionada(atualizada);

        auditoriaFacade.demandaRedirecionada(atualizada.getId(), descricaoLog);

        return toDTO(atualizada);
    }

    public List<DemandaResponseDTO> listarTodas() {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public DemandaResponseDTO buscarPorId(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        auditoriaFacade.demandaVisualizada(entity.getId());

        return toDTO(entity);
    }

    public List<DemandaResponseDTO> listarPorPaciente(Long pacienteId) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaResponseDTO> listarPorUnidadeSaude(Long unidadeResponsavelId) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByUnidadeResponsavelId(unidadeResponsavelId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaResponseDTO> listarPorUsuarioCriador(Long usuarioId) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByUsuarioCriadorId(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaResponseDTO> listarPorPacienteEStatus(Long pacienteId, StatusDemanda status) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByPacienteIdAndStatus(pacienteId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaResponseDTO> listarPorUnidadeSaudeEStatus(Long unidadeResponsavelId, StatusDemanda status) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByUnidadeResponsavelIdAndStatus(unidadeResponsavelId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandaResponseDTO> listarPorUsuarioCriadorEStatus(Long usuarioId, StatusDemanda status) {
        auditoriaFacade.demandaVisualizada(0L);
        return demandaRepository.findByUsuarioCriadorIdAndStatus(usuarioId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void deletar(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        Long demandaId = entity.getId();

        demandaRepository.delete(entity);
        auditoriaFacade.demandaDeletada(demandaId);
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

    private void validarMotivoBusca(DemandaDTO dto) {
        if (dto.getMotivoBuscaAtiva() == MotivoBuscaAtiva.OUTRO &&
                (dto.getDescricaoBusca() == null || dto.getDescricaoBusca().isBlank())) {
            throw new BusinessException("Descrição da busca é obrigatória quando o motivo for OUTRO");
        }
    }

    private Demanda toEntity(DemandaDTO dto) {
        Paciente paciente = buscarPacientePorId(dto.getPacienteId());

        UnidadeSaude unidadeResponsavel = buscarUnidadeSaudePorId(dto.getUnidadeResponsavelId());

        Usuario usuarioCriador = usuarioService.buscarUsuarioAutenticado();

        validarMotivoBusca(dto);

        Demanda entity = new Demanda();
        entity.setPaciente(paciente);
        entity.setUsuarioCriador(usuarioCriador);
        entity.setUnidadeSolicitante(usuarioCriador.getUnidadeSaude());
        entity.setUnidadeResponsavel(unidadeResponsavel);
        entity.setMotivoBuscaAtiva(dto.getMotivoBuscaAtiva());
        entity.setDescricaoBusca(dto.getDescricaoBusca());
        entity.setPrazoDemanda(dto.getPrazoDemanda());
        entity.setStatus(StatusDemanda.ABERTA);
        entity.setDataHoraCriacao(LocalDateTime.now());
        entity.setDataHoraLimite(calcularDataLimite(entity.getDataHoraCriacao(), dto.getPrazoDemanda()));

        return entity;
    }

    private DemandaResponseDTO toDTO(Demanda entity) {
        DemandaResponseDTO dto = new DemandaResponseDTO();

        dto.setId(entity.getId());
        dto.setPacienteId(entity.getPaciente().getId());

        if (entity.getUnidadeSolicitante() != null) {
            dto.setUnidadeSolicitanteId(entity.getUnidadeSolicitante().getId());
        }

        dto.setUnidadeResponsavelId(entity.getUnidadeResponsavel().getId());
        dto.setMotivoBuscaAtiva(entity.getMotivoBuscaAtiva());
        dto.setDescricaoBusca(entity.getDescricaoBusca());
        dto.setPrazoDemanda(entity.getPrazoDemanda());
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

        dto.setFoiRedirecionada(entity.getFoiRedirecionada());

        if (entity.getUnidadeResponsavelAnterior() != null) {
            dto.setUnidadeResponsavelAnteriorId(entity.getUnidadeResponsavelAnterior().getId());
        }

        dto.setMotivoRedirecionamento(entity.getMotivoRedirecionamento());
        dto.setDataHoraRedirecionamento(entity.getDataHoraRedirecionamento());

        if (entity.getUsuarioRedirecionamento() != null) {
            dto.setUsuarioRedirecionamentoId(entity.getUsuarioRedirecionamento().getId());
            dto.setUsuarioRedirecionamentoNome(entity.getUsuarioRedirecionamento().getNome());
        }

        return dto;
    }

    private LocalDateTime calcularDataLimite(LocalDateTime inicio, PrazoDemanda prazo) {
        return switch (prazo) {
            case D1 -> inicio.plusDays(1);
            case D2 -> inicio.plusDays(2);
            case D3 -> inicio.plusDays(3);
            case D7 -> inicio.plusDays(7);
            case D15 -> inicio.plusDays(15);
            case D20 -> inicio.plusDays(20);
            case D30 -> inicio.plusDays(30);
        };
    }
}