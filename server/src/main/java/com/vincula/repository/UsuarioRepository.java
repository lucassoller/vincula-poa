package com.vincula.repository;

import com.vincula.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findAllByOrderByNomeCompletoAsc(Pageable pageable);

    List<Usuario> findAllByOrderByNomeCompletoAsc();

    @Query("""
    SELECT p
    FROM Usuario p
    WHERE (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR p.documento LIKE CONCAT('%', :filtro, '%')
        OR p.telefone LIKE CONCAT('%', :filtro, '%')
        OR LOWER(p.unidadeSaude.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Usuario> findFiltrados(@Param("filtro") String filtro, Pageable pageable);

    Page<Usuario> findByUnidadeSaudeIdOrderByNomeCompletoAsc(Long unidadeSaudeId, Pageable pageable);

    @Query("""
    SELECT p
    FROM Usuario p
    WHERE p.unidadeSaude.id = :unidadeSaudeId
    AND (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR p.documento LIKE CONCAT('%', :filtro, '%')
        OR p.telefone LIKE CONCAT('%', :filtro, '%')
        OR LOWER(p.unidadeSaude.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Usuario> findFiltradosByUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                         @Param("filtro") String filtro,
                                         Pageable pageable
    );

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Usuario> findByDocumento(String documento);
}