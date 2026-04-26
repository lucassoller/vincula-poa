package com.vincula.controller;

import com.vincula.dto.paciente.PacienteDTO;
import com.vincula.dto.paciente.PacienteResponseDTO;
import com.vincula.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> criar(@Valid @RequestBody PacienteDTO dto) {
        PacienteResponseDTO pacienteCriado = pacienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        List<PacienteResponseDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        PacienteResponseDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        PacienteResponseDTO paciente = pacienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cns/{cns}")
    public ResponseEntity<PacienteResponseDTO> buscarPorCns(@PathVariable String cns) {
        PacienteResponseDTO paciente = pacienteService.buscarPorCns(cns);
        return ResponseEntity.ok(paciente);
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteDTO dto) {
        PacienteResponseDTO pacienteAtualizado = pacienteService.atualizar(id, dto);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}