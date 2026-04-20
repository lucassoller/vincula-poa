package com.vincula.service;

import com.vincula.dto.LoginRequestDTO;
import com.vincula.dto.LoginResponseDTO;
import com.vincula.entity.Usuario;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.UsuarioRepository;
import com.vincula.security.CustomUserDetailsService;
import com.vincula.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UsuarioRepository usuarioRepository;

    public LoginService(AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        CustomUserDetailsService customUserDetailsService,
                        UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.usuarioRepository = usuarioRepository;
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
            throw new BusinessException("Usuário inativo");
        } catch (AuthenticationException ex) {
            throw new BusinessException("Login ou senha inválidos");
        }

        Usuario usuario = usuarioRepository.findByLogin(dto.getLogin())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getLogin());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil(),
                usuario.getAtivo()
        );
    }
}
