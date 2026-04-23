package com.vincula.repository;

import com.vincula.entity.TentativaContato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TentativaContatoRepository extends JpaRepository<TentativaContato, Long> {

    List<TentativaContato> findByDemandaId(Long demandaId);
    List<TentativaContato> findByUsuario(Long usuarioId);
    boolean existsByDemandaId(Long demandaId);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)) / 3600.0)
    FROM (
        SELECT d.data_hora_criacao,
               MIN(t.data_hora) AS primeira_tentativa
        FROM demanda d
        JOIN tentativa_contato t ON t.demanda_id = d.id
        GROUP BY d.id, d.data_hora_criacao
    ) sub
    """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHoras();

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)) / 3600.0)
    FROM (
        SELECT d.data_hora_criacao,
               MIN(t.data_hora) AS primeira_tentativa
        FROM demanda d
        JOIN tentativa_contato t ON t.demanda_id = d.id
        WHERE d.unidade_saude_id = :unidadeSaudeId
        GROUP BY d.id, d.data_hora_criacao
    ) sub
    """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)) / 3600.0)
        FROM (
            SELECT d.data_hora_criacao,
                   MIN(t.data_hora) AS primeira_tentativa
            FROM demanda d
            JOIN tentativa_contato t ON t.demanda_id = d.id
            WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
            GROUP BY d.id, d.data_hora_criacao
        ) sub
        """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                   @Param("fim") LocalDateTime fim);

    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)) / 3600.0)
        FROM (
            SELECT d.data_hora_criacao,
                   MIN(t.data_hora) AS primeira_tentativa
            FROM demanda d
            JOIN tentativa_contato t ON t.demanda_id = d.id
            WHERE d.unidade_saude_id = :unidadeSaudeId
              AND d.data_hora_criacao BETWEEN :inicio AND :fim
            GROUP BY d.id, d.data_hora_criacao
        ) sub
        """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidadeEPeriodo(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                                                           @Param("inicio") LocalDateTime inicio,
                                                                           @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato
        GROUP BY demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemanda();

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.unidade_saude_id = :unidadeSaudeId
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                       @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.unidade_saude_id = :unidadeSaudeId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorUnidadeEPeriodo(@Param("unidadeSaudeId") Long unidadeSaudeId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fim") LocalDateTime fim);

}