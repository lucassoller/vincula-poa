package com.vincula.repository;

import com.vincula.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findAllByUnidadeSaudeIdOrderByNomeCompletoAsc(Long id);

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    Optional<Paciente> findByDocumento(String documento);
}