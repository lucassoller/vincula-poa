package com.vincula.service;

import com.vincula.dto.*;
import com.vincula.entity.Usuario;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.PerfilUsuario;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorService {

    private final DemandaRepository demandaRepository;
    private final TentativaContatoRepository tentativaContatoRepository;
    private final UsuarioService usuarioService;

    public IndicadorService(DemandaRepository demandaRepository,
                            TentativaContatoRepository tentativaContatoRepository,
                            UsuarioService usuarioService) {
        this.demandaRepository = demandaRepository;
        this.tentativaContatoRepository = tentativaContatoRepository;
        this.usuarioService = usuarioService;
    }

    public List<IndicadorValorDTO> indicadoresGerais() {
        validarAcessoDashboardGeral();

        return List.of(
                new IndicadorValorDTO("totalDemandas",  demandaRepository.countBy()),
                new IndicadorValorDTO("demandasAbertas",  demandaRepository.countByStatus(StatusDemanda.ABERTA)),
                new IndicadorValorDTO("demandasEmAndamento",  demandaRepository.countByStatus(StatusDemanda.EM_ANDAMENTO)),
                new IndicadorValorDTO("demandasFinalizadas",  demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        return List.of(
                new IndicadorValorDTO("totalDemandas",  demandaRepository.countByUnidadeSaudeId(unidadeSaudeId)),
                new IndicadorValorDTO("demandasAbertas",  demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.ABERTA, unidadeSaudeId)),
                new IndicadorValorDTO("demandasEmAndamento",  demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.EM_ANDAMENTO, unidadeSaudeId)),
                new IndicadorValorDTO("demandasFinalizadas",  demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        return List.of(
                new IndicadorValorDTO("demandasCriadas",  demandaRepository.countByDataHoraCriacaoBetween(inicio, fim)),
                new IndicadorValorDTO("demandasFinalizadas",  demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        return List.of(
                new IndicadorValorDTO("demandasCriadas",  demandaRepository.countByUnidadeSaudeIdAndDataHoraCriacaoBetween(unidadeSaudeId, inicio, fim)),
                new IndicadorValorDTO("demandasFinalizadas",  demandaRepository.countByUnidadeSaudeIdAndDataHoraFinalizacaoBetween(unidadeSaudeId, inicio, fim))
        );
    }

    public IndicadorValorDTO percentualDemandasResolvidas() {
        validarAcessoDashboardGeral();

        double total = demandaRepository.countBy();
        double finalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);

        double percentual = total == 0 ? 0.0 : ( finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", arredondarDuasCasas(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        double total = demandaRepository.countByUnidadeSaudeId(unidadeSaudeId);
        double finalizadas = demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId);

        double percentual = total == 0 ? 0.0 : ( finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", arredondarDuasCasas(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        double total = demandaRepository.countByDataHoraCriacaoBetween(inicio, fim);
        double finalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim);

        double percentual = total == 0 ? 0.0 : ( finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", arredondarDuasCasas(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        double total = demandaRepository.countByUnidadeSaudeIdAndDataHoraCriacaoBetween(unidadeSaudeId, inicio, fim);
        double finalizadas = demandaRepository.countByStatusAndUnidadeSaudeIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeSaudeId, inicio, fim);

        double percentual = total == 0 ? 0.0 : ( finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", arredondarDuasCasas(percentual));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHoras() {
        validarAcessoDashboardGeral();

        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundos();
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", arredondarDuasCasas(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidade(unidadeSaudeId);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", arredondarDuasCasas(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorPeriodo(inicio, fim);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", arredondarDuasCasas(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", arredondarDuasCasas(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativa() {
        validarAcessoDashboardGeral();

        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHoras();

        return new IndicadorValorDTO(
                "tempoMedioAtePrimeiraTentativaHoras", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidade(unidadeSaudeId);

        return new IndicadorValorDTO(
                "tempoMedioAtePrimeiraTentativaHoras", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorPeriodo(inicio, fim);

        return new IndicadorValorDTO(
                "tempoMedioAtePrimeiraTentativaHoras", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);

        return new IndicadorValorDTO(
                "tempoMedioAtePrimeiraTentativaHoras", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemanda() {
        validarAcessoDashboardGeral();

        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemanda();

        return new IndicadorValorDTO(
                "mediaTentativasPorDemanda", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorUnidade(unidadeSaudeId);

        return new IndicadorValorDTO(
                "mediaTentativasPorDemanda", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorPeriodo(inicio, fim);

        return new IndicadorValorDTO(
                "mediaTentativasPorDemanda", arredondarDuasCasas(valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorUnidadeEPeriodo(Long unidadeSaudeId,
                                                                         LocalDateTime inicio,
                                                                         LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorUnidadeEPeriodo(
                unidadeSaudeId, inicio, fim
        );

        return new IndicadorValorDTO(
                "mediaTentativasPorDemanda", arredondarDuasCasas(valor));
    }

    public List<IndicadorValorDTO> percentualPorDesfecho() {
        validarAcessoDashboardGeral();

        double totalFinalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);

        double encontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO) * 100.0 / totalFinalizadas;

        double naoEncontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.NAO_ENCONTRADO) * 100.0 / totalFinalizadas;

        double obito = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.OBITO) * 100.0 / totalFinalizadas;

        double foraDoTerritorio = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.FORA_DO_TERRITORIO) * 100.0 / totalFinalizadas;

        return List.of(
                new IndicadorValorDTO("percentualEncontrado", arredondarDuasCasas(encontrado)),
                new IndicadorValorDTO("percentualNaoEncontrado", arredondarDuasCasas(naoEncontrado)),
                new IndicadorValorDTO("percentualObito", arredondarDuasCasas(obito)),
                new IndicadorValorDTO("percentualForaDoTerritorio", arredondarDuasCasas(foraDoTerritorio))
        );
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId);

        double encontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.ENCONTRADO, unidadeSaudeId) * 100.0 / totalFinalizadas;

        double naoEncontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.NAO_ENCONTRADO, unidadeSaudeId) * 100.0 / totalFinalizadas;

        double obito = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.OBITO, unidadeSaudeId) * 100.0 / totalFinalizadas;

        double foraDoTerritorio = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.FORA_DO_TERRITORIO, unidadeSaudeId) * 100.0 / totalFinalizadas;

        return List.of(
                new IndicadorValorDTO("percentualEncontrado", arredondarDuasCasas(encontrado)),
                new IndicadorValorDTO("percentualNaoEncontrado", arredondarDuasCasas(naoEncontrado)),
                new IndicadorValorDTO("percentualObito", arredondarDuasCasas(obito)),
                new IndicadorValorDTO("percentualForaDoTerritorio", arredondarDuasCasas(foraDoTerritorio))
        );
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        double totalFinalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim);

        double encontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO, inicio, fim) * 100.0 / totalFinalizadas;

        double naoEncontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_ENCONTRADO, inicio, fim) * 100.0 / totalFinalizadas;

        double obito = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim) * 100.0 / totalFinalizadas;

        double foraDoTerritorio = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.FORA_DO_TERRITORIO, inicio, fim) * 100.0 / totalFinalizadas;

        return List.of(
                new IndicadorValorDTO("percentualEncontrado", arredondarDuasCasas(encontrado)),
                new IndicadorValorDTO("percentualNaoEncontrado", arredondarDuasCasas(naoEncontrado)),
                new IndicadorValorDTO("percentualObito", arredondarDuasCasas(obito)),
                new IndicadorValorDTO("percentualForaDoTerritorio", arredondarDuasCasas(foraDoTerritorio))
        );
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeSaudeIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeSaudeId, inicio, fim);

        double encontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO, unidadeSaudeId, inicio, fim) * 100.0 / totalFinalizadas;

        double naoEncontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_ENCONTRADO, unidadeSaudeId, inicio, fim) * 100.0 / totalFinalizadas;

        double obito = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeSaudeId, inicio, fim) * 100.0 / totalFinalizadas;

        double foraDoTerritorio = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.FORA_DO_TERRITORIO, unidadeSaudeId, inicio, fim) * 100.0 / totalFinalizadas;

        return List.of(
                new IndicadorValorDTO("percentualEncontrado", arredondarDuasCasas(encontrado)),
                new IndicadorValorDTO("percentualNaoEncontrado", arredondarDuasCasas(naoEncontrado)),
                new IndicadorValorDTO("percentualObito", arredondarDuasCasas(obito)),
                new IndicadorValorDTO("percentualForaDoTerritorio", arredondarDuasCasas(foraDoTerritorio))
        );
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucesso() {
        validarAcessoDashboardGeral();

        return demandaRepository.listarPrincipaisMotivosInsucesso()
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        return demandaRepository.listarPrincipaisMotivosInsucessoPorUnidade(unidadeSaudeId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        return demandaRepository.listarPrincipaisMotivosInsucessoPorPeriodo(inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosInsucessoPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        return demandaRepository.listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(item.getMotivo(), item.getQuantidade()))
                .toList();
    }

    public DashboardIndicadoresDTO dashboardGeral() {
        validarAcessoDashboardGeral();

        List<IndicadorValorDTO> producao = indicadoresGerais();

        List<IndicadorValorDTO> processo = List.of(
                percentualDemandasResolvidas(),
                tempoMedioResolucaoEmHoras(),
                tempoMedioAtePrimeiraTentativa(),
                mediaTentativasPorDemanda()
        );

        List<IndicadorValorDTO> resultado = percentualPorDesfecho();

        List<MotivoQuantidadeDTO> motivos = principaisMotivosInsucesso();

        return new DashboardIndicadoresDTO(producao, processo, resultado, motivos);
    }

    public DashboardIndicadoresDTO dashboardPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        List<IndicadorValorDTO> producao = indicadoresPorUnidade(unidadeSaudeId);

        List<IndicadorValorDTO> processo = List.of(
                percentualDemandasResolvidasPorUnidade(unidadeSaudeId),
                tempoMedioResolucaoEmHorasPorUnidade(unidadeSaudeId),
                tempoMedioAtePrimeiraTentativaPorUnidade(unidadeSaudeId),
                mediaTentativasPorDemandaPorUnidade(unidadeSaudeId)
        );

        List<IndicadorValorDTO> resultado = percentualPorDesfechoPorUnidade(unidadeSaudeId);

        List<MotivoQuantidadeDTO> motivos = principaisMotivosInsucessoPorUnidade(unidadeSaudeId);

        return new DashboardIndicadoresDTO(producao, processo, resultado, motivos);
    }

    public DashboardIndicadoresDTO dashboardPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        List<IndicadorValorDTO> producao = indicadoresPorPeriodo(inicio, fim);

        List<IndicadorValorDTO> processo = List.of(
                percentualDemandasResolvidasPorPeriodo(inicio, fim),
                tempoMedioResolucaoEmHorasPorPeriodo(inicio, fim),
                tempoMedioAtePrimeiraTentativaPorPeriodo(inicio, fim),
                mediaTentativasPorDemandaPorPeriodo(inicio, fim)
        );

        List<IndicadorValorDTO> resultado = percentualPorDesfechoPorPeriodo(inicio, fim);

        List<MotivoQuantidadeDTO> motivos = principaisMotivosInsucessoPorPeriodo(inicio, fim);

        return new DashboardIndicadoresDTO(producao, processo, resultado, motivos);
    }

    public DashboardIndicadoresDTO dashboardPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        List<IndicadorValorDTO> producao = indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);

        List<IndicadorValorDTO> processo = List.of(
                percentualDemandasResolvidasPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                tempoMedioResolucaoEmHorasPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                tempoMedioAtePrimeiraTentativaPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                mediaTentativasPorDemandaPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
        );

        List<IndicadorValorDTO> resultado = percentualPorDesfechoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);

        List<MotivoQuantidadeDTO> motivos = principaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);

        return new DashboardIndicadoresDTO(producao, processo, resultado, motivos);
    }

    private double arredondarDuasCasas(Double valor) {
        if (valor == null) {
            return 0.0;
        }

        return Math.round(valor * 100.0) / 100.0;
    }

    private void validarAcessoDashboardGeral() {
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        if (usuario.getPerfil() != PerfilUsuario.GESTAO_MUNICIPAL) {
            throw new BusinessException("Usuário não pode acessar indicadores gerais");
        }
    }

    private void validarAcessoUnidade(Long unidadeSaudeId) {
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();

        if (usuario.getPerfil() == PerfilUsuario.GESTAO_MUNICIPAL) {
            return;
        }

        if (usuario.getPerfil() == PerfilUsuario.EXECUTOR_APS) {
            if (usuario.getUnidadeSaude() == null || !usuario.getUnidadeSaude().getId().equals(unidadeSaudeId)) {
                throw new BusinessException("Usuário não pode acessar indicadores de outra unidade");
            }
            return;
        }

        throw new BusinessException("Usuário não pode acessar indicadores por unidade");
    }

    private String montarCsvDashboard(DashboardIndicadoresDTO dashboard) {
        StringBuilder sb = new StringBuilder();

        sb.append("categoria,indicador,valor\n");

        for (IndicadorValorDTO item : dashboard.getProducao()) {
            sb.append("producao,")
                    .append(item.getIndicador()).append(",")
                    .append(item.getValor()).append("\n");
        }

        for (IndicadorValorDTO item : dashboard.getProcesso()) {
            sb.append("processo,")
                    .append(item.getIndicador()).append(",")
                    .append(item.getValor()).append("\n");
        }

        for (IndicadorValorDTO item : dashboard.getResultado()) {
            sb.append("resultado,")
                    .append(item.getIndicador()).append(",")
                    .append(item.getValor()).append("\n");
        }

        for (MotivoQuantidadeDTO item : dashboard.getPrincipaisMotivosInsucesso()) {
            sb.append("motivo_insucesso,")
                    .append(item.getMotivo()).append(",")
                    .append(item.getQuantidade()).append("\n");
        }

        return "\uFEFF" + sb.toString();
    }

    public String exportarDashboardGeralCsv() {
        DashboardIndicadoresDTO dashboard = dashboardGeral();
        return montarCsvDashboard(dashboard);
    }

    public String exportarDashboardPorUnidadeCsv(Long unidadeSaudeId) {
        DashboardIndicadoresDTO dashboard = dashboardPorUnidade(unidadeSaudeId);
        return montarCsvDashboard(dashboard);
    }

    public String exportarDashboardPorPeriodoCsv(LocalDateTime inicio, LocalDateTime fim) {
        DashboardIndicadoresDTO dashboard = dashboardPorPeriodo(inicio, fim);
        return montarCsvDashboard(dashboard);
    }

    public String exportarDashboardPorUnidadeEPeriodoCsv(Long unidadeSaudeId,
                                                         LocalDateTime inicio,
                                                         LocalDateTime fim) {
        DashboardIndicadoresDTO dashboard = dashboardPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);
        return montarCsvDashboard(dashboard);
    }
}