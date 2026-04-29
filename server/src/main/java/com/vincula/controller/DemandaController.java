package com.vincula.controller;

import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
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
    public ResponseEntity<DemandaResponseDTO> criar(@Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.criar(dto));
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<DemandaResponseDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.atualizar(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<DemandaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(demandaService.listarTodas());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<DemandaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(demandaService.buscarPorId(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DemandaResponseDTO>> listarPorPacienteEStatus(
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
    public ResponseEntity<List<DemandaResponseDTO>> listarPorUnidadeSaudeEStatus(
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
    public ResponseEntity<List<DemandaResponseDTO>> listarPorUsuarioCriadorEStatus(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) StatusDemanda status
    ) {
        if (status != null) {
            return ResponseEntity.ok(demandaService.listarPorUsuarioCriadorEStatus(usuarioId, status));
        }

        return ResponseEntity.ok(demandaService.listarPorUsuarioCriador(usuarioId));
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS','GESTAO_MUNICIPAL')")
    @PatchMapping("/{id}/redirecionar")
    public ResponseEntity<DemandaResponseDTO> redirecionar(@PathVariable Long id,
                                                   @Valid @RequestBody RedirecionarDemandaDTO dto) {
        return ResponseEntity.ok(demandaService.redirecionar(id, dto));
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<DemandaResponseDTO> encerrar(@PathVariable Long id,
                                                       @Valid @RequestBody EncerrarDemandaDTO dto) {

        return ResponseEntity.ok(demandaService.encerrar(id, dto));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        demandaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}