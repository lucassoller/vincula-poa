package com.vincula.controller;

import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.service.ImportarTerritorioService;
import com.vincula.service.ImportarUbsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/territorios")
public class ImportarMapaController {

    private final ImportarTerritorioService importarTerritorioService;
    private final ImportarUbsService importarUbsService;

    public ImportarMapaController(ImportarTerritorioService importarTerritorioService,
                                  ImportarUbsService  importarUbsService) {
        this.importarTerritorioService = importarTerritorioService;
        this.importarUbsService = importarUbsService;
    }

    @PostMapping("/importar")
    public ResponseEntity<Void> importar() {

        importarUbsService.importar();
        importarTerritorioService.importar();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/mapa")
    public ResponseEntity<List<TerritorioUbsDTO>> exportar() {
        return ResponseEntity.ok(importarTerritorioService.listarTodos());
    }
}
