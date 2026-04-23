package com.vincula.repository;

import com.vincula.dto.projection.DesfechoQuantidadeProjection;
import com.vincula.dto.projection.MotivoQuantidadeProjection;
import com.vincula.dto.projection.StatusQuantidadeProjection;
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
}