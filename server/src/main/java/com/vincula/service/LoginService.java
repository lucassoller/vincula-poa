package com.vincula.service;

import com.vincula.dto.login.LoginRequestDTO;
import com.vincula.dto.login.LoginResponseDTO;
import com.vincula.entity.Servidor;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.ServidorRepository;
import com.vincula.security.CustomUserDetailsService;
import com.vincula.security.JwtService;
import com.vincula.util.AuditoriaFacade;
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
    private final ServidorRepository servidorRepository;
    private final AuditoriaFacade auditoriaFacade;
    private final PasswordEncoder passwordEncoder;

    public LoginService(AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        CustomUserDetailsService customUserDetailsService,
                        ServidorRepository servidorRepository,
                        AuditoriaFacade auditoriaFacade, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.servidorRepository = servidorRepository;
        this.auditoriaFacade = auditoriaFacade;
        this.passwordEncoder = passwordEncoder;
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
            throw new BusinessException("Servidor inativo");
        } catch (AuthenticationException ex) {
            throw new BusinessException("Login ou senha inválidos");
        }

        Servidor servidor = buscarServidorPorLogin(dto.getLogin());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(servidor.getLogin());
        String token = jwtService.generateToken(userDetails);

        auditoriaFacade.loginRealizado(servidor);

        //servidor.setSenhaHash(passwordEncoder.encode(servidor.getSenhaHash()));
        //servidorRepository.save(servidor);

        return new LoginResponseDTO(
                token,
                servidor.getId(),
                servidor.getNome(),
                servidor.getLogin(),
                servidor.getEmail(),
                servidor.getPerfil(),
                servidor.getAtivo(),
                servidor.getServico() != null
                        ? servidor.getServico().getId()
                        : null,

                servidor.getServico() != null
                        ? servidor.getServico().getNome()
                        : null
        );
    }

    private Servidor buscarServidorPorLogin(String login){
        return servidorRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Servidor não encontrado"));
    }
}
