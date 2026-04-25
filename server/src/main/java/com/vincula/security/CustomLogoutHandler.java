package com.vincula.security;

import com.vincula.repository.UsuarioRepository;
import com.vincula.util.AuditoriaFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AuditoriaFacade auditoriaFacade;
    private final UsuarioRepository usuarioRepository;

    public CustomLogoutHandler(AuditoriaFacade auditoriaFacade,
                               UsuarioRepository usuarioRepository) {
        this.auditoriaFacade = auditoriaFacade;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        try {
            if (authentication == null || authentication.getName() == null) {
                return;
            }

            usuarioRepository.findByLogin(authentication.getName()).ifPresent(auditoriaFacade::logoutRealizado);

        } catch (Exception ignored) {
        }
    }
}