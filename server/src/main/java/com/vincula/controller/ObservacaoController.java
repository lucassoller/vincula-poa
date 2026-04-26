package com.vincula.controller;

import com.vincula.dto.observacao.ObservacaoDTO;
import com.vincula.dto.observacao.ObservacaoResponseDTO;
import com.vincula.service.ObservacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/observacoes")
public class ObservacaoController {

    private final ObservacaoService observacaoService;

    public ObservacaoController(ObservacaoService observacaoService) {
        this.observacaoService = observacaoService;
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PostMapping
    public ResponseEntity<ObservacaoResponseDTO> criar(@Valid @RequestBody ObservacaoDTO dto) {
        return ResponseEntity.ok(observacaoService.criar(dto));
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<ObservacaoResponseDTO> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ObservacaoDTO dto) {
        return ResponseEntity.ok(observacaoService.atualizar(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ObservacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(observacaoService.listarTodas());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ObservacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(observacaoService.buscarPorId(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ObservacaoResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(observacaoService.listarPorPaciente(pacienteId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ObservacaoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(observacaoService.listarPorUsuario(usuarioId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        observacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}