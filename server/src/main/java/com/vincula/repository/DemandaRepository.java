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
    WHERE d.servicoResponsavel.id = :servicoId
    ORDER BY u.nomeCompleto
    """)
    List<Usuario> findUsuariosComDemandaPorServico(Long servicoId);

    @Query("""
    SELECT DISTINCT u
    FROM Demanda d
    JOIN d.usuario u
    WHERE d.servicoSolicitante.id = :servicoId
    ORDER BY u.nomeCompleto
    """)
    List<Usuario> findUsuariosComDemandaPorServicoSolicitante(Long servicoId);

    @Query("""
    SELECT d
    FROM Demanda d
    JOIN d.usuario p
    WHERE d.usuario.id = :usuarioId
    ORDER BY p.nomeCompleto ASC
    """)
    Page<Demanda> findByUsuarioOrderByUsuarioNome(Long usuarioId, Pageable pageable);

    List<Demanda> findByUsuarioIdAndStatusIn(Long usuarioId, List<StatusDemanda> status);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (:servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId)
    AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
    AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Long countDemandas(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE d.status = 'FINALIZADA'
      AND (
            :servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId
      )
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Long countDemandasFinalizadas(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim);

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (d.status IN ('ABERTA', 'EM_ANDAMENTO')
                 AND NOW() <= d.data_hora_limite)
        AND (
            :servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId
        )
        AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
        AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Long countDemandasDentroDoPrazo(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE
        (d.status IN ('ABERTA', 'EM_ANDAMENTO')
            AND NOW() > d.data_hora_limite)
        AND (
            :servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId
        )
        AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
        AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Long countDemandasAtrasadas(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM demanda d
    WHERE d.status = 'FINALIZADA'
      AND d.data_hora_finalizacao > d.data_hora_limite
      AND (
            :servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId
      )
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Long countDemandasFinalizadasComAtraso(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT d.status AS status, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:servicoResponsavelId IS NULL OR d.servico_responsavel_id = :servicoResponsavelId)
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    GROUP BY d.status
    """, nativeQuery = true)
    List<StatusQuantidadeProjection> agruparPorStatus(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT d.desfecho AS desfecho, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:servicoResponsavelId IS NULL 
           OR d.servico_responsavel_id = :servicoResponsavelId)
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
        AND d.desfecho IS NOT NULL
    GROUP BY d.desfecho
    """, nativeQuery = true)
    List<DesfechoQuantidadeProjection> agruparPorDesfecho(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT d.motivo_busca_ativa AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:servicoResponsavelId IS NULL 
            OR d.servico_responsavel_id = :servicoResponsavelId)
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
      AND d.motivo_busca_ativa IS NOT NULL
    GROUP BY d.motivo_busca_ativa
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisMotivos(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT d.motivo_complemento AS motivo, COUNT(*) AS quantidade
    FROM demanda d
    WHERE (:servicoResponsavelId IS NULL 
            OR d.servico_responsavel_id = :servicoResponsavelId)
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
      AND d.motivo_complemento IS NOT NULL
    GROUP BY d.motivo_complemento
    """, nativeQuery = true)
    List<MotivoQuantidadeProjection> listarPrincipaisComplementos(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT AVG(
        EXTRACT(EPOCH FROM (
            d.data_hora_finalizacao - d.data_hora_limite
        ))
    )
    FROM demanda d
    WHERE
        d.status = 'FINALIZADA'
        AND d.data_hora_finalizacao > d.data_hora_limite
        AND (
            :servicoResponsavelId IS NULL
            OR d.servico_responsavel_id = :servicoResponsavelId
        )
        AND (
            :servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId
        )
        AND (
            (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
            AND
            (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
        )
    """, nativeQuery = true)
    Double calcularTempoMedioAtraso(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao)))
    FROM demanda d
    WHERE d.status = 'FINALIZADA'
      AND (:servicoResponsavelId IS NULL OR d.servico_responsavel_id = :servicoResponsavelId)
      AND (:servicoSolicitanteId IS NULL
            OR (:servicoSolicitanteId = -1 AND d.servico_solicitante_id IS NULL)
            OR d.servico_solicitante_id = :servicoSolicitanteId)
      AND (
        (CAST(:inicio AS DATE) IS NULL OR d.data_hora_criacao >= CAST(:inicio AS DATE))
        AND
        (CAST(:fim AS DATE) IS NULL OR d.data_hora_criacao < CAST(:fim AS DATE) + INTERVAL '1 day')
    )
    """, nativeQuery = true)
    Double calcularTempoMedioResolucao(
            Long servicoResponsavelId,
            Long servicoSolicitanteId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query(value = """
    SELECT 
        u.id AS servicoId,
        u.nome AS servicoNome,
        COUNT(d.id) AS valor
    FROM servico u
    LEFT JOIN demanda d ON d.servico_responsavel_id = u.id
    GROUP BY u.id, u.nome
    HAVING COUNT(d.id) > 0
    ORDER BY valor DESC, u.nome ASC
    """, nativeQuery = true)
    List<RankingQuantidadeProjection> rankingServicosPorTotalDemandas();

    @Query(value = """
    SELECT
        u.id AS servicoId,
        u.nome AS servicoNome,
        COUNT(d.id) FILTER (WHERE d.status = 'FINALIZADA') * 100.0 / COUNT(d.id) AS valor
    FROM servico u
    LEFT JOIN demanda d ON d.servico_responsavel_id = u.id
    GROUP BY u.id, u.nome
    HAVING COUNT(d.id) > 0
    ORDER BY valor DESC, u.nome ASC
    """, nativeQuery = true)
    List<RankingValorProjection> rankingServicosPorPercentualResolucao();

    @Query(value = """
    SELECT
        u.id AS servicoId,
        u.nome AS servicoNome,
        AVG(EXTRACT(EPOCH FROM (d.data_hora_finalizacao - d.data_hora_criacao))) AS valor
    FROM servico u
    JOIN demanda d ON d.servico_responsavel_id = u.id
    WHERE d.data_hora_finalizacao IS NOT NULL
    GROUP BY u.id, u.nome
    ORDER BY valor ASC, u.nome ASC
    """, nativeQuery = true)
    List<RankingValorProjection> rankingServicosPorTempoMedioResolucao();
}