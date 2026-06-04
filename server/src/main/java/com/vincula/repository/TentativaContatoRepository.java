package com.vincula.repository;

import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.entity.TentativaContato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TentativaContatoRepository extends JpaRepository<TentativaContato, Long> {

    List<TentativaContato> findByDemandaId(Long demandaId);
    List<TentativaContato> findByServidorId(Long servidorId);
    Page<TentativaContato> findAllByOrderByDemandaIdAsc(Pageable pageable);
    boolean existsByDemandaId(Long demandaId);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
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
    SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
    FROM (
        SELECT d.data_hora_criacao,
               MIN(t.data_hora) AS primeira_tentativa
        FROM demanda d
        JOIN tentativa_contato t ON t.demanda_id = d.id
        WHERE d.unidade_responsavel_id = :unidadeResponsavelId
        GROUP BY d.id, d.data_hora_criacao
    ) sub
    """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
    FROM (
        SELECT d.data_hora_criacao,
               MIN(t.data_hora) AS primeira_tentativa
        FROM demanda d
        JOIN tentativa_contato t ON t.demanda_id = d.id
        WHERE d.servidor_criador_id = :servidorCriadorId
        GROUP BY d.id, d.data_hora_criacao
    ) sub
    """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorServidor(@Param("servidorCriadorId") Long servidorCriadorId);

    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
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
        SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
        FROM (
            SELECT d.data_hora_criacao,
                   MIN(t.data_hora) AS primeira_tentativa
            FROM demanda d
            JOIN tentativa_contato t ON t.demanda_id = d.id
            WHERE d.unidade_responsavel_id = :unidadeResponsavelId
              AND d.data_hora_criacao BETWEEN :inicio AND :fim
            GROUP BY d.id, d.data_hora_criacao
        ) sub
        """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                           @Param("inicio") LocalDateTime inicio,
                                                                           @Param("fim") LocalDateTime fim);
    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (primeira_tentativa - data_hora_criacao)))
        FROM (
            SELECT d.data_hora_criacao,
                   MIN(t.data_hora) AS primeira_tentativa
            FROM demanda d
            JOIN tentativa_contato t ON t.demanda_id = d.id
            WHERE d.servidor_criador_id = :servidorCriadorId
              AND d.data_hora_criacao BETWEEN :inicio AND :fim
            GROUP BY d.id, d.data_hora_criacao
        ) sub
        """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativaEmHorasPorServidorEPeriodo(@Param("servidorCriadorId") Long servidorCriadorId,
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
        WHERE d.unidade_responsavel_id = :unidadeResponsavelId
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.servidor_criador_id = :servidorCriadorId
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorServidor(@Param("servidorCriadorId") Long servidorCriadorId);

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
        WHERE d.unidade_responsavel_id = :unidadeResponsavelId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.servidor_criador_id = :servidorCriadorId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.demanda_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorDemandaPorServidorEPeriodo(@Param("servidorCriadorId") Long servidorCriadorId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT COUNT(*) AS qtd
        FROM tentativa_contato
        GROUP BY servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidor();

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT t.servidor_id, COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.unidade_responsavel_id = :unidadeResponsavelId
        GROUP BY t.servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidorPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT t.servidor_id, COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.servidor_criador_id = :servidorCriadorId
        GROUP BY t.servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidorPorCriador(@Param("servidorCriadorId") Long servidorCriadorId);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT t.servidor_id, COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidorPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                       @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT t.servidor_id, COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.unidade_responsavel_id = :unidadeResponsavelId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidorPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(qtd)
    FROM (
        SELECT t.servidor_id, COUNT(*) AS qtd
        FROM tentativa_contato t
        JOIN demanda d ON d.id = t.demanda_id
        WHERE d.servidor_criador_id = :servidorCriadorId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        GROUP BY t.servidor_id
    ) sub
    """, nativeQuery = true)
    Double calcularMediaTentativasPorServidorPorServidorEPeriodo(@Param("servidorCriadorId") Long servidorCriadorId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fim") LocalDateTime fim);


    @Query(value = """
    SELECT
        sub.unidade_responsavel_id AS unidadeSaudeId,
        sub.unidade_saude_nome AS unidadeSaudeNome,
        AVG(EXTRACT(EPOCH FROM (sub.primeira_tentativa - sub.data_hora_criacao))) AS valor
    FROM (
        SELECT
            d.id,
            d.unidade_responsavel_id,
            u.nome AS unidade_saude_nome,
            d.data_hora_criacao,
            MIN(t.data_hora) AS primeira_tentativa
        FROM demanda d
        JOIN unidade_saude u ON u.id = d.unidade_responsavel_id
        JOIN tentativa_contato t ON t.demanda_id = d.id
        GROUP BY d.id, d.unidade_responsavel_id, u.nome, d.data_hora_criacao
    ) sub
    GROUP BY sub.unidade_responsavel_id, sub.unidade_saude_nome
    ORDER BY valor ASC, sub.unidade_saude_nome ASC
    """, nativeQuery = true)
    List<RankingValorProjection> rankingUnidadesPorTempoAtePrimeiraTentativa();
}