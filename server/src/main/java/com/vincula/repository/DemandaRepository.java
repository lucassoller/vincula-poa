package com.vincula.repository;

import com.vincula.dto.projection.*;
import com.vincula.entity.Demanda;
import com.vincula.entity.Usuario;
import com.vincula.enums.StatusDemanda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface DemandaRepository extends JpaRepository<Demanda, Long>, JpaSpecificationExecutor<Demanda> {

    @Query("""
    SELECT DISTINCT u
    FROM Demanda d
    JOIN d.usuario u
    ORDER BY u.nomeCompleto
    """)
    List<Usuario> findUsuariosComDemanda();

    @Query("""
    SELECT DISTINCT u
    FROM Demanda d
    JOIN d.usuario u
    WHERE d.unidadeResponsavel.id = :unidadeSaudeId
    ORDER BY u.nomeCompleto
    """)
    List<Usuario> findUsuariosComDemandaPorUnidade(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query("""
    SELECT DISTINCT u
    FROM Demanda d
    JOIN d.usuario u
    WHERE d.unidadeSolicitante.id = :unidadeSaudeId
    ORDER BY u.nomeCompleto
    """)
    List<Usuario> findUsuariosComDemandaPorUnidadeSolicitante(@Param("unidadeSaudeId") Long unidadeSaudeId);

    @Query("""
    SELECT d
    FROM Demanda d
    JOIN d.usuario p
    WHERE d.usuario.id = :usuarioId
    ORDER BY p.nomeCompleto ASC
    """)
    Page<Demanda> findByUsuarioOrderByUsuarioNome(@Param("usuarioId") Long usuarioId, Pageable pageable);

    List<Demanda> findByUsuarioIdAndStatusIn(Long usuarioId, List<StatusDemanda> status);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (:unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId)
    AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
    AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
    )
    """, nativeQuery = true)
    int countDemandas(Long unidadeResponsavelId,
                       Long unidadeSolicitanteId,
                       LocalDate inicio,
                       LocalDate fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE d.status = 'FINALIZADA'
      AND (
            :unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId
      )
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_finalizacao BETWEEN :inicio AND :fim
      )
    """, nativeQuery = true)
    int countDemandasFinalizadas(Long unidadeResponsavelId,
                                  Long unidadeSolicitanteId,
                                  LocalDate inicio,
                                  LocalDate fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (
            (d.status IN ('ABERTA', 'EM_ANDAMENTO')
                AND NOW() <= d.data_hora_limite)
            OR
            (d.status = 'FINALIZADA'
                AND d.data_hora_finalizacao <= d.data_hora_limite)
        )
        AND (
            :unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId
        )
        AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
        AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
        )
    """, nativeQuery = true)
    int countDemandasDentroDoPrazo(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (
            (d.status IN ('ABERTA', 'EM_ANDAMENTO')
                AND NOW() > d.data_hora_limite)
            OR
            (d.status = 'FINALIZADA'
                AND d.data_hora_finalizacao > d.data_hora_limite)
        )
        AND (
            :unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId
        )
        AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
        AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
        )
    """, nativeQuery = true)
    int countDemandasAtrasadas(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE d.status = 'FINALIZADA'
      AND d.data_hora_finalizacao > d.data_hora_limite
      AND (
            :unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId
      )
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
      )
    """, nativeQuery = true)
    int countDemandasFinalizadasComAtraso(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:unidadeResponsavelId IS NULL OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
      )
      AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
    """, nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatus(
            Long unidadeResponsavelId,
            Long unidadeSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:unidadeResponsavelId IS NULL 
           OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (:inicio IS NULL 
           OR d.data_hora_criacao BETWEEN :inicio AND :fim)
      AND d.desfecho IN (
          'ENCONTRADO_VINCULADO',
          'ENCONTRADO_RECUSOU',
          'NAO_LOCALIZADO',
          'ENDERECO_INCORRETO',
          'MUDOU_TERRITORIO',
          'OBITO',
          'OUTRO'
      )
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfecho(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:unidadeResponsavelId IS NULL 
            OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (:inicio IS NULL 
            OR d.data_hora_criacao BETWEEN :inicio AND :fim)
      AND d.motivo_busca_ativa IS NOT NULL
    GROUP BY d.motivo_busca_ativa
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivos(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:unidadeResponsavelId IS NULL 
            OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (:inicio IS NULL 
            OR d.data_hora_criacao BETWEEN :inicio AND :fim)
      AND d.motivo_complemento IS NOT NULL
    GROUP BY d.motivo_complemento
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementos(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT AVG(
        EXTRACT(EPOCH FROM
            (
                CASE
                    WHEN d.status = 'FINALIZADA'
                        THEN d.data_hora_finalizacao - d.data_hora_limite
                END
            )
        )
    )
    FROM demanda d
    WHERE
        (
            (d.status = 'FINALIZADA'
                AND d.data_hora_finalizacao > d.data_hora_limite)
        )
        AND (
            :unidadeResponsavelId IS NULL
            OR d.unidade_responsavel_id = :unidadeResponsavelId
        )
        AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
        AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
        )
    """, nativeQuery = true)
    Double calcularTempoMedioAtraso(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
    FROM demanda d
    WHERE d.data_hora_finalizacao IS NOT NULL
      AND (:unidadeResponsavelId IS NULL OR d.unidade_responsavel_id = :unidadeResponsavelId)
      AND (:unidadeSolicitanteId IS NULL
            OR (:unidadeSolicitanteId = -1 AND d.unidade_solicitante_id IS NULL)
            OR d.unidade_solicitante_id = :unidadeSolicitanteId)
      AND (
            :inicio IS NULL
            OR d.data_hora_criacao BETWEEN :inicio AND :fim
          )
    """, nativeQuery = true)
    Double calcularTempoMedioResolucao(
            @Param("unidadeResponsavelId") Long unidadeResponsavelId,
            @Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(value = """
    SELECT 
        u.id AS unidadeSaudeId,
        u.nome AS unidadeSaudeNome,
        COUNT(d.id) AS valor
    FROM unidade_saude u
    LEFT JOIN demanda d ON d.unidade_responsavel_id = u.id
    GROUP BY u.id, u.nome
    HAVING COUNT(d.id) > 0
    ORDER BY valor DESC, u.nome ASC
    """, nativeQuery = true)
    List<RankingQuantidadeProjection> rankingUnidadesPorTotalDemandas();

    @Query(value = """
    SELECT
        u.id AS unidadeSaudeId,
        u.nome AS unidadeSaudeNome,
        COUNT(d.id) FILTER (WHERE d.status = 'FINALIZADA') * 100.0 / COUNT(d.id) AS valor
    FROM unidade_saude u
    LEFT JOIN demanda d ON d.unidade_responsavel_id = u.id
    GROUP BY u.id, u.nome
    HAVING COUNT(d.id) > 0
    ORDER BY valor DESC, u.nome ASC
    """, nativeQuery = true)
    List<RankingValorProjection> rankingUnidadesPorPercentualResolucao();

    @Query(value = """
    SELECT
        u.id AS unidadeSaudeId,
        u.nome AS unidadeSaudeNome,
        AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao))) AS valor
    FROM unidade_saude u
    JOIN demanda d ON d.unidade_responsavel_id = u.id
    WHERE d.data_hora_finalizacao IS NOT NULL
    GROUP BY u.id, u.nome
    ORDER BY valor ASC, u.nome ASC
    """, nativeQuery = true)
    List<RankingValorProjection> rankingUnidadesPorTempoMedioResolucao();
}