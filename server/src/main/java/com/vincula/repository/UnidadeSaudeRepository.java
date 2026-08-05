package com.vincula.repository;

import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long>, JpaSpecificationExecutor<UnidadeSaude> {

    List<UnidadeSaude> findTop10ByNomeContainingIgnoreCaseOrderByNome(String nome);

    @Query("""
    SELECT u
    FROM UnidadeSaude u
    ORDER BY u.tipoServico, u.nome ASC
""")
    List<UnidadeSaude> findAllByOrderByTipoServicoAndNomeAsc();

    List<UnidadeSaude> findAllByTipoServicoOrderByNomeAsc(TipoServico tipoServico);

    List<UnidadeSaude> findAllByTipoServicoNotOrderByNomeAsc(TipoServico tipoServico);

    boolean existsByCnes(String cnes);

    boolean existsByCnesAndIdNot(String cnes, Long id);

    Optional<UnidadeSaude> findByCnes(String cnes);
}