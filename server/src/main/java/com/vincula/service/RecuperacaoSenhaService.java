package com.vincula.service;

import com.vincula.dto.senha.RecuperarSenhaDTO;
import com.vincula.dto.senha.RedefinirSenhaDTO;
import com.vincula.entity.RecuperacaoSenha;
import com.vincula.entity.Usuario;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.RecuperacaoSenhaRepository;
import com.vincula.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {

    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public RecuperacaoSenhaService(RecuperacaoSenhaRepository recuperacaoSenhaRepository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder encoder,
                                   EmailService emailService){
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    public void recuperarSenha(RecuperarSenhaDTO dto) throws MessagingException, IOException {
        Usuario usuario = findUsuario(dto.getEmail());
        String token = UUID.randomUUID().toString();

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();
        recuperacao.setToken(token);
        recuperacao.setUsuario(usuario);
        recuperacao.setExpiracao(LocalDateTime.now().plusMinutes(30));
        recuperacao.setUsado(false);

        recuperacaoSenhaRepository.save(recuperacao);
        String link = "http://localhost:5173/redefinir-senha?token=" + token;

        emailService.enviarEmail(usuario.getEmail(), link);
    }

    public void redefinirSenha(RedefinirSenhaDTO dto) {
        RecuperacaoSenha recuperacao = findRecuperacao(dto.getToken());

        if (recuperacao.getUsado()) {
            throw new BusinessException("Link já utilizado");
        }

        if (recuperacao.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Link expirado");
        }

        Usuario usuario = recuperacao.getUsuario();
        usuario.setSenhaHash(encoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);

        recuperacao.setUsado(true);
        recuperacaoSenhaRepository.save(recuperacao);
    }

    private RecuperacaoSenha findRecuperacao(String token) {
        return recuperacaoSenhaRepository
                .findByToken(token)
                .orElseThrow(() -> new BusinessException("Link inválido"));
    }

    private Usuario findUsuario(String email){
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}