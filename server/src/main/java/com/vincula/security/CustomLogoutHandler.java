package com.vincula.security;

import com.vincula.repository.ServidorRepository;
import com.vincula.util.AuditoriaFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AuditoriaFacade auditoriaFacade;
    private final ServidorRepository servidorRepository;

    public CustomLogoutHandler(AuditoriaFacade auditoriaFacade,
                               ServidorRepository servidorRepository) {
        this.auditoriaFacade = auditoriaFacade;
        this.servidorRepository = servidorRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        try {
            if (authentication == null || authentication.getName() == null) {
                return;
            }

            servidorRepository.findByLogin(authentication.getName()).ifPresent(auditoriaFacade::logoutRealizado);

        } catch (Exception ignored) {
        }
    }
}