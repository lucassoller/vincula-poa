package com.vincula.security;

import com.vincula.entity.Servidor;
import com.vincula.repository.ServidorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ServidorRepository servidorRepository;

    public CustomUserDetailsService(ServidorRepository servidorRepository) {
        this.servidorRepository = servidorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Servidor servidor = servidorRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Servidor não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(servidor.getLogin())
                .password(servidor.getSenhaHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + servidor.getPerfil().name())))
                .disabled(!Boolean.TRUE.equals(servidor.getAtivo()))
                .build();
    }
}