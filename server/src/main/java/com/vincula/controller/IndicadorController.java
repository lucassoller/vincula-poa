package com.vincula.controller;

import com.vincula.dto.DashboardIndicadoresDTO;
import com.vincula.dto.IndicadorValorDTO;
import com.vincula.dto.MotivoQuantidadeDTO;
import com.vincula.service.indicador.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/indicadores")
public class IndicadorController {

    private final DashboardIndicadorService dashboardIndicadorService;
    private final IndicadorInsucessoService indicadorInsucessoService;
    private final IndicadorProcessoService indicadorProcessoService;
    private final IndicadorProducaoService indicadorProducaoService;
    private final IndicadorResultadoService indicadorResultadoService;

    public IndicadorController(DashboardIndicadorService dashboardIndicadorService, IndicadorInsucessoService indicadorInsucessoService, IndicadorProcessoService indicadorProcessoService, IndicadorProducaoService indicadorProducaoService, IndicadorResultadoService indicadorResultadoService) {
        this.dashboardIndicadorService = dashboardIndicadorService;
        this.indicadorInsucessoService = indicadorInsucessoService;
        this.indicadorProcessoService = indicadorProcessoService;
        this.indicadorProducaoService = indicadorProducaoService;
        this.indicadorResultadoService = indicadorResultadoService;
    }

    // =========================
    // PRODUÇÃO
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/geral")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresGerais() {
        return ResponseEntity.ok(indicadorProducaoService.indicadoresGerais());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorProducaoService.indicadoresPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/periodo")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorProducaoService.indicadoresPorPeriodo(inicio, fim));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/unidade/{unidadeSaudeId}/periodo")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorUnidadeEPeriodo(
            @PathVariable Long unidadeSaudeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorProducaoService.indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
    }

    // =========================
    // PROCESSO
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/percentual-resolvidas")
    public ResponseEntity<IndicadorValorDTO> percentualResolvidas() {
        return ResponseEntity.ok(indicadorProcessoService.percentualDemandasResolvidas());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/percentual-resolvidas/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> percentualResolvidasPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorProcessoService.percentualDemandasResolvidasPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/tempo-medio-resolucao")
    public ResponseEntity<IndicadorValorDTO> tempoResolucao() {
        return ResponseEntity.ok(indicadorProcessoService.tempoMedioResolucaoEmHoras());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/tempo-medio-resolucao/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> tempoResolucaoPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorProcessoService.tempoMedioResolucaoEmHorasPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/tempo-primeira-tentativa")
    public ResponseEntity<IndicadorValorDTO> tempoPrimeiraTentativa() {
        return ResponseEntity.ok(indicadorProcessoService.tempoMedioAtePrimeiraTentativa());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/tempo-primeira-tentativa/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> tempoPrimeiraTentativaPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorProcessoService.tempoMedioAtePrimeiraTentativaPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/media-tentativas")
    public ResponseEntity<IndicadorValorDTO> mediaTentativas() {
        return ResponseEntity.ok(indicadorProcessoService.mediaTentativasPorDemanda());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/media-tentativas/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> mediaTentativasPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorProcessoService.mediaTentativasPorDemandaPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/media-tentativas/periodo")
    public ResponseEntity<IndicadorValorDTO> mediaTentativasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorProcessoService.mediaTentativasPorDemandaPorPeriodo(inicio, fim));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/media-tentativas/unidade/{unidadeSaudeId}/periodo")
    public ResponseEntity<IndicadorValorDTO> mediaTentativasPorUnidadeEPeriodo(
       @PathVariable Long unidadeSaudeId,
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorProcessoService.mediaTentativasPorDemandaPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
    }

    // =========================
    // RESULTADO
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/percentual-desfecho")
    public ResponseEntity<List<IndicadorValorDTO>> percentualDesfecho() {
        return ResponseEntity.ok(indicadorResultadoService.percentualPorDesfecho());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/percentual-desfecho/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<IndicadorValorDTO>> percentualDesfechoPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorResultadoService.percentualPorDesfechoPorUnidade(unidadeSaudeId));
    }

    // =========================
    // MOTIVOS
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/demandas/motivos-insucesso")
    public ResponseEntity<List<MotivoQuantidadeDTO>> motivosInsucesso() {
        return ResponseEntity.ok(indicadorInsucessoService.principaisMotivosInsucesso());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/motivos-insucesso/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<MotivoQuantidadeDTO>> motivosInsucessoPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorInsucessoService.principaisMotivosInsucessoPorUnidade(unidadeSaudeId));
    }

    // =========================
    // DASHBOARD
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardIndicadoresDTO> dashboardGeral() {
        return ResponseEntity.ok(dashboardIndicadorService.dashboardGeral());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard/unidade/{unidadeSaudeId}")
    public ResponseEntity<DashboardIndicadoresDTO> dashboardPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(dashboardIndicadorService.dashboardPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/dashboard/periodo")
    public ResponseEntity<DashboardIndicadoresDTO> dashboardPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(dashboardIndicadorService.dashboardPorPeriodo(inicio, fim));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard/unidade/{unidadeSaudeId}/periodo")
    public ResponseEntity<DashboardIndicadoresDTO> dashboardPorUnidadeEPeriodo(
            @PathVariable Long unidadeSaudeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(dashboardIndicadorService.dashboardPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
    }

    // =========================
    // EXPORTACAO
    // =========================

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping(value = "/dashboard/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDashboardGeralCsv() {
        String csv = dashboardIndicadorService.exportarDashboardGeralCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dashboard-geral.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/dashboard/unidade/{unidadeSaudeId}/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDashboardPorUnidadeCsv(@PathVariable Long unidadeSaudeId) {
        String csv = dashboardIndicadorService.exportarDashboardPorUnidadeCsv(unidadeSaudeId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dashboard-unidade-" + unidadeSaudeId + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/dashboard/periodo/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDashboardPorPeriodoCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        String csv = dashboardIndicadorService.exportarDashboardPorPeriodoCsv(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dashboard-periodo.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/dashboard/unidade/{unidadeSaudeId}/periodo/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDashboardPorUnidadeEPeriodoCsv(
            @PathVariable Long unidadeSaudeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        String csv = dashboardIndicadorService.exportarDashboardPorUnidadeEPeriodoCsv(unidadeSaudeId, inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dashboard-unidade-" + unidadeSaudeId + "-periodo.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}