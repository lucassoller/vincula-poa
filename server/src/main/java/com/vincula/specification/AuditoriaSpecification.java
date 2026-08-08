package com.vincula.specification;

import com.vincula.dto.auditoria.FiltroAuditoriaRequestDTO;
import com.vincula.entity.Auditoria;
import com.vincula.enums.PerfilServidor;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class AuditoriaSpecification {

    public static Specification<Auditoria> comFiltros(FiltroAuditoriaRequestDTO filtro) {

        Specification<Auditoria> spec =
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

        if (filtro.getDataInicial() != null || filtro.getDataFinal() != null) {
            spec = spec.and(dataHora(
                    filtro.getDataInicial(),
                    filtro.getDataFinal()));
        }


        if (filtro.getServicoId() != null) {
            spec = spec.and(servico(filtro.getServicoId()));
        }

        return spec;
    }

    private static Specification<Auditoria> nome(String nome) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("servidor").get("nome")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Auditoria> perfil(List<PerfilServidor> perfis) {

        return (root, query, cb) -> root.get("servidor").get("perfil").in(perfis);
    }

    public static Specification<Auditoria> servico(Long servicoId) {

        return (root, query, cb) ->
                cb.equal(root.get("servidor").get("servico").get("id"), servicoId);
    }

    private static Specification<Auditoria> dataHora(
            LocalDateTime inicio,
            LocalDateTime fim) {

        return (root, query, cb) -> {

            Path<LocalDateTime> data = root.get("dataHora");

            if (inicio != null && fim != null) {
                return cb.between(data, inicio, fim);
            }

            if (inicio != null) {
                return cb.greaterThanOrEqualTo(data, inicio);
            }

            return cb.lessThanOrEqualTo(data, fim);
        };
    }

    public static Specification<Auditoria> id(Long id) {

        return (root, query, cb) ->
                cb.equal(root.get("servidor").get("id"), id);
    }
}