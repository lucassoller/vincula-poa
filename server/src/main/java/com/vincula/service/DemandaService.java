package com.vincula.service;

import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.*;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.export.DemandaExporter;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final DemandaExporter demandaExporter;

    public DemandaService(DemandaRepository demandaRepository,
                          PacienteRepository pacienteRepository,
                          UnidadeSaudeRepository unidadeSaudeRepository,
                          UsuarioService usuarioService,
                          AuditoriaFacade auditoriaFacade, DemandaExporter demandaExporter) {
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.usuarioService = usuarioService;
        this.auditoriaFacade = auditoriaFacade;
        this.demandaExporter = demandaExporter;
    }

    public DemandaResponseDTO criar(DemandaDTO dto) {
        Demanda entity = toEntity(dto);

        Demanda salvo = demandaRepository.save(entity);

        auditoriaFacade.demandaCriada(salvo.getId(), salvo.getPaciente().getId());

        return toDTO(salvo);
    }

    public Page<DemandaResponseDTO> listarTodas(Pageable pageable) {
        return demandaRepository.findAllOrderByPacienteNome(pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarTodasFiltradas(String filtro, Pageable pageable) {
        return demandaRepository.findFiltradas(filtro, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        return demandaRepository.findByPacienteOrderByPacienteNome(pacienteId, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorUnidadeSaude(Long unidadeResponsavelId, Pageable pageable) {
        return demandaRepository.findByUnidadeOrderByPacienteNome(unidadeResponsavelId, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorUnidadeSaudeFiltradas(Long unidadeResponsavelId, String filtro, Pageable pageable) {
        return demandaRepository.findFiltradasByUnidade(unidadeResponsavelId, filtro, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorUsuarioCriador(Long usuarioId, Pageable pageable) {
        return demandaRepository.findByUsuarioOrderByPacienteNome(usuarioId, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorUsuarioCriadorFiltradas(Long usuarioId, String filtro, Pageable pageable) {
        return demandaRepository.findFiltradasByUsuarioCriador(usuarioId, filtro, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarPorStatus(StatusDemanda status, Pageable pageable) {
        return demandaRepository.findByStatusOrderByPacienteNome(status, pageable)
                .map(this::toDTO);
    }

    public DemandaResponseDTO atualizar(Long id, DemandaDTO dto) {
        Demanda entity = buscarDemandaPorId(id);

        if (entity.getStatus() == StatusDemanda.FINALIZADA) {
            throw new BusinessException("Não é possível alterar uma demanda finalizada");
        }

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

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        entity.setStatus(StatusDemanda.FINALIZADA);
        entity.setDesfecho(dto.getDesfechoDemanda());
        entity.setDescricaoDesfecho(dto.getDescricaoDesfecho());
        entity.setDataHoraFinalizacao(LocalDateTime.now());
        entity.setUsuarioEncerramento(usuario);

        Demanda atualizado = demandaRepository.save(entity);

        String descricaoLog = AuditoriaDescricaoUtil.demandaEncerrada(entity);
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

    public void redirecionarDemandasAbertasDoPaciente(Long pacienteId, RedirecionarDemandaDTO dto) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        List<Demanda> demandas = demandaRepository
                .findByPacienteIdAndStatusIn(
                        pacienteId,
                        List.of(StatusDemanda.ABERTA, StatusDemanda.EM_ANDAMENTO)
                );

        for (Demanda demanda : demandas) {
            if (!demanda.getUnidadeResponsavel().getId().equals(paciente.getUnidadeSaude().getId())) {
                demanda.setUnidadeResponsavelAnterior(demanda.getUnidadeResponsavel());
                demanda.setUnidadeResponsavel(paciente.getUnidadeSaude());
                demanda.setFoiRedirecionada(true);
                demanda.setMotivoRedirecionamento(dto.getMotivoRedirecionamento());
                demanda.setUsuarioRedirecionamento(usuario);
                demanda.setDataHoraRedirecionamento(LocalDateTime.now());
            }
        }

        demandaRepository.saveAll(demandas);
    }

    public DemandaResponseDTO buscarPorId(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        return toDTO(entity);
    }

    public void deletar(Long id) {
        Demanda entity = buscarDemandaPorId(id);

        Long demandaId = entity.getId();

        demandaRepository.delete(entity);
        auditoriaFacade.demandaDeletada(demandaId);
    }

    public String exportarDemandasCsv(){
        List<Demanda> demandas = demandaRepository.findAllOrderByPacienteNome();
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas exportadas");
        return demandaExporter.exportar(demandas);
    }

    public String exportarDemandasFiltradasCsv(String filtro){
        List<Demanda> demandas = demandaRepository.findFiltradas(filtro);
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas exportadas");
        return demandaExporter.exportar(demandas);
    }

    public String exportarDemandasPorUnidadeCsv(Long unidadeId){
        List<Demanda> demandas = demandaRepository.findByUnidadeOrderByPacienteNome(unidadeId);
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas da unidade " + unidadeId + " exportadas");
        return demandaExporter.exportar(demandas);
    }

    public String exportarDemandasFiltradasPorUnidadeCsv(Long unidadeId, String filtro){
        List<Demanda> demandas = demandaRepository.findFiltradasByUnidade(unidadeId, filtro);
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas da unidade " + unidadeId + " exportadas");
        return demandaExporter.exportar(demandas);
    }

    public String exportarDemandasPorUsuarioCsv(Long usuarioCriadorId){
        List<Demanda> demandas = demandaRepository.findByUsuarioOrderByPacienteNome(usuarioCriadorId);
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas do usuário criador " + usuarioCriadorId + " exportadas");
        return demandaExporter.exportar(demandas);
    }

    public String exportarDemandasFiltradasPorUsuarioCsv(Long usuarioCriadorId, String filtro){
        List<Demanda> demandas = demandaRepository.findFiltradasByUsuarioCriador(usuarioCriadorId, filtro);
        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas do usuário criador " + usuarioCriadorId + " exportadas");
        return demandaExporter.exportar(demandas);
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

        Usuario usuarioCriador = usuarioService.buscarUsuarioAutenticado();

        Demanda entity = new Demanda();
        entity.setPaciente(paciente);
        entity.setUsuarioCriador(usuarioCriador);

        if(usuarioCriador.getUnidadeSaude() != null){
            entity.setUnidadeSolicitante(usuarioCriador.getUnidadeSaude());
        }

        entity.setUnidadeResponsavel(paciente.getUnidadeSaude());
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
        dto.setPacienteNome(entity.getPaciente().getNomeCompleto());

        if (entity.getUnidadeSolicitante() != null) {
            dto.setUnidadeSolicitanteId(entity.getUnidadeSolicitante().getId());
            dto.setUnidadeSolicitanteNome(entity.getUnidadeSolicitante().getNome());
        }

        dto.setUnidadeResponsavelId(entity.getUnidadeResponsavel().getId());
        dto.setUnidadeResponsavelNome(entity.getUnidadeResponsavel().getNome());

        dto.setMotivoBuscaAtiva(entity.getMotivoBuscaAtiva());
        dto.setDescricaoBusca(entity.getDescricaoBusca());
        dto.setPrazoDemanda(entity.getPrazoDemanda());
        dto.setStatus(entity.getStatus());

        dto.setDataHoraCriacao(entity.getDataHoraCriacao());
        dto.setDataHoraLimite(entity.getDataHoraLimite());

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
            dto.setUnidadeResponsavelAnteriorNome(entity.getUnidadeResponsavelAnterior().getNome());
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