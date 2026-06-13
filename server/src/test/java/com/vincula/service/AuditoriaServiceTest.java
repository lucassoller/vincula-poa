package com.vincula.service;

import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.entity.Auditoria;
import com.vincula.entity.Servidor;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.repository.AuditoriaRepository;
import com.vincula.repository.ServidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private AuditoriaService auditoriaService;

    @Test
    void deveRegistrarAuditoria() {

        Auditoria auditoriaSalva = new Auditoria();

        when(auditoriaRepository.save(any(Auditoria.class)))
                .thenReturn(auditoriaSalva);

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "Teste"
        );

        verify(auditoriaRepository)
                .save(any(Auditoria.class));
    }

    @Test
    void deveRegistrarAuditoriaComServidor() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        auditoriaService.registrarComServidor(
                servidor,
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "Teste"
        );

        verify(auditoriaRepository)
                .save(any(Auditoria.class));
    }

    @Test
    void deveListarAuditorias() {

        Auditoria auditoria = new Auditoria();

        Page<Auditoria> page =
                new PageImpl<>(List.of(auditoria));

        when(auditoriaRepository
                .findAllByOrderByDataHoraDesc(any(Pageable.class)))
                .thenReturn(page);

        Page<AuditoriaDTO> resultado =
                auditoriaService.listarTodos(PageRequest.of(0, 10));

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveListarPorServidor() {

        Auditoria auditoria = new Auditoria();

        Page<Auditoria> page =
                new PageImpl<>(List.of(auditoria));

        when(auditoriaRepository
                .findByServidorIdOrderByDataHoraDesc(
                        eq(1L),
                        any(Pageable.class)))
                .thenReturn(page);

        Page<AuditoriaDTO> resultado =
                auditoriaService.listarPorServidor(
                        1L,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveListarPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        Page<Auditoria> page =
                new PageImpl<>(List.of(new Auditoria()));

        when(auditoriaRepository
                .findByDataHoraBetweenOrderByDataHoraDesc(
                        eq(inicio),
                        eq(fim),
                        any(Pageable.class)))
                .thenReturn(page);

        Page<AuditoriaDTO> resultado =
                auditoriaService.listarPorPeriodo(
                        inicio,
                        fim,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveListarPorServidorEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        Page<Auditoria> page =
                new PageImpl<>(List.of(new Auditoria()));

        when(auditoriaRepository
                .findByServidorIdAndDataHoraBetweenOrderByDataHoraDesc(
                        eq(1L),
                        eq(inicio),
                        eq(fim),
                        any(Pageable.class)))
                .thenReturn(page);

        Page<AuditoriaDTO> resultado =
                auditoriaService.listarPorServidorEPeriodo(
                        1L,
                        inicio,
                        fim,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveConverterParaDTOComServidor() {

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        Auditoria auditoria = new Auditoria();
        auditoria.setId(10L);
        auditoria.setServidor(servidor);

        AuditoriaDTO dto =
                auditoriaService.toDTO(auditoria);

        assertEquals(1L, dto.getServidorId());
        assertEquals("Lucas", dto.getServidorNome());
    }

    @Test
    void deveConverterParaDTOSemServidor() {

        Auditoria auditoria = new Auditoria();

        AuditoriaDTO dto =
                auditoriaService.toDTO(auditoria);

        assertNull(dto.getServidorId());
        assertNull(dto.getServidorNome());
    }

    @Test
    void deveRegistrarComServidorLogado() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn("login");

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        Servidor servidor = new Servidor();
        servidor.setLogin("login");

        when(servidorRepository.findByLogin("login"))
                .thenReturn(Optional.of(servidor));

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(any(Auditoria.class));
    }

    @Test
    void deveRegistrarSemServidorQuandoAuthenticationNula() {

        SecurityContextHolder.clearContext();

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        a.getServidor() == null
                ));
    }

    @Test
    void deveRegistrarSemServidorQuandoAnonymous() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn("anonymousUser");

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        a.getServidor() == null
                ));
    }

    @Test
    void deveUsarRemoteAddrQuandoNaoExisteHeader() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRemoteAddr("127.0.0.1");

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        "127.0.0.1".equals(a.getIp())
                ));
    }

    @Test
    void deveUsarXForwardedFor() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "X-Forwarded-For",
                "192.168.1.10"
        );

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        "192.168.1.10".equals(a.getIp())
                ));
    }

    @Test
    void deveRetornarIpNuloQuandoNaoExisteRequest() {

        RequestContextHolder.resetRequestAttributes();

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        a.getIp() == null
                ));
    }

    @Test
    void deveRetornarNullQuandoOcorreExcecaoAoBuscarServidorLogado() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenThrow(new RuntimeException("erro"));

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        a.getServidor() == null
                ));
    }

    @Test
    void deveRetornarIpNuloQuandoOcorreExcecaoAoObterIp() {

        ServletRequestAttributes attributes =
                mock(ServletRequestAttributes.class);

        when(attributes.getRequest())
                .thenThrow(new RuntimeException("erro"));

        RequestContextHolder.setRequestAttributes(attributes);

        auditoriaService.registrar(
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Servidor",
                1L,
                "teste"
        );

        verify(auditoriaRepository)
                .save(argThat(a ->
                        a.getIp() == null
                ));
    }
}