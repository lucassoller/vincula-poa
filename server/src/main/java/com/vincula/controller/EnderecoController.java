package com.vincula.controller;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
@PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> criar(@Valid @RequestBody EnderecoDTO dto) {
        EnderecoResponseDTO enderecoCriado = enderecoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoCriado);
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
        List<EnderecoResponseDTO> enderecos = enderecoService.listarTodos();
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Long id) {
        EnderecoResponseDTO endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(endereco);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(@PathVariable Long id,
                                                 @Valid @RequestBody EnderecoDTO dto) {
        EnderecoResponseDTO enderecoAtualizado = enderecoService.atualizar(id, dto);
        return ResponseEntity.ok(enderecoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}