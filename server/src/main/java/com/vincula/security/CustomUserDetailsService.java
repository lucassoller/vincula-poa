package com.vincula.security;

import com.vincula.entity.Usuario;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())))
                .disabled(!Boolean.TRUE.equals(usuario.getAtivo()))
                .build();
    }
}