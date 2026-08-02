package com.vincula.controller;

import com.vincula.dto.demanda.FiltroDemandaRequestDTO;
import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
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
    @PostMapping("/geral")
    public ResponseEntity<IndicadorDTO> indicadorGeral(
            @RequestBody FiltroIndicadorRequestDTO filtro) {
        return ResponseEntity.ok(indicadorService.gerarIndicadores(filtro));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarIndicadorGeralCsv(
            @RequestBody FiltroIndicadorRequestDTO filtro) {
        String csv = indicadorService.exportarIndicadoresCsv(filtro);
        String dataHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=indicadores-vincula-poa"+dataHora+".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}