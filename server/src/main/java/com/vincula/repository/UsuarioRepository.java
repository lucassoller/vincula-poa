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

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Usuario> findByDocumento(String documento);

}