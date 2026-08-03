package com.vincula.repository;

import com.vincula.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario>{

    Page<Usuario> findAllByOrderByNomeCompletoAsc(Pageable pageable);

    List<Usuario> findAllByOrderByNomeCompletoAsc();

    @Query("""
    SELECT u
    FROM Usuario u
    WHERE
        LOWER(u.nomeCompleto) LIKE LOWER(:filtro)
        OR u.documento LIKE :filtro
""")
    List<Usuario> buscarPorNomeOuDocumento(String filtro);

    List<Usuario> findTop10ByNomeCompletoContainingIgnoreCaseOrderByNomeCompleto(String nomeCompleto);

    @Query("""
    SELECT p
    FROM Usuario p
    WHERE (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(p.documento) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(p.telefone) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(p.unidadeSaude.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Usuario> findFiltrados(String filtro, Pageable pageable);

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
    Page<Usuario> findFiltradosByUnidade(
            Long unidadeSaudeId,
            String filtro,
            Pageable pageable
    );

    Page<Usuario> findByUnidadeSolicitanteIdOrderByNomeCompletoAsc(Long unidadeSolicitanteId, Pageable pageable);

    @Query("""
    SELECT p
    FROM Usuario p
    WHERE p.unidadeSolicitante.id = :unidadeSolicitanteId
    AND (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR p.documento LIKE CONCAT('%', :filtro, '%')
        OR p.telefone LIKE CONCAT('%', :filtro, '%')
        OR LOWER(p.unidadeSolicitante.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Usuario> findFiltradosByUnidadeSolicitante(
            Long unidadeSolicitanteId,
            String filtro,
            Pageable pageable
    );

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Usuario> findByDocumento(String documento);

}