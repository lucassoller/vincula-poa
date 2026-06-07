package com.vincula.controller;

import com.vincula.dto.senha.RecuperarSenhaDTO;
import com.vincula.dto.senha.RedefinirSenhaDTO;
import com.vincula.service.RecuperacaoSenhaService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

@RestController
@RequestMapping("/auth")
public class RecuperacaoSenhaController {
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoSenhaService) {
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> recuperarSenha(@Valid @RequestBody RecuperarSenhaDTO dto) throws MessagingException, IOException {
        recuperacaoSenhaService.recuperarSenha(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teste-smtp")
    public String teste() {
        try (Socket socket = new Socket()) {
            System.out.println(InetAddress.getByName("smtp.gmail.com"));
            socket.connect(new InetSocketAddress("google.com", 443), 10000);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO dto) {
        recuperacaoSenhaService.redefinirSenha(dto);
        return ResponseEntity.ok().build();
    }
}