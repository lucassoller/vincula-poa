package com.vincula.repository;

import com.vincula.entity.Usuario;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.TipoServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {

    Page<UnidadeSaude> findAllByOrderByNomeAsc(Pageable pageable);

    @Query("""
    SELECT u
    FROM UnidadeSaude u
    ORDER BY u.tipoServico, u.nome ASC
""")
    List<UnidadeSaude> findAllByOrderByTipoServicoAndNomeAsc();

    List<UnidadeSaude> findAllByTipoServicoOrderByNomeAsc(TipoServico tipoServico);

    List<UnidadeSaude> findAllByTipoServicoNotOrderByNomeAsc(TipoServico tipoServico);

    @Query("""
    SELECT u
    FROM UnidadeSaude u
    WHERE
        LOWER(u.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.cnes) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.tipoServico) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.telefone) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.telefone2) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.endereco.bairro) LIKE LOWER(CONCAT('%', :filtro, '%'))
        OR LOWER(u.endereco.rua) LIKE LOWER(CONCAT('%', :filtro, '%'))
    ORDER BY u.nome ASC
""")
    Page<UnidadeSaude> buscarFiltradas(
            String filtro,
            Pageable pageable
    );

    boolean existsByCnes(String cnes);

    boolean existsByCnesAndIdNot(String cnes, Long id);

    Optional<UnidadeSaude> findByCnes(String cnes);

    @Query("""
       SELECT p
       FROM Usuario p
       LEFT JOIN FETCH p.endereco
       WHERE p.unidadeSaude.id = :unidadeSaudeId
       """)
    List<Usuario> findUsuariosByUnidadeSaudeId(Long unidadeSaudeId);
}