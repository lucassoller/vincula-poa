package com.vincula.controller;

import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.service.indicador.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/indicadores")
public class IndicadorController {

    private final IndicadorService indicadorService;

    public IndicadorController(IndicadorService indicadorService) {
        this.indicadorService = indicadorService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/geral")
    public ResponseEntity<IndicadorDTO> indicadorGeral(
            @RequestParam(required = false) Long unidadeSaudeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Long servidorId) {
        return ResponseEntity.ok(indicadorService.indicadorGeral(unidadeSaudeId, inicio, fim, servidorId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarIndicadorGeralCsv(
            @RequestParam(required = false) Long unidadeSaudeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Long servidorId
    ) {
        String csv = indicadorService.exportarIndicadorGeralCsv(unidadeSaudeId, inicio, fim, servidorId);
        String dataHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=indicadores-vincula-poa"+dataHora+".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}