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
import java.time.LocalDateTime;
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

    double countBy();

    double countByStatus(StatusDemanda status);

    double countByUnidadeResponsavelId(Long unidadeRespondavelId);

    double countByUnidadeSolicitanteId(Long unidadeSolicitanteId);

    double countByStatusAndUnidadeResponsavelId(StatusDemanda status, Long unidadeRespondavelId);

    double countByStatusAndUnidadeSolicitanteId(StatusDemanda status, Long unidadeSolicitanteId);

    double countByDataHoraCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(Long unidadeRespondavelId, LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeSolicitanteIdAndDataHoraCriacaoBetween(Long unidadeSolicitanteId, LocalDateTime inicio, LocalDateTime fim);

    double countByDataHoraFinalizacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeResponsavelIdAndDataHoraFinalizacaoBetween(Long unidadeRespondavelId, LocalDateTime inicio, LocalDateTime fim);

    double countByUnidadeSolicitanteIdAndDataHoraFinalizacaoBetween(Long unidadeSolicitanteId, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndDataHoraCriacaoBetween(StatusDemanda status, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda status, Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim);

    double countByStatusAndUnidadeSolicitanteIdAndDataHoraCriacaoBetween(StatusDemanda status, Long unidadeSolicitanteId, LocalDateTime inicio, LocalDateTime fim);

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
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
        AND d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfechoEUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

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
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
      AND d.desfecho IN ('ENCONTRADO_VINCULADO', 'ENCONTRADO_RECUSOU', 'NAO_LOCALIZADO', 'ENDERECO_INCORRETO', 'MUDOU_TERRITORIO', 'OBITO', 'OUTRO')
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfechoEUnidadeSolicitantePorPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
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
         AND d.unidade_solicitante_id = :unidadeSolicitanteId
       """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

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
        SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
        FROM demanda d
        WHERE d.data_hora_finalizacao IS NOT NULL
          AND d.unidade_solicitante_id = :unidadeSolicitanteId
          AND d.data_hora_criacao BETWEEN :inicio AND :fim
        """, nativeQuery = true)
    Double calcularTempoMedioResolucaoEmSegundosPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                                                   @Param("inicio") LocalDateTime inicio,
                                                                   @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivos();

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);


    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                                @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE 
      d.unidade_responsavel_id = :unidadeResponsavelId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                                        @Param("inicio") LocalDateTime inicio,
                                                                                        @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE 
      d.unidade_solicitante_id = :unidadeSolicitanteId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_busca_ativa
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivosPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                                                                        @Param("inicio") LocalDateTime inicio,
                                                                                        @Param("fim") LocalDateTime fim);

                                                                                        @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementos();

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_responsavel_id = :unidadeResponsavelId
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementosPorUnidade(@Param("unidadeResponsavelId") Long unidadeResponsavelId);

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementosPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);


    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementosPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                                @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE 
      d.unidade_responsavel_id = :unidadeResponsavelId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementosPorUnidadeEPeriodo(@Param("unidadeResponsavelId") Long unidadeResponsavelId,
                                                                                        @Param("inicio") LocalDateTime inicio,
                                                                                        @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE 
      d.unidade_solicitante_id = :unidadeSolicitanteId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
    GROUP BY d.motivo_complemento
    ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementosPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
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
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
        AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatusPorUnidadeSolicitante(Long unidadeSolicitanteId);

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
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE d.unidade_solicitante_id = :unidadeSolicitanteId
      AND d.data_hora_criacao BETWEEN :inicio AND :fim
      AND d.status IN ('FINALIZADA', 'ABERTA', 'EM_ANDAMENTO')
    GROUP BY d.status
""", nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatusPorUnidadeSolicitanteEPeriodo(Long unidadeSolicitanteId,
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
    WHERE unidade_solicitante_id = :unidadeSolicitanteId
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
      )
    """, nativeQuery = true)
    long countDentroPrazoPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
      )
""", nativeQuery = true)
    long countDentroPrazoPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_responsavel_id = :unidadeId
    AND data_hora_criacao BETWEEN :inicio AND :fim
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
      )
""", nativeQuery = true)
    long countDentroPrazoPorUnidadeEPeriodo(@Param("unidadeId") Long unidadeId,
                                            @Param("inicio") LocalDateTime inicio,
                                            @Param("fim")LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_solicitante_id = :unidadeSolicitanteId
    AND data_hora_criacao BETWEEN :inicio AND :fim
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() <= data_hora_limite)
      )
""", nativeQuery = true)
    long countDentroPrazoPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                            @Param("inicio") LocalDateTime inicio,
                                            @Param("fim")LocalDateTime fim);

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
    WHERE unidade_solicitante_id = :unidadeSolicitanteId
      AND (
        (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
      )
    """, nativeQuery = true)
    long countAtrasadasPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
        AND (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
    """, nativeQuery = true)
    long countDemandasAtrasadasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                           @Param("fim")LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
          AND unidade_responsavel_id = :unidadeId
        AND (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
    """, nativeQuery = true)
    long countDemandasAtrasadasPorUnidadeEPeriodo(@Param("unidadeId") Long unidadeId,
                                                  @Param("inicio") LocalDateTime inicio,
                                                  @Param("fim")LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
          AND unidade_solicitante_id = :unidadeSolicitanteId
        AND (status IN ('ABERTA', 'EM_ANDAMENTO') AND NOW() > data_hora_limite)
    """, nativeQuery = true)
    long countDemandasAtrasadasPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                                  @Param("inicio") LocalDateTime inicio,
                                                  @Param("fim")LocalDateTime fim);

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
    SELECT COUNT(*)
    FROM demanda
    WHERE unidade_solicitante_id = :unidadeSolicitanteId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countFinalizadasAtrasadasPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countFinalizadasAtrasadasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                             @Param("fim")LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
          AND unidade_responsavel_id = :unidadeId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countFinalizadasAtrasadasPorUnidadeEPeriodo(@Param("unidadeId") Long unidadeId,
                                                     @Param("inicio") LocalDateTime inicio,
                                                     @Param("fim")LocalDateTime fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
          AND unidade_solicitante_id = :unidadeSolicitanteId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    long countFinalizadasAtrasadasPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                                     @Param("inicio") LocalDateTime inicio,
                                                     @Param("fim")LocalDateTime fim);

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

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE unidade_solicitante_id = :unidadeSolicitanteId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
    """, nativeQuery = true)
    Double tempoMedioAtrasoPorUnidadeSolicitante(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
""", nativeQuery = true)
    Double tempoMedioAtrasoEmSegundosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
      AND unidade_responsavel_id = :unidadeId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
""", nativeQuery = true)
    Double tempoMedioAtrasoEmSegundosPorUnidadeEPeriodo(@Param("unidadeId") Long unidadeId,
                                                        @Param("inicio") LocalDateTime inicio,
                                                        @Param("fim") LocalDateTime fim);

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (data_hora_finalizacao - data_hora_limite)))
    FROM demanda
    WHERE data_hora_criacao BETWEEN :inicio AND :fim
      AND unidade_solicitante_id = :unidadeSolicitanteId
      AND status = 'FINALIZADA'
      AND data_hora_finalizacao > data_hora_limite
""", nativeQuery = true)
    Double tempoMedioAtrasoEmSegundosPorUnidadeSolicitanteEPeriodo(@Param("unidadeSolicitanteId") Long unidadeSolicitanteId,
                                                        @Param("inicio") LocalDateTime inicio,
                                                        @Param("fim") LocalDateTime fim);
}