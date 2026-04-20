package com.vincula.repository;

import com.vincula.entity.Demanda;
import com.vincula.enums.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandaRepository extends JpaRepository<Demanda, Long> {

    List<Demanda> findByPacienteId(Long pacienteId);

    List<Demanda> findByUnidadeSaudeId(Long unidadeSaudeId);

    List<Demanda> findByUsuarioCriadorId(Long usuarioCriadorId);

    List<Demanda> findByPacienteIdAndStatus(Long pacienteId, StatusDemanda status);

    List<Demanda> findByUnidadeSaudeIdAndStatus(Long unidadeSaudeId, StatusDemanda status);

    List<Demanda> findByUsuarioCriadorIdAndStatus(Long usuarioCriadorId, StatusDemanda status);
}