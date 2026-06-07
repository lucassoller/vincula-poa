package com.vincula.service;

import com.vincula.dto.senha.RecuperarSenhaDTO;
import com.vincula.dto.senha.RedefinirSenhaDTO;
import com.vincula.entity.RecuperacaoSenha;
import com.vincula.entity.Servidor;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.RecuperacaoSenhaRepository;
import com.vincula.repository.ServidorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.env.Environment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {

    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final ServidorRepository servidorRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final Environment env;

    public RecuperacaoSenhaService(RecuperacaoSenhaRepository recuperacaoSenhaRepository,
                                   ServidorRepository servidorRepository,
                                   PasswordEncoder encoder,
                                   EmailService emailService, Environment env){
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.servidorRepository = servidorRepository;
        this.encoder = encoder;
        this.emailService = emailService;
        this.env = env;
    }

    public void recuperarSenha(RecuperarSenhaDTO dto) throws IOException {
        Servidor servidor = findServidor(dto.getEmail());
        String token = UUID.randomUUID().toString();

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();
        recuperacao.setToken(token);
        recuperacao.setServidor(servidor);
        recuperacao.setExpiracao(LocalDateTime.now().plusMinutes(30));
        recuperacao.setUsado(false);

        recuperacaoSenhaRepository.save(recuperacao);
        String baseUrl = env.getProperty("frontend.url");
        String link = baseUrl + "/redefinir-senha?token=" + token;

        emailService.enviarEmail(servidor.getEmail(), link);
    }

    public void redefinirSenha(RedefinirSenhaDTO dto) {
        RecuperacaoSenha recuperacao = findRecuperacao(dto.getToken());

        if (recuperacao.getUsado()) {
            throw new BusinessException("Link já utilizado");
        }

        if (recuperacao.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Link expirado");
        }

        Servidor servidor = recuperacao.getServidor();
        servidor.setSenhaHash(encoder.encode(dto.getNovaSenha()));
        servidorRepository.save(servidor);

        recuperacao.setUsado(true);
        recuperacaoSenhaRepository.save(recuperacao);
    }

    @Scheduled(fixedRate = 3600000) // a cada 1 hora
    @Transactional
    public void limparTokensExpirados() {
        recuperacaoSenhaRepository.deleteByExpiracaoBefore(
                LocalDateTime.now().minusHours(24)
        );
    }

    private RecuperacaoSenha findRecuperacao(String token) {
        return recuperacaoSenhaRepository
                .findByToken(token)
                .orElseThrow(() -> new BusinessException("Link inválido"));
    }

    private Servidor findServidor(String email){
        return servidorRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Servidor não encontrado"));
    }
}