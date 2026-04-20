package com.vincula.controller;

import com.vincula.dto.UsuarioDTO;
import com.vincula.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO criado = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    //@PreAuthorize("isAuthenticated()")
    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<UsuarioDTO> buscarPorLogin(@PathVariable String login) {
        return ResponseEntity.ok(usuarioService.buscarPorLogin(login));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}