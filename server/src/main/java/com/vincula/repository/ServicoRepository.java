package com.vincula.repository;

import com.vincula.entity.Servico;
import com.vincula.enums.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long>, JpaSpecificationExecutor<Servico> {

    @Query("""
    SELECT u
    FROM Servico u
    ORDER BY u.tipoServico, u.nome ASC
""")
    List<Servico> findAllByOrderByTipoServicoAndNomeAsc();

    List<Servico> findAllByTipoServicoOrderByNomeAsc(TipoServico tipoServico);

    boolean existsByCnes(String cnes);

    boolean existsByCnesAndIdNot(String cnes, Long id);

    Optional<Servico> findByCnes(String cnes);

    List<Servico> findAllByCnesIn(Collection<String> cnes);
}