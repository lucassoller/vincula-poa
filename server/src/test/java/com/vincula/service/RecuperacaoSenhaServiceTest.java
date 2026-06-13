package com.vincula.service;

import com.vincula.dto.senha.RecuperarSenhaDTO;
import com.vincula.dto.senha.RedefinirSenhaDTO;
import com.vincula.entity.RecuperacaoSenha;
import com.vincula.entity.Servidor;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.RecuperacaoSenhaRepository;
import com.vincula.repository.ServidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock
    private RecuperacaoSenhaRepository recuperacaoSenhaRepository;

    @Mock
    private ServidorRepository servidorRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private EmailService emailService;

    @Mock
    private Environment env;

    @InjectMocks
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @Test
    void deveRecuperarSenha() throws IOException {

        RecuperarSenhaDTO dto = new RecuperarSenhaDTO();
        dto.setEmail("teste@email.com");

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setEmail("teste@email.com");

        when(servidorRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(servidor));

        when(env.getProperty("frontend.url"))
                .thenReturn("http://localhost:3000");

        recuperacaoSenhaService.recuperarSenha(dto);

        verify(recuperacaoSenhaRepository)
                .save(any(RecuperacaoSenha.class));

        verify(emailService)
                .enviarEmail(eq("teste@email.com"), contains("token="));
    }

    @Test
    void deveLancarNotFoundQuandoServidorNaoExiste() {

        RecuperarSenhaDTO dto = new RecuperarSenhaDTO();
        dto.setEmail("inexistente@email.com");

        when(servidorRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> recuperacaoSenhaService.recuperarSenha(dto)
        );
    }

    @Test
    void deveLancarExceptionQuandoTokenInvalido() {

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setToken("abc");

        when(recuperacaoSenhaRepository.findByToken("abc"))
                .thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> recuperacaoSenhaService.redefinirSenha(dto)
        );
    }

    @Test
    void deveLancarExceptionQuandoLinkJaFoiUtilizado() {

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();
        recuperacao.setUsado(true);

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setToken("abc");

        when(recuperacaoSenhaRepository.findByToken("abc"))
                .thenReturn(Optional.of(recuperacao));

        assertThrows(
                BusinessException.class,
                () -> recuperacaoSenhaService.redefinirSenha(dto)
        );
    }

    @Test
    void deveLancarExceptionQuandoLinkExpirado() {

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();
        recuperacao.setUsado(false);
        recuperacao.setExpiracao(LocalDateTime.now().minusMinutes(1));

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setToken("abc");

        when(recuperacaoSenhaRepository.findByToken("abc"))
                .thenReturn(Optional.of(recuperacao));

        assertThrows(
                BusinessException.class,
                () -> recuperacaoSenhaService.redefinirSenha(dto)
        );
    }

    @Test
    void deveRedefinirSenha() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        RecuperacaoSenha recuperacao = new RecuperacaoSenha();
        recuperacao.setServidor(servidor);
        recuperacao.setUsado(false);
        recuperacao.setExpiracao(LocalDateTime.now().plusMinutes(30));

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setToken("abc");
        dto.setNovaSenha("123456");

        when(recuperacaoSenhaRepository.findByToken("abc"))
                .thenReturn(Optional.of(recuperacao));

        when(encoder.encode("123456"))
                .thenReturn("hash");

        recuperacaoSenhaService.redefinirSenha(dto);

        assertTrue(recuperacao.getUsado());

        verify(servidorRepository)
                .save(servidor);

        verify(recuperacaoSenhaRepository)
                .save(recuperacao);
    }

    @Test
    void deveLimparTokensExpirados() {

        recuperacaoSenhaService.limparTokensExpirados();

        verify(recuperacaoSenhaRepository)
                .deleteByExpiracaoBefore(any(LocalDateTime.class));
    }
}