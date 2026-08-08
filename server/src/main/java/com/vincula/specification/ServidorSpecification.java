package com.vincula.specification;

import com.vincula.dto.servidor.FiltroServidorRequestDTO;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class ServidorSpecification {

    public static Specification<Servidor> comFiltros(FiltroServidorRequestDTO filtro) {

        Specification<Servidor> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (filtro.getId() != null) {
            spec = spec.and(id(filtro.getId()));
        }

        if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
            spec = spec.and(nome(filtro.getNome()));
        }

        if (filtro.getPerfil() != null && !filtro.getPerfil().isEmpty()) {
            spec = spec.and(perfil(filtro.getPerfil()));
        }

        if (filtro.getServicoId() != null) {
            spec = spec.and(servico(filtro.getServicoId()));
        }

        return spec;
    }

    private static Specification<Servidor> nome(String nome) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Servidor> perfil(List<PerfilServidor> perfis) {

        return (root, query, cb) -> root.get("perfil").in(perfis);
    }

    public static Specification<Servidor> servico(Long servicoId) {

        return (root, query, cb) -> {
            if (servicoId == -1L) {
                return cb.isNull(root.get("servico"));
            }

            return cb.equal(root.get("servico").get("id"), servicoId);
        };
    }

    public static Specification<Servidor> id(Long id) {

        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }
}