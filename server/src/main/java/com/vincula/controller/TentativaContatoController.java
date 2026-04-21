package com.vincula.controller;

import com.vincula.dto.TentativaContatoDTO;
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
    public ResponseEntity<TentativaContatoDTO> criar(@Valid @RequestBody TentativaContatoDTO dto) {
        return ResponseEntity.ok(tentativaService.criar(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demanda/{demandaId}")
    public ResponseEntity<List<TentativaContatoDTO>> listarPorDemanda(@PathVariable Long demandaId) {
        return ResponseEntity.ok(tentativaService.listarPorDemanda(demandaId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TentativaContatoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(tentativaService.listarPorUsuario(usuarioId));
    }
}