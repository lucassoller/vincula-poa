package com.vincula.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.service.ImportarTerritorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/territorios")
public class ImportarMapaController {

    private final ImportarTerritorioService importarTerritorioService;
    private final ObjectMapper objectMapper;

    public ImportarMapaController(ImportarTerritorioService importarTerritorioService, ObjectMapper objectMapper) {
        this.importarTerritorioService = importarTerritorioService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PostMapping("/importar")
    public ResponseEntity<Void> importar(@RequestBody Map<String, Object> geojson) {
        JsonNode node = objectMapper.valueToTree(geojson);
        importarTerritorioService.importar(node);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mapa")
    public ResponseEntity<List<TerritorioUbsDTO>> exportar() {
        return ResponseEntity.ok(importarTerritorioService.listarTodos());
    }
}
