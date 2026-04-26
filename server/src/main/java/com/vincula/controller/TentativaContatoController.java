package com.vincula.controller;

import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.service.TentativaContatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tentativas-contato")
public class TentativaContatoController {

    private final TentativaContatoService tentativaService;

    public TentativaContatoController(TentativaContatoService tentativaService) {
        this.tentativaService = tentativaService;
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PostMapping
    public ResponseEntity<TentativaContatoResponseDTO> criar(@Valid @RequestBody TentativaContatoDTO dto) {
        return ResponseEntity.ok(tentativaService.criar(dto));
    }

    @PreAuthorize("hasRole('EXECUTOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<TentativaContatoResponseDTO> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody TentativaContatoDTO dto) {
        return ResponseEntity.ok(tentativaService.atualizar(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demanda/{demandaId}")
    public ResponseEntity<List<TentativaContatoResponseDTO>> listarPorDemanda(@PathVariable Long demandaId) {
        return ResponseEntity.ok(tentativaService.listarPorDemanda(demandaId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TentativaContatoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(tentativaService.listarPorUsuario(usuarioId));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tentativaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}