package com.vincula.service;

import com.vincula.dto.DemandaDTO;
import com.vincula.dto.RedirecionarDemandaDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.StatusDemanda;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public DemandaService(DemandaRepository demandaRepository,
                          PacienteRepository pacienteRepository,
                          UnidadeSaudeRepository unidadeSaudeRepository,
                          UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    public DemandaDTO criar(DemandaDTO dto) {
        Demanda entity = toEntity(dto);

        Demanda salvo = demandaRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.DEMANDA_CRIADA,
                "Demanda",
                salvo.getId(),
                "Demanda criada para o paciente ID " + salvo.getPaciente().getId()
        );
        return toDTO(salvo);
    }

    public DemandaDTO atualizar(Long id, DemandaDTO dto) {
        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar uma demanda finalizada");
        }

        validarMotivoBusca(dto);

        String descricaoLog = AuditoriaDescricaoUtil.demandaAtualizada(entity, dto);

        entity.setMotivoBuscaAtiva(dto.getMotivoBuscaAtiva());
        entity.setDescricaoBusca(dto.getDescricaoBusca());
        entity.setPrazoDemanda(dto.getPrazoDemanda());

        Demanda atualizado = demandaRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.DEMANDA_ATUALIZADA,
                "Demanda",
                atualizado.getId(),
                descricaoLog
        );
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

        if (desfecho == DesfechoDemanda.OUTRO &&
                (descricao == null || descricao.isBlank())) {
            throw new BusinessException("Descrição do desfecho é obrigatória quando o desfecho for OUTRO");
        }

        String descricaoLog = AuditoriaDescricaoUtil.demandaEncerrada(entity);

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        entity.setStatus(StatusDemanda.FINALIZADA);
        entity.setDesfecho(desfecho);
        entity.setDescricaoDesfecho(descricao);
        entity.setDataHoraFinalizacao(LocalDateTime.now());
        entity.setUsuarioEncerramento(usuario);

        Demanda atualizado = demandaRepository.save(entity);
        auditoriaService.registrar(
                TipoAcaoAuditoria.DEMANDA_ENCERRADA,
                "Demanda",
                atualizado.getId(),
                descricaoLog
        );
        return toDTO(atualizado);
    }

    public DemandaDTO redirecionar(Long id, RedirecionarDemandaDTO dto) {
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

        auditoriaService.registrar(
                TipoAcaoAuditoria.DEMANDA_REDIRECIONADA,
                "Demanda",
                atualizada.getId(),
                descricaoLog
        );
        return toDTO(atualizada);
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

    public List<DemandaDTO> listarPorUnidadeSaude(Long unidadeResponsavelId) {
        return demandaRepository.findByUnidadeResponsavelId(unidadeResponsavelId)
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

    public List<DemandaDTO> listarPorUnidadeSaudeEStatus(Long unidadeResponsavelId, StatusDemanda status) {
        return demandaRepository.findByUnidadeResponsavelIdAndStatus(unidadeResponsavelId, status)
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

        return entity;
    }

    private DemandaDTO toDTO(Demanda entity) {
        DemandaDTO dto = new DemandaDTO();

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
}