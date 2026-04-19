package com.vincula.controller;

import com.vincula.dto.PacienteDTO;
import com.vincula.dto.UnidadeSaudeDTO;
import com.vincula.service.UnidadeSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades-saude")
public class UnidadeSaudeController {

    private final UnidadeSaudeService unidadeSaudeService;

    public UnidadeSaudeController(UnidadeSaudeService unidadeSaudeService) {
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @PostMapping
    public ResponseEntity<UnidadeSaudeDTO> criar(@Valid @RequestBody UnidadeSaudeDTO dto) {
        UnidadeSaudeDTO criada = unidadeSaudeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<UnidadeSaudeDTO>> listarTodos() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaudeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeSaudeService.buscarPorId(id));
    }

    @GetMapping("/{id}/pacientes")
    public ResponseEntity<List<PacienteDTO>> listarPacientesPorUnidade(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeSaudeService.listarPacientesPorUnidade(id));
    }

    @GetMapping("/cnes/{cnes}")
    public ResponseEntity<UnidadeSaudeDTO> buscarPorCnes(@PathVariable String cnes) {
        return ResponseEntity.ok(unidadeSaudeService.buscarPorCnes(cnes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeSaudeDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UnidadeSaudeDTO dto) {
        return ResponseEntity.ok(unidadeSaudeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeSaudeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}