package com.vincula.controller;

import com.vincula.dto.PacienteDTO;
import com.vincula.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteDTO> criar(@Valid @RequestBody PacienteDTO dto) {
        PacienteDTO pacienteCriado = pacienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        List<PacienteDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> buscarPorId(@PathVariable Long id) {
        PacienteDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteDTO> buscarPorCpf(@PathVariable String cpf) {
        PacienteDTO paciente = pacienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/cns/{cns}")
    public ResponseEntity<PacienteDTO> buscarPorCns(@PathVariable String cns) {
        PacienteDTO paciente = pacienteService.buscarPorCns(cns);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("endereco")
    public ResponseEntity<List<PacienteDTO>> listarTodosComEndereco() {
        List<PacienteDTO> pacientes = pacienteService.listarTodosComEndereco();
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("endereco/{id}")
    public ResponseEntity<PacienteDTO> buscarPorIdComEndereco(@PathVariable Long id) {
        PacienteDTO paciente = pacienteService.buscarPorIdComEndereco(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("endereco/cpf/{cpf}")
    public ResponseEntity<PacienteDTO> buscarPorCpfComEndereco(@PathVariable String cpf) {
        PacienteDTO paciente = pacienteService.buscarPorCpfComEndereco(cpf);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("endereco/cns/{cns}")
    public ResponseEntity<PacienteDTO> buscarPorCnsComEndereco(@PathVariable String cns) {
        PacienteDTO paciente = pacienteService.buscarPorCnsComEndereco(cns);
        return ResponseEntity.ok(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteDTO dto) {
        PacienteDTO pacienteAtualizado = pacienteService.atualizar(id, dto);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}