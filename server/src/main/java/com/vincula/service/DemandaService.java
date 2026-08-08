package com.vincula.service;

import com.vincula.dto.MotivoBuscaResponseDTO;
import com.vincula.dto.MotivoComplementoResponseDTO;
import com.vincula.dto.demanda.*;
import com.vincula.entity.*;
import com.vincula.enums.*;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.export.DemandaExporter;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.UsuarioRepository;
import com.vincula.repository.ServicoRepository;
import com.vincula.specification.DemandaSpecification;
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final ServidorService servidorService;
    private final AuditoriaFacade auditoriaFacade;
    private final DemandaExporter demandaExporter;

    public DemandaService(DemandaRepository demandaRepository,
                          UsuarioRepository usuarioRepository,
                          ServicoRepository servicoRepository,
                          ServidorService servidorService,
                          AuditoriaFacade auditoriaFacade, DemandaExporter demandaExporter) {
        this.demandaRepository = demandaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoRepository = servicoRepository;
        this.servidorService = servidorService;
        this.auditoriaFacade = auditoriaFacade;
        this.demandaExporter = demandaExporter;
    }

    public DemandaResponseDTO criar(DemandaDTO dto) {
        Demanda entity = toEntity(dto);

        Demanda salvo = demandaRepository.save(entity);

        auditoriaFacade.demandaCriada(salvo.getId(), salvo.getUsuario().getId());

        return toDTO(salvo);
    }

    public Page<DemandaResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return demandaRepository.findByUsuarioOrderByUsuarioNome(usuarioId, pageable)
                .map(this::toDTO);
    }

    public Page<DemandaResponseDTO> listarTodasFiltradas(
            FiltroDemandaRequestDTO filtro,
            Pageable pageable) {

        Specification<Demanda> specification =
                DemandaSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("usuario.nomeCompleto")
        );

        return demandaRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public String exportarDemandasCsv(FiltroDemandaRequestDTO filtro) {

        Specification<Demanda> specification =
                DemandaSpecification.comFiltros(filtro);

        List<Demanda> demandas = demandaRepository.findAll(
                specification,
                Sort.by("usuario.nomeCompleto")
        );

        auditoriaFacade.exportacaoCsvRealizadaDemanda("Demandas exportadas");

        return demandaExporter.exportar(demandas);
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
            throw new BusinessException("A demanda já está finalizada");
        }

        if (dto.getDesfechoDemanda() == null) {
            throw new BusinessException("O desfecho é obrigatório");
        }

        Servidor servidor = servidorService.buscarServidorAutenticado();

        entity.setStatus(StatusDemanda.FINALIZADA);
        entity.setDesfecho(dto.getDesfechoDemanda());
        entity.setDescricaoDesfecho(dto.getDescricaoDesfecho());
        entity.setDataHoraFinalizacao(LocalDateTime.now());
        entity.setServidorEncerramento(servidor);

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

        Servico novaServico = buscarServicoPorId(dto.getNovoServicoResponsavelId());

        if (demanda.getServicoResponsavel().getId().equals(novaServico.getId())) {
            throw new BusinessException("O novo serviço responsável deve ser diferente da atual");
        }

        Servidor servidor = servidorService.buscarServidorAutenticado();

        demanda.setServicoResponsavelAnterior(demanda.getServicoResponsavel());
        demanda.setServicoResponsavel(novaServico);
        demanda.setFoiRedirecionada(true);
        demanda.setMotivoRedirecionamento(dto.getMotivoRedirecionamento());
        demanda.setDataHoraRedirecionamento(LocalDateTime.now());
        demanda.setServidorRedirecionamento(servidor);

        Demanda atualizada = demandaRepository.save(demanda);

        String descricaoLog = AuditoriaDescricaoUtil.demandaRedirecionada(atualizada);

        auditoriaFacade.demandaRedirecionada(atualizada.getId(), descricaoLog);

        return toDTO(atualizada);
    }

    public void redirecionarDemandasAbertasDoUsuario(Long usuarioId, RedirecionarDemandaDTO dto) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);

        Servidor servidor = servidorService.buscarServidorAutenticado();

        List<Demanda> demandas = demandaRepository
                .findByUsuarioIdAndStatusIn(
                        usuarioId,
                        List.of(StatusDemanda.ABERTA, StatusDemanda.EM_ANDAMENTO)
                );

        for (Demanda demanda : demandas) {
            if (!demanda.getServicoResponsavel().getId().equals(usuario.getServico().getId())) {
                demanda.setServicoResponsavelAnterior(demanda.getServicoResponsavel());
                demanda.setServicoResponsavel(usuario.getServico());
                demanda.setFoiRedirecionada(true);
                demanda.setMotivoRedirecionamento(dto.getMotivoRedirecionamento());
                demanda.setServidorRedirecionamento(servidor);
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

    private Demanda buscarDemandaPorId(Long id) {
        return demandaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private Servico buscarServicoPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }

    private Demanda toEntity(DemandaDTO dto) {
        Usuario usuario = buscarUsuarioPorId(dto.getUsuarioId());

        Servidor servidorCriador = servidorService.buscarServidorAutenticado();

        Demanda entity = new Demanda();
        entity.setUsuario(usuario);
        entity.setServidorCriador(servidorCriador);

        if(servidorCriador.getServico() != null){
            entity.setServicoSolicitante(servidorCriador.getServico());
        }

        entity.setServicoResponsavel(usuario.getServico());
        entity.setPrioridade(dto.getPrioridade());
        entity.setDescricaoBusca(dto.getDescricaoBusca());
        entity.setPrazoDemanda(dto.getPrazoDemanda());
        entity.setStatus(StatusDemanda.ABERTA);
        entity.setDataHoraCriacao(LocalDateTime.now());
        entity.setDataHoraLimite(calcularDataLimite(entity.getDataHoraCriacao(), dto.getPrazoDemanda()));

        validarMotivoEComplemento(dto.getMotivoBuscaAtiva(), dto.getMotivoComplemento());
        entity.setMotivoBuscaAtiva(dto.getMotivoBuscaAtiva());
        entity.setMotivoComplemento(dto.getMotivoComplemento());

        return entity;
    }

    private DemandaResponseDTO toDTO(Demanda entity) {
        DemandaResponseDTO dto = new DemandaResponseDTO();

        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuario().getId());
        dto.setUsuarioNome(entity.getUsuario().getNomeCompleto());

        if (entity.getServicoSolicitante() != null) {
            dto.setServicoSolicitanteId(entity.getServicoSolicitante().getId());
            dto.setServicoSolicitanteNome(entity.getServicoSolicitante().getNome());
        }

        dto.setServicoResponsavelId(entity.getServicoResponsavel().getId());
        dto.setServicoResponsavelNome(entity.getServicoResponsavel().getNome());

        dto.setMotivoBuscaAtiva(entity.getMotivoBuscaAtiva());
        dto.setMotivoComplemento(entity.getMotivoComplemento());
        dto.setPrioridade(entity.getPrioridade());
        dto.setDescricaoBusca(entity.getDescricaoBusca());
        dto.setPrazoDemanda(entity.getPrazoDemanda());
        dto.setStatus(entity.getStatus());

        dto.setDataHoraCriacao(entity.getDataHoraCriacao());
        dto.setDataHoraLimite(entity.getDataHoraLimite());

        dto.setServidorCriadorId(entity.getServidorCriador().getId());
        dto.setServidorCriadorNome(entity.getServidorCriador().getNome());

        dto.setDesfecho(entity.getDesfecho());
        dto.setDescricaoDesfecho(entity.getDescricaoDesfecho());
        dto.setDataHoraFinalizacao(entity.getDataHoraFinalizacao());

        if (entity.getServidorEncerramento() != null) {
            dto.setServidorEncerramentoId(entity.getServidorEncerramento().getId());
            dto.setServidorEncerramentoNome(entity.getServidorEncerramento().getNome());
        }

        dto.setFoiRedirecionada(entity.getFoiRedirecionada());

        if (entity.getServicoResponsavelAnterior() != null) {
            dto.setServicoResponsavelAnteriorId(entity.getServicoResponsavelAnterior().getId());
            dto.setServicoResponsavelAnteriorNome(entity.getServicoResponsavelAnterior().getNome());
        }

        dto.setMotivoRedirecionamento(entity.getMotivoRedirecionamento());
        dto.setDataHoraRedirecionamento(entity.getDataHoraRedirecionamento());

        if (entity.getServidorRedirecionamento() != null) {
            dto.setServidorRedirecionamentoId(entity.getServidorRedirecionamento().getId());
            dto.setServidorRedirecionamentoNome(entity.getServidorRedirecionamento().getNome());
        }

        return dto;
    }

    public void validarMotivoEComplemento(MotivoBuscaAtiva motivo,
                                        MotivoComplemento complemento) {

        // se não tem complemento, não valida nada
        if (complemento == null || motivo == null || motivo == MotivoBuscaAtiva.OUTRO) {
            return;
        }

        Set<MotivoComplemento> permitidos = motivo.getComplementosPermitidos();

        // se motivo não tem regra ou não permite nada
        if (!permitidos.contains(complemento)) {
            throw new BusinessException(
                "O detalhamento é inválido para o motivo: " + motivo
            );
        }
    }

    public List<MotivoBuscaResponseDTO> listarMotivos() {

        return Arrays.stream(MotivoBuscaAtiva.values())
                .map(motivo -> new MotivoBuscaResponseDTO(
                        motivo.name(),
                        motivo.getDescricao(),
                        motivo.getComplementosPermitidos()
                                .stream()
                                .map(c -> new MotivoComplementoResponseDTO(
                                        c.name(),
                                        c.getDescricao()))
                                .toList()
                ))
                .toList();
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