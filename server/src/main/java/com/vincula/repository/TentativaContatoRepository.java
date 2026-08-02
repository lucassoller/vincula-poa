package com.vincula.repository;

import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.entity.TentativaContato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
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
        WHERE (:unidadeResponsavelId IS NULL OR d.unidade_responsavel_id = :unidadeResponsavelId)
          AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
          AND (
                :dataInicial IS NULL
                OR d.data_hora_criacao BETWEEN :dataInicial AND :dataFinal
              )
        GROUP BY d.id, d.data_hora_criacao
    ) sub
    """, nativeQuery = true)
    Double calcularTempoMedioAtePrimeiraTentativa(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("dataInicial") LocalDate dataInicial,
            @Param("dataFinal") LocalDate dataFinal
    );

    @Query(value = """
SELECT AVG(qtd)
FROM (
    SELECT COUNT(*) AS qtd
    FROM tentativa_contato t
    JOIN demanda d ON d.id = t.demanda_id
    WHERE (:unidadeResponsavelId IS NULL OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
      )
    GROUP BY t.demanda_id
) sub
""", nativeQuery = true)
    Double calcularMediaTentativasPorDemanda(
            Long unidadeResponsavelId,
            Long unidadeSolicitanteId,
            LocalDate inicio,
            LocalDate fim);

    @Query(value = """
SELECT AVG(qtd)
FROM (
    SELECT t.servidor_id, COUNT(*) AS qtd
    FROM tentativa_contato t
    JOIN demanda d ON d.id = t.demanda_id
    WHERE (:unidadeResponsavelId IS NULL OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
      )
    GROUP BY t.servidor_id
) sub
""", nativeQuery = true)
    Double calcularMediaTentativasPorServidor(
            Long unidadeResponsavelId,
            Long unidadeSolicitanteId,
            LocalDate inicio,
            LocalDate fim);


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