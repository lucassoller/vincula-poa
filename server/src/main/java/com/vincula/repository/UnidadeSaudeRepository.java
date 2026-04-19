package com.vincula.repository;

import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {

    boolean existsByCnes(String cnes);

    boolean existsByCnesAndIdNot(String cnes, Long id);

    Optional<UnidadeSaude> findByCnes(String cnes);

    @Query("""
       SELECT p
       FROM Paciente p
       LEFT JOIN FETCH p.endereco
       WHERE p.unidadeSaude.id = :unidadeSaudeId
       """)
    List<Paciente> findPacientesByUnidadeSaudeId(Long unidadeSaudeId);
}