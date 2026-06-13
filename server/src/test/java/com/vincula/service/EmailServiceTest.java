package com.vincula.service;

import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @Mock
    private Environment env;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> response;

    @InjectMocks
    private EmailService emailService;

    @Test
    void deveEnviarEmailComSucesso() throws Exception {

        when(env.getProperty("mail.token"))
                .thenReturn("token");

        when(response.statusCode())
                .thenReturn(200);

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        emailService.enviarEmail(
                "teste@email.com",
                "http://localhost"
        );

        verify(auditoriaFacade)
                .emailEnviado("teste@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoApiRetornaErro() throws Exception {

        when(env.getProperty("mail.token"))
                .thenReturn("token");

        when(response.statusCode())
                .thenReturn(500);

        when(response.body())
                .thenReturn("erro");

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> emailService.enviarEmail(
                        "teste@email.com",
                        "http://localhost"
                )
        );

        assertTrue(ex.getMessage().contains("Falha ao enviar email"));

        verify(auditoriaFacade, atLeastOnce())
                .emailFalhou("teste@email.com");
    }

    @Test
    void deveRegistrarFalhaQuandoHttpClientLancaExcecao() throws Exception {

        when(env.getProperty("mail.token"))
                .thenReturn("token");

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Erro conexão"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> emailService.enviarEmail(
                        "teste@email.com",
                        "http://localhost"
                )
        );

        assertEquals("Erro conexão", ex.getMessage());

        verify(auditoriaFacade)
                .emailFalhou("teste@email.com");
    }
}