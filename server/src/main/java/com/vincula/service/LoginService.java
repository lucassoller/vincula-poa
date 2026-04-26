package com.vincula.service;

import com.vincula.dto.login.LoginRequestDTO;
import com.vincula.dto.login.LoginResponseDTO;
import com.vincula.entity.Usuario;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.UsuarioRepository;
import com.vincula.security.CustomUserDetailsService;
import com.vincula.security.JwtService;
import com.vincula.util.AuditoriaFacade;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaFacade auditoriaFacade;

    public LoginService(AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        CustomUserDetailsService customUserDetailsService,
                        UsuarioRepository usuarioRepository,
                        AuditoriaFacade auditoriaFacade) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaFacade = auditoriaFacade;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getLogin(),
                            dto.getSenha()
                    )
            );
        } catch (DisabledException ex) {
            auditoriaFacade.loginFalhou(dto.getLogin());
            throw new BusinessException("Usuário inativo");
        } catch (AuthenticationException ex) {
            auditoriaFacade.loginFalhou(dto.getLogin());
            throw new BusinessException("Login ou senha inválidos");
        }

        Usuario usuario = buscarUsuarioPorLogin(dto.getLogin());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getLogin());
        String token = jwtService.generateToken(userDetails);

        auditoriaFacade.loginRealizado(usuario);

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil(),
                usuario.getAtivo()
        );
    }

    private Usuario buscarUsuarioPorLogin(String login){
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
