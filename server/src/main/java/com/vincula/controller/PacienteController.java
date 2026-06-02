package com.vincula.controller;

import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.paciente.PacienteDTO;
import com.vincula.dto.paciente.PacienteResponseDTO;
import com.vincula.dto.paciente.PacienteShortResponseDTO;
import com.vincula.service.DemandaService;
import com.vincula.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final DemandaService demandaService;

    public PacienteController(PacienteService pacienteService, DemandaService demandaService) {
        this.pacienteService = pacienteService;
        this.demandaService = demandaService;
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS', 'GESTAO_MUNICIPAL')")
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> criar(@Valid @RequestBody PacienteDTO dto) {
        PacienteResponseDTO pacienteCriado = pacienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping
    public ResponseEntity<Page<PacienteResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(pacienteService.listarTodos(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity<List<PacienteShortResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS', 'GESTAO_MUNICIPAL')")
    @GetMapping("/unidadeSaude/{unidadeSaudeId}")
    public ResponseEntity<Page<PacienteResponseDTO>> listarPorUnidade(
            @PathVariable Long unidadeSaudeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                pacienteService.listarTodosPorUnidade(unidadeSaudeId, pageable)
        );
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping("/filtrados/{filtro}")
    public ResponseEntity<Page<PacienteResponseDTO>> listarTodosFiltrados(@PathVariable String filtro, Pageable pageable) {
        return ResponseEntity.ok(pacienteService.listarTodosFiltrados(filtro, pageable));
    }


    @PreAuthorize("hasAnyRole('EXECUTOR_APS', 'GESTAO_MUNICIPAL')")
    @GetMapping("/filtrados/unidadeSaude/{unidadeSaudeId}/{filtro}")
    public ResponseEntity<Page<PacienteResponseDTO>> listarPorUnidade(
            @PathVariable Long unidadeSaudeId,
            @PathVariable String filtro,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                pacienteService.listarTodosPorUnidadeFiltrados(unidadeSaudeId, filtro, pageable)
        );
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS', 'GESTAO_MUNICIPAL')")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        PacienteResponseDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS', 'GESTAO_MUNICIPAL')")
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteDTO dto) {
        PacienteResponseDTO pacienteAtualizado = pacienteService.atualizar(id, dto);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @PreAuthorize("hasAnyRole('EXECUTOR_APS','GESTAO_MUNICIPAL')")
    @PatchMapping("/{id}/redirecionar-abertas")
    public ResponseEntity<Void> redirecionarDemandasAbertas(
            @PathVariable Long id,
            @Valid @RequestBody RedirecionarDemandaDTO dto
    ) {
        demandaService.redirecionarDemandasAbertasDoPaciente(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}