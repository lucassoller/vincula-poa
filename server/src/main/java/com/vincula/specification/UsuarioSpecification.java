package com.vincula.specification;

import com.vincula.dto.usuario.FiltroUsuarioRequestDTO;
import com.vincula.entity.Usuario;
import com.vincula.enums.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecification {

    public static Specification<Usuario> comFiltros(FiltroUsuarioRequestDTO filtro) {

        Specification<Usuario> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (filtro.getId() != null) {
            spec = spec.and(id(filtro.getId()));
        }

        if (filtro.getNomeCompleto() != null && !filtro.getNomeCompleto().isBlank()) {
            spec = spec.and(nomeCompleto(filtro.getNomeCompleto()));
        }

        if (filtro.getFaixaEtaria() != null && !filtro.getFaixaEtaria().isEmpty()) {
            spec = spec.and(faixaEtaria(filtro.getFaixaEtaria()));
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

    private static Specification<Usuario> faixaEtaria(List<FaixaEtaria> faixas) {

        return (root, query, cb) -> {

            LocalDate hoje = LocalDate.now();

            List<Predicate> predicates = new ArrayList<>();

            for (FaixaEtaria faixa : faixas) {

                switch (faixa) {

                    case CRIANCA -> predicates.add(
                            cb.greaterThan(
                                    root.get("dataNascimento"),
                                    hoje.minusYears(12)
                            )
                    );

                    case ADOLESCENTE -> predicates.add(
                            cb.between(
                                    root.get("dataNascimento"),
                                    hoje.minusYears(18).plusDays(1),
                                    hoje.minusYears(12)
                            )
                    );

                    case ADULTO -> predicates.add(
                            cb.between(
                                    root.get("dataNascimento"),
                                    hoje.minusYears(60).plusDays(1),
                                    hoje.minusYears(18)
                            )
                    );

                    case IDOSO -> predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get("dataNascimento"),
                                    hoje.minusYears(60)
                            )
                    );
                }
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Usuario> unidadeSaude(Long unidadeId) {

        if (unidadeId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("unidadeSaude").get("id"), unidadeId);
    }

    public static Specification<Usuario> unidadeSolicitante(Long unidadeId) {

        if (unidadeId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("unidadeSolicitante").get("id"), unidadeId);
    }

    public static Specification<Usuario> id(Long id) {

        if (id == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }
}