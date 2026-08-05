package com.vincula.specification;

import com.vincula.dto.unidadeSaude.FiltroServicoRequestDTO;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.TipoServico;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ServicoSpecification {

    public static Specification<UnidadeSaude> comFiltros(FiltroServicoRequestDTO filtro) {

        Specification<UnidadeSaude> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (filtro.getId() != null) {
            spec = spec.and(id(filtro.getId()));
        }

        if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
            spec = spec.and(nome(filtro.getNome()));
        }

        if (filtro.getTipoServico() != null && !filtro.getTipoServico().isEmpty()) {
            spec = spec.and(tipoServico(filtro.getTipoServico()));
        }

        return spec;
    }

    private static Specification<UnidadeSaude> nome(String nome) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    public static Specification<UnidadeSaude> id(Long id) {

        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }

    private static Specification<UnidadeSaude> tipoServico(List<TipoServico> tiposServico) {

        return (root, query, cb) -> root.get("tipoServico").in(tiposServico);
    }
}