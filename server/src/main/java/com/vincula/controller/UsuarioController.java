package com.vincula.controller;

import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.usuario.FiltroUsuarioRequestDTO;
import com.vincula.dto.usuario.UsuarioDTO;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.dto.usuario.UsuarioShortResponseDTO;
import com.vincula.service.DemandaService;
import com.vincula.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final DemandaService demandaService;

    public UsuarioController(UsuarioService usuarioService, DemandaService demandaService) {
        this.usuarioService = usuarioService;
        this.demandaService = demandaService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioResponseDTO usuarioCriado = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity<List<UsuarioShortResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodosFiltradas(
            @RequestBody FiltroUsuarioRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodosFiltrados(filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filtrados/busca/{filtro}")
    public ResponseEntity<List<UsuarioShortResponseDTO>> listarTodosFiltradosPorNomeOuDocumento(@PathVariable String filtro) {
        return ResponseEntity.ok(usuarioService.listarTodosFiltradosPorNomeOuDocumento(filtro));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filtrados/buscas")
    public ResponseEntity<List<UsuarioShortResponseDTO>> listarTodosFiltradosPorNomeCompleto(@RequestParam String nomeCompleto) {
        return ResponseEntity.ok(usuarioService.listarTodosFiltradosPorNomeCompleto(nomeCompleto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/redirecionar-abertas")
    public ResponseEntity<Void> redirecionarDemandasAbertas(
            @PathVariable Long id,
            @Valid @RequestBody RedirecionarDemandaDTO dto
    ) {
        demandaService.redirecionarDemandasAbertasDoUsuario(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}