package com.vincula.repository;

import com.vincula.entity.Usuario;
import com.vincula.enums.PerfilUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findAllByOrderByNomeAsc(Pageable pageable);

    List<Usuario> findAllByOrderByNomeAsc();

    Page<Usuario> findByPerfilOrderByNomeAsc(PerfilUsuario perfil, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByLogin(String login);
}