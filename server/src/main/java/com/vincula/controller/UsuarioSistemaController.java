package com.vincula.controller;

import com.vincula.dto.UsuarioSistemaDTO;
import com.vincula.service.UsuarioSistemaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios-sistema")
public class UsuarioSistemaController {

    private final UsuarioSistemaService usuarioSistemaService;

    public UsuarioSistemaController(UsuarioSistemaService usuarioSistemaService) {
        this.usuarioSistemaService = usuarioSistemaService;
    }

    @PostMapping
    public ResponseEntity<UsuarioSistemaDTO> criar(@Valid @RequestBody UsuarioSistemaDTO dto) {
        UsuarioSistemaDTO criado = usuarioSistemaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSistemaDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioSistemaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioSistemaService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioSistemaDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioSistemaService.buscarPorEmail(email));
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<UsuarioSistemaDTO> buscarPorLogin(@PathVariable String login) {
        return ResponseEntity.ok(usuarioSistemaService.buscarPorLogin(login));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody UsuarioSistemaDTO dto) {
        return ResponseEntity.ok(usuarioSistemaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioSistemaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}