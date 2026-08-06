package com.vincula.controller;

import com.vincula.dto.senha.MudancaSenhaDTO;
import com.vincula.dto.servidor.*;
import com.vincula.service.ServidorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servidores")
public class ServidorController {

    private final ServidorService servidorService;

    public ServidorController(ServidorService servidorService) {
        this.servidorService = servidorService;
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PostMapping
    public ResponseEntity<ServidorResponseDTO> criar(@Valid @RequestBody ServidorDTO dto) {
        ServidorResponseDTO criado = servidorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<ServidorResponseDTO>> listarTodosFiltradas(
            @RequestBody FiltroServidorRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(servidorService.listarTodosFiltrados(filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filtrados/buscas")
    public ResponseEntity<List<ServidorShortResponseDTO>> listarTodosFiltradosPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(servidorService.listarTodosFiltradosPorNome(nome));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ServidorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servidorService.buscarPorId(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/email/{email}")
    public ResponseEntity<ServidorResponseDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(servidorService.buscarPorEmail(email));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login/{login}")
    public ResponseEntity<ServidorResponseDTO> buscarPorLogin(@PathVariable String login) {
        return ResponseEntity.ok(servidorService.buscarPorLogin(login));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ServidorResponseDTO> getServidorLogado() {
        return ResponseEntity.ok(servidorService.getServidorAutenticadoDTO());
    }

    // COMENTAR ROLE PARA CADASTRAR SERVIDOR
    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PutMapping("/{id}")
    public ResponseEntity<ServidorResponseDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody ServidorDTO dto) {
        return ResponseEntity.ok(servidorService.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PutMapping("/transferir/{id}")
    public ResponseEntity<ServidorResponseDTO> transferir(@PathVariable Long id,
                                                         @Valid @RequestBody TransferirServidorDTO dto) {
        return ResponseEntity.ok(servidorService.transferirServidor(id, dto));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServidorResponseDTO> atualizarMeuPerfil(
            @Valid @RequestBody MeuPerfilDTO dto
    ) {
        return ResponseEntity.ok(servidorService.atualizarMeuPerfil(dto));
    }

    @PutMapping("/me/senha")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServidorResponseDTO> alterarMinhaSenha(@Valid @RequestBody MudancaSenhaDTO dto)
    {
        return ResponseEntity.ok(servidorService.atualizarMinhaSenha(dto));
    }

    @PutMapping("/servidores/{id}/senha")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id,
                                             @RequestBody MudancaSenhaDTO dto) {

        servidorService.alterarSenha(id, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servidorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}