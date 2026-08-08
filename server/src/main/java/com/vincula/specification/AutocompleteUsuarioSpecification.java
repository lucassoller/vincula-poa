package com.vincula.specification;

import com.vincula.dto.usuario.AutocompleteUsuarioRequestDTO;
import com.vincula.entity.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class AutocompleteUsuarioSpecification {

    public static Specification<Usuario> comFiltros(AutocompleteUsuarioRequestDTO filtro) {

        Specification<Usuario> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (filtro.getNomeCompleto() != null && !filtro.getNomeCompleto().isBlank()) {
            spec = spec.and(nomeCompleto(filtro.getNomeCompleto()));
        }

        if (filtro.getServicoId() != null) {
            spec = spec.and(servico(filtro.getServicoId()));
        }

        if (filtro.getServicoSolicitanteId() != null) {
            spec = spec.and(servicoSolicitante(filtro.getServicoSolicitanteId()));
        }

        return spec;
    }

    private static Specification<Usuario> nomeCompleto(String nome) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nomeCompleto")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    public static Specification<Usuario> servico(Long servicoId) {

        return (root, query, cb) ->
                cb.equal(root.get("servico").get("id"), servicoId);
    }

    public static Specification<Usuario> servicoSolicitante(Long servicoId) {

        return (root, query, cb) ->
                cb.equal(root.get("servicoSolicitante").get("id"), servicoId);
    }

    public static Specification<Usuario> id(Long id) {

        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }
}