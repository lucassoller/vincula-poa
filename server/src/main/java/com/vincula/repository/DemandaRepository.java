package com.vincula.repository;

import com.vincula.dto.projection.*;
import com.vincula.entity.Demanda;
import com.vincula.enums.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DemandaRepository extends JpaRepository<Demanda, Long> {

    List<Demanda> findByPacienteId(Long pacienteId);

    List<Demanda> findByUnidadeResponsavelId(Long unidadeRespondavelId);

    List<Demanda> findByUsuarioCriadorId(Long usuarioCriadorId);

    List<Demanda> findByPacienteIdAndStatus(Long pacienteId, StatusDemanda status);

    List<Demanda> findByUnidadeResponsavelIdAndStatus(Long unidadeRespondavelId, StatusDemanda status);

    List<Demanda> findByUsuarioCriadorIdAndStatus(Long usuarioCriadorId, StatusDemanda status);

    double countBy();

    double countByStatus(StatusDemanda status);

    double countByUnidadeResponsavelId(Long unidadeRespondavelId);

    double countByStatusAndUnidadeResponsavelId(StatusDemanda status, Long unidadeRespondavelId);

    double countByDataHoraCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(Long unidadeRespondavelId, LocalDateTime inicio, LocalDateTime fim);

    double countByDataHoraFinalizacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeResponsavelIdAndDataHoraFinalizacaoBetween(Long unidadeRespondavelId, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndDataHoraCriacaoBetween(StatusDemanda status, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda status, Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim);

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfecho();

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
        AND d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfechoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                    @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
        AND d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfechoEUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
      AND d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfechoEUnidadePorPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                            @Param("inicio") LocalDateTime inicio,
                                                                            @Param("fim") LocalDateTime fim);
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
         AND d.unidade_responsavel_id = :unidadeResponsavelId
       """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

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
          AND d.unidade_responsavel_id = :unidadeResponsavelId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                   @Param("inicio") LocalDateTime inicio,
                                                                   @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OUTRO')
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucesso();

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OUTRO')
      AND d.unidade_responsavel_id = :unidadeResponsavelId
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OUTRO')
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                                @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.desfecho IN ('NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OUTRO')
      AND d.unidade_responsavel_id = :unidadeResponsavelId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                                        @Param("inicio") LocalDateTime inicio,
                                                                                        @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatus();

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
        AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatusPorUnidade(Long unidadeResponsavelId);

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
        AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatusPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
      AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatusPorUnidadeEPeriodo(Long unidadeResponsavelId,
                                                                        LocalDateTime inicio,
                                                                        LocalDateTime fim);

    @Query(value = """
    SELECT 
        u.id AS unidadeSaudeId,
        u.nome AS unidadeSaudeNome,
        COUNT(d.id) AS valor
    FROM unidade_saude u
    LEFT JOIN demanda d ON d.unidade_responsavel_id = u.id
    GROUP BY u.id, u.nome
    ORDER BY valor DESC, u.nome ASC
    """, nativeQuery = true)
    List<RankingQuantidadeProjection> rankingUnidadesPorTotalDemandas();

    @Query(value = """
    SELECT
        u.id AS unidadeSaudeId,
        u.nome AS unidadeSaudeNome,
        CASE 
            WHEN COUNT(d.id) = 0 THEN 0
            ELSE COUNT(d.id) FILTER (WHERE d.status = 'FINALIZADA') * 100.0 / COUNT(d.id)
        END AS valor
    FROM unidade_saude u
    LEFT JOIN demanda d ON d.unidade_responsavel_id = u.id
    GROUP BY u.id, u.nome
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

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
    """, nativeQuery = true)
    long countDemandasDentroDoPrazo();

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_responsavel_id = :unidadeId
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
      )
    """, nativeQuery = true)
    long countDentroPrazoPorUnidade(@Param("unidadeId") Long unidadeId);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
    """, nativeQuery = true)
    long countDemandasAtrasadas();

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_responsavel_id = :unidadeId
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
      )
    """, nativeQuery = true)
    long countAtrasadasPorUnidade(@Param("unidadeId") Long unidadeId);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countDemandasFinalizadasComAtraso();

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_responsavel_id = :unidadeId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countFinalizadasAtrasadasPorUnidade(@Param("unidadeId") Long unidadeId);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    Double tempoMedioAtrasoEmSegundos();

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE unidade_responsavel_id = :unidadeId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    Double tempoMedioAtrasoPorUnidade(@Param("unidadeId") Long unidadeId);
}