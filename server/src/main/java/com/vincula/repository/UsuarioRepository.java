package com.vincula.repository;

import com.vincula.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByLogin(String login);
}