package com.vincula.repository;

import com.vincula.dto.projection.MotivoQuantidadeProjection;
import com.vincula.entity.Demanda;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DemandaRepository extends JpaRepository<Demanda, Long> {

    List<Demanda> findByPacienteId(Long pacienteId);

    List<Demanda> findByUnidadeSaudeId(Long unidadeSaudeId);

    List<Demanda> findByUsuarioCriadorId(Long usuarioCriadorId);

    List<Demanda> findByPacienteIdAndStatus(Long pacienteId, StatusDemanda status);

    List<Demanda> findByUnidadeSaudeIdAndStatus(Long unidadeSaudeId, StatusDemanda status);

    List<Demanda> findByUsuarioCriadorIdAndStatus(Long usuarioCriadorId, StatusDemanda status);

    double countBy();

    double countByStatus(StatusDemanda status);

    double countByUnidadeSaudeId(Long unidadeSaudeId);

    double countByDesfecho(DesfechoDemanda desfecho);

    double countByStatusAndUnidadeSaudeId(StatusDemanda status, Long unidadeSaudeId);

    double countByDesfechoAndUnidadeSaudeId(DesfechoDemanda desfecho, Long unidadeSaudeId);

    double countByDataHoraCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeSaudeIdAndDataHoraCriacaoBetween(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim);

    double countByDataHoraFinalizacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeSaudeIdAndDataHoraFinalizacaoBetween(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndDataHoraCriacaoBetween(StatusDemanda status, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndUnidadeSaudeIdAndDataHoraCriacaoBetween(StatusDemanda status, Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim);

    double countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda desfecho, LocalDateTime inicio, LocalDateTime fim);

    double countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda desfecho, Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim);

    @Query(value = """
       SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
       FROM demanda d
       WHERE d.data_hora_finalizacao IS NOT NULL
       """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundos();

    @Query(value = """
       SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
       FROM demanda d
       WHERE d.data_hora_finalizacao IS NOT NULL
         AND d.unidade_saude_id = :unidadeSaudeId
       """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query(value = """
       SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
       FROM demanda d
       WHERE d.data_hora_finalizacao IS NOT NULL
         AND d.data_hora_criacao BETWEEN :inicio AND :fim
       """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
        FROM demanda d
        WHERE d.data_hora_finalizacao IS NOT NULL
          AND d.unidade_saude_id = :unidadeSaudeId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidadeEPeriodo(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                                                   @Param("inicio") LocalDateTime inicio,
                                                                   @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_ENCONTRADO', 'FORA_DO_TERRITORIO')
    GROUP BY d.motivo
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucesso();

    @Query(value = """
    SELECT d.motivo AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_ENCONTRADO', 'FORA_DO_TERRITORIO')
      AND d.unidade_saude_id = :unidadeSaudeId
    GROUP BY d.motivo
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query(value = """
        SELECT d.motivo AS motivo, COUNT(*) AS quantidade
        FROM demanda d
        WHERE d.desfecho IN ('NAO_ENCONTRADO', 'FORA_DO_TERRITORIO')
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY d.motivo
        ORDER BY quantidade DESC
        """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                                @Param("fim") LocalDateTime fim);

    @Query(value = """
        SELECT d.motivo AS motivo, COUNT(*) AS quantidade
        FROM demanda d
        WHERE d.desfecho IN ('NAO_ENCONTRADO', 'FORA_DO_TERRITORIO')
          AND d.unidade_saude_id = :unidadeSaudeId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY d.motivo
        ORDER BY quantidade DESC
        """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                                                                        @Param("inicio") LocalDateTime inicio,
                                                                                        @Param("fim") LocalDateTime fim);

}