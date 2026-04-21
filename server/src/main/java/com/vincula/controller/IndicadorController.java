package com.vincula.controller;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.service.IndicadorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/indicadores")
public class IndicadorController {

    private final IndicadorService indicadorService;

    public IndicadorController(IndicadorService indicadorService) {
        this.indicadorService = indicadorService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/geral")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresGerais() {
        return ResponseEntity.ok(indicadorService.indicadoresGerais());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorService.indicadoresPorUnidade(unidadeSaudeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/desfecho")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorDesfecho() {
        return ResponseEntity.ok(indicadorService.indicadoresPorDesfecho());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/desfecho/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorDesfechoEUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorService.indicadoresPorDesfechoEUnidade(unidadeSaudeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/periodo")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorService.indicadoresPorPeriodo(inicio, fim));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demandas/unidade/{unidadeSaudeId}/periodo")
    public ResponseEntity<List<IndicadorValorDTO>> indicadoresPorUnidadeEPeriodo(
            @PathVariable Long unidadeSaudeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorService.indicadoresPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim));
    }

    @GetMapping("/demandas/percentual-resolvidas")
    public ResponseEntity<IndicadorValorDTO> percentualDemandasResolvidas() {
        return ResponseEntity.ok(indicadorService.percentualDemandasResolvidas());
    }

    @GetMapping("/demandas/percentual-resolvidas/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> percentualDemandasResolvidasPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorService.percentualDemandasResolvidasPorUnidade(unidadeSaudeId));
    }

    @GetMapping("/demandas/tempo-medio-resolucao")
    public ResponseEntity<IndicadorValorDTO> tempoMedioResolucao() {
        return ResponseEntity.ok(indicadorService.tempoMedioResolucaoEmHoras());
    }

    @GetMapping("/demandas/tempo-medio-resolucao/unidade/{unidadeSaudeId}")
    public ResponseEntity<IndicadorValorDTO> tempoMedioResolucaoPorUnidade(@PathVariable Long unidadeSaudeId) {
        return ResponseEntity.ok(indicadorService.tempoMedioResolucaoEmHorasPorUnidade(unidadeSaudeId));
    }

    @GetMapping("/demandas/tempo-medio-resolucao/periodo")
    public ResponseEntity<IndicadorValorDTO> tempoMedioResolucaoPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(indicadorService.tempoMedioResolucaoEmHorasPorPeriodo(inicio, fim));
    }
}