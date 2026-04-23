package com.vincula.service.indicador;

import com.vincula.dto.*;
import com.vincula.entity.Usuario;
import com.vincula.enums.PerfilUsuario;
import com.vincula.exception.BusinessException;
import com.vincula.export.DashboardIndicadoresExporter;
import com.vincula.service.UsuarioService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DashboardIndicadorService {

    private final IndicadorProducaoService indicadorProducaoService;
    private final IndicadorProcessoService indicadorProcessoService;
    private final IndicadorResultadoService indicadorResultadoService;
    private final IndicadorInsucessoService indicadorInsucessoService;
    private final UsuarioService usuarioService;
    private final DashboardIndicadoresExporter csvExporter;

    public DashboardIndicadorService(IndicadorProducaoService indicadorProducaoService,
                                     IndicadorProcessoService indicadorProcessoService,
                                     IndicadorResultadoService indicadorResultadoService,
                                     IndicadorInsucessoService indicadorInsucessoService,
                                     UsuarioService usuarioService, DashboardIndicadoresExporter csvExporter) {
        this.indicadorProducaoService = indicadorProducaoService;
        this.indicadorProcessoService = indicadorProcessoService;
        this.indicadorResultadoService = indicadorResultadoService;
        this.indicadorInsucessoService = indicadorInsucessoService;
        this.usuarioService = usuarioService;
        this.csvExporter = csvExporter;
    }

    public DashboardIndicadoresDTO dashboardGeral() {
        validarAcessoDashboardGeral();

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresGerais(),
                indicadorProcessoService.montarProcessoGeral(),
                indicadorResultadoService.percentualPorDesfecho(),
                indicadorInsucessoService.principaisMotivosInsucesso()
        );
    }

    public DashboardIndicadoresDTO dashboardPorUnidade(Long unidadeSaudeId) {
        validarAcessoUnidade(unidadeSaudeId);

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorUnidade(unidadeSaudeId),
                indicadorProcessoService.montarProcessoPorUnidade(unidadeSaudeId),
                indicadorResultadoService.percentualPorDesfechoPorUnidade(unidadeSaudeId),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidade(unidadeSaudeId)
        );
    }

    public DashboardIndicadoresDTO dashboardPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoDashboardGeral();

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorPeriodo(inicio, fim),
                indicadorProcessoService.montarProcessoPorPeriodo(inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorPeriodo(inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorPeriodo(inicio, fim)
        );
    }

    public DashboardIndicadoresDTO dashboardPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        validarAcessoUnidade(unidadeSaudeId);

        return new DashboardIndicadoresDTO(
                indicadorProducaoService.indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorProcessoService.montarProcessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorResultadoService.percentualPorDesfechoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                indicadorInsucessoService.principaisMotivosInsucessoPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
        );
    }

    public String exportarDashboardGeralCsv() {
        return csvExporter.exportar(dashboardGeral());
    }

    public String exportarDashboardPorUnidadeCsv(Long unidadeSaudeId) {
        return csvExporter.exportar(dashboardPorUnidade(unidadeSaudeId));
    }

    public String exportarDashboardPorPeriodoCsv(LocalDateTime inicio, LocalDateTime fim) {
        return csvExporter.exportar(dashboardPorPeriodo(inicio, fim));
    }

    public String exportarDashboardPorUnidadeEPeriodoCsv(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
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