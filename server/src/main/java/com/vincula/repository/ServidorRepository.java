package com.vincula.repository;

import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServidorRepository extends JpaRepository<Servidor, Long> {

    Page<Servidor> findAllByOrderByNomeAsc(Pageable pageable);

    @Query("""
    SELECT p
    FROM Servidor p
    LEFT JOIN p.unidadeSaude us
    WHERE (
        LOWER(p.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(p.email) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(p.perfil) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(COALESCE(us.nome, '')) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nome ASC
""")
    Page<Servidor> findFiltrados(String filtro, Pageable pageable);

    List<Servidor> findAllByOrderByNomeAsc();

    Page<Servidor> findByPerfilOrderByNomeAsc(PerfilServidor perfil, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<Servidor> findByEmail(String email);

    Optional<Servidor> findByLogin(String login);
}