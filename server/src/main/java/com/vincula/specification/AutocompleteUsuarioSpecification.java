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

        if (filtro.getUnidadeSaudeId() != null) {
            spec = spec.and(unidadeSaude(filtro.getUnidadeSaudeId()));
        }

        if (filtro.getUnidadeSolicitanteId() != null) {
            spec = spec.and(unidadeSolicitante(filtro.getUnidadeSolicitanteId()));
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

    public static Specification<Usuario> unidadeSaude(Long unidadeId) {

        return (root, query, cb) ->
                cb.equal(root.get("unidadeSaude").get("id"), unidadeId);
    }

    public static Specification<Usuario> unidadeSolicitante(Long unidadeId) {

        return (root, query, cb) ->
                cb.equal(root.get("unidadeSolicitante").get("id"), unidadeId);
    }

    public static Specification<Usuario> id(Long id) {

        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }
}