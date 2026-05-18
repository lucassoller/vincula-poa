package com.vincula.controller;

import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.paciente.PacienteDTO;
import com.vincula.dto.paciente.PacienteResponseDTO;
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

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> criar(@Valid @RequestBody PacienteDTO dto) {
        PacienteResponseDTO pacienteCriado = pacienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    /*@PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        List<PacienteResponseDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unidadeSaude/{id}")
    public ResponseEntity<List<PacienteResponseDTO>> listarTodosPorUnidade(@PathVariable Long id) {
        List<PacienteResponseDTO> pacientes = pacienteService.listarTodosPorUnidade(id);
        return ResponseEntity.ok(pacientes);
    }*/

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<PacienteResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(pacienteService.listarTodos(pageable));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unidadeSaude/{unidadeSaudeId}")
    public ResponseEntity<Page<PacienteResponseDTO>> listarPorUnidade(
            @PathVariable Long unidadeSaudeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                pacienteService.listarTodosPorUnidade(unidadeSaudeId, pageable)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        PacienteResponseDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/documento/{documento}")
    public ResponseEntity<PacienteResponseDTO> buscarPorDocumento(@PathVariable String documento) {
        PacienteResponseDTO paciente = pacienteService.buscarPorDocumento(documento);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
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