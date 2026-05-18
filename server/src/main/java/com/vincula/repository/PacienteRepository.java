package com.vincula.repository;

import com.vincula.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAllByOrderByNomeCompletoAsc(Pageable pageable);

    Page<Paciente> findByUnidadeSaudeIdOrderByNomeCompletoAsc(Long unidadeSaudeId, Pageable pageable);

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Paciente> findByDocumento(String documento);
}