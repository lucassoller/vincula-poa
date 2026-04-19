package com.vincula.controller;

import com.vincula.dto.ObservacaoDTO;
import com.vincula.service.ObservacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/observacoes")
public class ObservacaoController {

    private final ObservacaoService observacaoService;

    public ObservacaoController(ObservacaoService observacaoService) {
        this.observacaoService = observacaoService;
    }

    @PostMapping
    public ResponseEntity<ObservacaoDTO> criar(@RequestBody ObservacaoDTO dto) {
        return ResponseEntity.ok(observacaoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObservacaoDTO> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ObservacaoDTO dto) {
        return ResponseEntity.ok(observacaoService.atualizar(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<ObservacaoDTO>> listarTodas() {
        return ResponseEntity.ok(observacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservacaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(observacaoService.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ObservacaoDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(observacaoService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ObservacaoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(observacaoService.listarPorUsuario(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        observacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}