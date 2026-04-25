package com.vincula.service.indicador;

import com.vincula.dto.*;
import com.vincula.entity.Usuario;
import com.vincula.enums.PerfilUsuario;
import com.vincula.exception.BusinessException;
import com.vincula.export.DashboardIndicadoresExporter;
import com.vincula.service.UsuarioService;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardIndicadorService {

    private final IndicadorProducaoService indicadorProducaoService;
    private final IndicadorProcessoService indicadorProcessoService;
    private final IndicadorResultadoService indicadorResultadoService;
    private final IndicadorInsucessoService indicadorInsucessoService;
    private final UsuarioService usuarioService;
    private final DashboardIndicadoresExporter csvExporter;
    private final IndicadorRankingService indicadorRankingService;
    private final AuditoriaFacade auditoriaFacade;

    public DashboardIndicadorService(IndicadorProducaoService indicadorProducaoService,
                                     IndicadorProcessoService indicadorProcessoService,
                                     IndicadorResultadoService indicadorResultadoService,
                                     IndicadorInsucessoService indicadorInsucessoService,
                                     UsuarioService usuarioService,
                                     DashboardIndicadoresExporter csvExporter,
                                     IndicadorRankingService indicadorRankingService,
                                     AuditoriaFacade auditoriaFacade) {
        this.indicadorProducaoService = indicadorProducaoService;
        this.indicadorProcessoService = indicadorProcessoService;
        this.indicadorResultadoService = indicadorResultadoService;
        this.indicadorInsucessoService = indicadorInsucessoService;
        this.usuarioService = usuarioService;
        this.csvExporter = csvExporter;
        this.indicadorRankingService = indicadorRankingService;
        this.auditoriaFacade = auditoriaFacade;
    }

    public DashboardIndicadoresDTO dashboardGeral() {
        validarAcessoDashboardGeral();

        auditoriaFacade.dashboardAcessado("Dashboard geral acessado");

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresGerais(),
                indicadorProcessoService.montarProcessoGeral(),
                indicadorResultadoService.percentualPorDesfecho(),
                indicadorInsucessoService.principaisMotivosInsucesso(),
                indicadorRankingService.rankingPorTotalDemandas(),
                indicadorRankingService.rankingPorPercentualResolucao(),
                indicadorRankingService.rankingPorTempoMedioResolucao(),
                indicadorRankingService.rankingPorTempoAtePrimeiraTentativa()

        );
    }

    public DashboardIndicadoresDTO dashboardPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        auditoriaFacade.dashboardAcessado("Dashboard da unidade ID " + unidadeSaudeId + " acessado");

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorUnidade(unidadeSaudeId),
                indicadorProcessoService.montarProcessoPorUnidade(unidadeSaudeId),
                indicadorResultadoService.percentualPorDesfechoPorUnidade(unidadeSaudeId),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidade(unidadeSaudeId),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public DashboardIndicadoresDTO dashboardPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        auditoriaFacade.dashboardAcessado("Dashboard de "+ inicio + " até "+ fim +" acessado");

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorPeriodo(inicio, fim),
                indicadorProcessoService.montarProcessoPorPeriodo(inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorPeriodo(inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorPeriodo(inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public DashboardIndicadoresDTO dashboardPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        auditoriaFacade.dashboardAcessado("Dashboard da unidade ID " +
                unidadeSaudeId + "de "+ inicio + " até "+ fim +" acessado");

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorProcessoService.montarProcessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public String exportarDashboardGeralCsv() {
        auditoriaFacade.exportacaoCsvRealizada("Dashboard geral exportado");
        return csvExporter.exportar(dashboardGeral());
    }

    public String exportarDashboardPorUnidadeCsv(Long unidadeSaudeId) {
        auditoriaFacade.exportacaoCsvRealizada("Dashboard da unidade ID " + unidadeSaudeId + " exportado");
        return csvExporter.exportar(dashboardPorUnidade(unidadeSaudeId));
    }

    public String exportarDashboardPorPeriodoCsv(LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Dashboard de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(dashboardPorPeriodo(inicio, fim));
    }

    public String exportarDashboardPorUnidadeEPeriodoCsv(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        auditoriaFacade.exportacaoCsvRealizada("Dashboard da unidade ID " +
                unidadeSaudeId + "de "+ inicio + " até "+ fim +" exportado");
        return csvExporter.exportar(dashboardPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
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
}