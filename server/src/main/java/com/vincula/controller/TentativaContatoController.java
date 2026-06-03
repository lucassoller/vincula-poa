package com.vincula.controller;

import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.service.TentativaContatoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PreAuthorize("hasAnyRole('USUARIO_APS','GESTAO_MUNICIPAL')")
    @PostMapping
    public ResponseEntity<TentativaContatoResponseDTO> criar(@Valid @RequestBody TentativaContatoDTO dto) {
        return ResponseEntity.ok(tentativaService.criar(dto));
    }

    @PreAuthorize("hasAnyRole('USUARIO_APS','GESTAO_MUNICIPAL')")
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

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping
    public ResponseEntity<Page<TentativaContatoResponseDTO>> listarTodas(Pageable pageable) {
        return ResponseEntity.ok(tentativaService.listarTodas(pageable));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tentativaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}