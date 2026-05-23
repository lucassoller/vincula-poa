package com.vincula.repository;

import com.vincula.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAllByOrderByNomeCompletoAsc(Pageable pageable);

    List<Paciente> findAllByOrderByNomeCompletoAsc();

    @Query("""
    SELECT p
    FROM Paciente p
    WHERE (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR p.documento LIKE CONCAT('%', :filtro, '%')
        OR p.telefone LIKE CONCAT('%', :filtro, '%')
        OR LOWER(p.unidadeSaude.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Paciente> findFiltrados(@Param("filtro") String filtro, Pageable pageable);

    Page<Paciente> findByUnidadeSaudeIdOrderByNomeCompletoAsc(Long unidadeSaudeId, Pageable pageable);

    @Query("""
    SELECT p
    FROM Paciente p
    WHERE p.unidadeSaude.id = :unidadeSaudeId
    AND (
        LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR p.documento LIKE CONCAT('%', :filtro, '%')
        OR p.telefone LIKE CONCAT('%', :filtro, '%')
        OR LOWER(p.unidadeSaude.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    ORDER BY p.nomeCompleto ASC
""")
    Page<Paciente> findFiltradosByUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                          @Param("filtro") String filtro,
                                          Pageable pageable
    );

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Paciente> findByDocumento(String documento);
}