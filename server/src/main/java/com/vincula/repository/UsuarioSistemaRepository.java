package com.vincula.repository;

import com.vincula.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<UsuarioSistema> findByEmail(String email);

    Optional<UsuarioSistema> findByLogin(String login);
}