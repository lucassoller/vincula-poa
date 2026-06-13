package com.vincula.service;

import com.vincula.dto.login.LoginRequestDTO;
import com.vincula.dto.login.LoginResponseDTO;
import com.vincula.entity.Servidor;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ServidorRepository;
import com.vincula.security.CustomUserDetailsService;
import com.vincula.security.JwtService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private ServidorRepository servidorRepository;

    @Mock
    private  AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private LoginService loginService;

    @Test
    void deveRealizarLogin() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("lucas");
        dto.setSenha("123");

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");
        servidor.setLogin("lucas");
        servidor.setEmail("lucas@email.com");
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);
        servidor.setAtivo(true);

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(10L);
        unidade.setNome("UBS Centro");

        servidor.setUnidadeSaude(unidade);

        UserDetails userDetails = mock(UserDetails.class);

        when(servidorRepository.findByLogin("lucas"))
                .thenReturn(Optional.of(servidor));

        when(customUserDetailsService.loadUserByUsername("lucas"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        LoginResponseDTO response =
                loginService.login(dto);

        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("Lucas", response.getNome());

        verify(auditoriaFacade)
                .loginRealizado(servidor);
    }

    @Test
    void deveLancarExceptionQuandoServidorInativo() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("lucas");
        dto.setSenha("123");

        doThrow(new DisabledException("Inativo"))
                .when(authenticationManager)
                .authenticate(any());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> loginService.login(dto)
        );

        assertEquals("Servidor inativo", ex.getMessage());
    }

    @Test
    void deveLancarExceptionQuandoLoginInvalido() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("lucas");
        dto.setSenha("123");

        doThrow(new org.springframework.security.authentication.BadCredentialsException("erro"))
                .when(authenticationManager)
                .authenticate(any());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> loginService.login(dto)
        );

        assertEquals(
                "Login ou senha inválidos",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarNotFoundQuandoServidorNaoExiste() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("lucas");
        dto.setSenha("123");

        when(servidorRepository.findByLogin("lucas"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> loginService.login(dto)
        );
    }

    @Test
    void deveRealizarLoginSemUnidadeSaude() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("lucas");
        dto.setSenha("123");

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");
        servidor.setLogin("lucas");
        servidor.setEmail("lucas@email.com");
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);
        servidor.setAtivo(true);

        UserDetails userDetails = mock(UserDetails.class);

        when(servidorRepository.findByLogin("lucas"))
                .thenReturn(Optional.of(servidor));

        when(customUserDetailsService.loadUserByUsername("lucas"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("token");

        LoginResponseDTO response =
                loginService.login(dto);

        assertNull(response.getUnidadeSaudeId());
        assertNull(response.getUnidadeSaude());
    }
}