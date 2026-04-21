package com.vincula.controller;

import com.vincula.dto.DemandaDTO;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.StatusDemanda;
import com.vincula.service.DemandaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demandas")
public class DemandaController {

    private final DemandaService demandaService;

    public DemandaController(DemandaService demandaService) {
        this.demandaService = demandaService;
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PostMapping
    public ResponseEntity<DemandaDTO> criar(@Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.criar(dto));
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<DemandaDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.atualizar(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<DemandaDTO>> listarTodas() {
        return ResponseEntity.ok(demandaService.listarTodas());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<DemandaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(demandaService.buscarPorId(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DemandaDTO>> listarPorPacienteEStatus(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) StatusDemanda status
    ) {
        if (status != null) {
            return ResponseEntity.ok(demandaService.listarPorPacienteEStatus(pacienteId, status));
        }

        return ResponseEntity.ok(demandaService.listarPorPaciente(pacienteId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unidade/{unidadeSaudeId}")
    public ResponseEntity<List<DemandaDTO>> listarPorUnidadeSaudeEStatus(
            @PathVariable Long unidadeSaudeId,
            @RequestParam(required = false) StatusDemanda status
    ) {
        if (status != null) {
            return ResponseEntity.ok(demandaService.listarPorUnidadeSaudeEStatus(unidadeSaudeId, status));
        }

        return ResponseEntity.ok(demandaService.listarPorUnidadeSaude(unidadeSaudeId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DemandaDTO>> listarPorUsuarioCriadorEStatus(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) StatusDemanda status
    ) {
        if (status != null) {
            return ResponseEntity.ok(demandaService.listarPorUsuarioCriadorEStatus(usuarioId, status));
        }

        return ResponseEntity.ok(demandaService.listarPorUsuarioCriador(usuarioId));
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<DemandaDTO> encerrar(@PathVariable Long id,
                                               @RequestParam DesfechoDemanda desfecho,
                                               @RequestParam(required = false) String descricao) {

        return ResponseEntity.ok(demandaService.encerrar(id, desfecho, descricao));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        demandaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}