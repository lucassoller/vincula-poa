package com.vincula.specification;

import com.vincula.dto.demanda.FiltroDemandaRequestDTO;
import com.vincula.entity.Demanda;
import com.vincula.enums.*;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DemandaSpecification {

    public static Specification<Demanda> comFiltros(FiltroDemandaRequestDTO filtro) {

        Specification<Demanda> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (filtro.getStatus() != null && !filtro.getStatus().isEmpty()) {
            spec = spec.and(status(filtro.getStatus()));
        }

        if (filtro.getPrioridade() != null && !filtro.getPrioridade().isEmpty()) {
            spec = spec.and(prioridade(filtro.getPrioridade()));
        }

        if (filtro.getMotivo() != null) {
            spec = spec.and(motivo(filtro.getMotivo()));
        }

        if (filtro.getComplemento() != null) {
            spec = spec.and(complemento(filtro.getComplemento()));
        }

        if (filtro.getUnidadeResponsavelId() != null) {
            spec = spec.and(unidadeResponsavel(filtro.getUnidadeResponsavelId()));
        }

        if (filtro.getUnidadeSolicitanteId() != null) {
            spec = spec.and(unidadeSolicitante(filtro.getUnidadeSolicitanteId()));
        }

        if (filtro.getUsuarioId() != null) {
            spec = spec.and(usuario(filtro.getUsuarioId()));
        }

        if (filtro.getDataAbInicial() != null || filtro.getDataAbFinal() != null) {
            spec = spec.and(dataAbertura(
                    filtro.getDataAbInicial(),
                    filtro.getDataAbFinal()));
        }

        if (filtro.getDataEnInicial() != null || filtro.getDataEnFinal() != null) {
            spec = spec.and(dataEncerramento(
                    filtro.getDataEnInicial(),
                    filtro.getDataEnFinal()));
        }

        if (filtro.getTempo() != null && !filtro.getTempo().isEmpty()) {
            spec = spec.and(tempo(filtro.getTempo()));
        }

        return spec;
    }

    private static Specification<Demanda> status(List<StatusDemanda> status) {

        return (root, query, cb) -> root.get("status").in(status);
    }

    private static Specification<Demanda> prioridade(List<Prioridade> prioridades) {

        return (root, query, cb) -> root.get("prioridade").in(prioridades);
    }

    private static Specification<Demanda> motivo(MotivoBuscaAtiva motivo) {

        return (root, query, cb) ->
                cb.equal(root.get("motivoBuscaAtiva"), motivo);
    }

    private static Specification<Demanda> complemento(MotivoComplemento complemento) {

        return (root, query, cb) ->
                cb.equal(root.get("motivoComplemento"), complemento);
    }

    public static Specification<Demanda> unidadeResponsavel(Long unidadeId) {

        return (root, query, cb) ->
                cb.equal(root.get("unidadeResponsavel").get("id"), unidadeId);
    }

    public static Specification<Demanda> unidadeSolicitante(Long unidadeId) {

        return (root, query, cb) -> {
            if (unidadeId == -1L) {
                return cb.isNull(root.get("unidadeSolicitante"));
            }

            return cb.equal(root.get("unidadeSolicitante").get("id"), unidadeId);
        };
    }

    public static Specification<Demanda> usuario(Long usuarioId) {

        return (root, query, cb) ->
                cb.equal(root.get("usuario").get("id"), usuarioId);
    }

    private static Specification<Demanda> dataAbertura(LocalDate inicio,
                                                       LocalDate fim) {

        return (root, query, cb) -> {

            Path<LocalDateTime> data = root.get("dataHoraCriacao");

            if (inicio != null && fim != null) {
                return cb.between(
                        data,
                        inicio.atStartOfDay(),
                        fim.atTime(LocalTime.MAX)
                );
            }

            if (inicio != null) {
                return cb.greaterThanOrEqualTo(
                        data,
                        inicio.atStartOfDay()
                );
            }

            return cb.lessThanOrEqualTo(
                    data,
                    fim.atTime(LocalTime.MAX)
            );
        };
    }

    private static Specification<Demanda> dataEncerramento(LocalDate inicio,
                                                       LocalDate fim) {

        return (root, query, cb) -> {

            Path<LocalDateTime> data = root.get("dataHoraEncerramento");

            if (inicio != null && fim != null) {
                return cb.between(
                        data,
                        inicio.atStartOfDay(),
                        fim.atTime(LocalTime.MAX)
                );
            }

            if (inicio != null) {
                return cb.greaterThanOrEqualTo(
                        data,
                        inicio.atStartOfDay()
                );
            }

            return cb.lessThanOrEqualTo(
                    data,
                    fim.atTime(LocalTime.MAX)
            );
        };
    }

    private static Specification<Demanda> tempo(List<PrazoAtual> tempos) {

        return (root, query, cb) -> {

            LocalDateTime agora = LocalDateTime.now();

            List<Predicate> predicates = new ArrayList<>();

            for (PrazoAtual tempo : tempos) {

                switch (tempo) {

                    case ATRASADA ->
                            predicates.add(
                                    cb.lessThan(root.get("dataHoraLimite"), agora)
                            );

                    case NO_PRAZO ->
                            predicates.add(
                                    cb.greaterThanOrEqualTo(root.get("dataHoraLimite"), agora)
                            );

                    case HOJE -> {
                        LocalDate hoje = LocalDate.now();

                        predicates.add(
                                cb.between(
                                        root.get("dataHoraLimite"),
                                        hoje.atStartOfDay(),
                                        hoje.atTime(LocalTime.MAX)
                                )
                        );
                    }

                    case ATE_3 ->
                            predicates.add(
                                    cb.between(
                                            root.get("dataHoraLimite"),
                                            agora,
                                            agora.plusDays(3)
                                    )
                            );

                    case ATE_7 ->
                            predicates.add(
                                    cb.between(
                                            root.get("dataHoraLimite"),
                                            agora,
                                            agora.plusDays(7)
                                    )
                            );

                    case ATE_15 ->
                            predicates.add(
                                    cb.between(
                                            root.get("dataHoraLimite"),
                                            agora,
                                            agora.plusDays(15)
                                    )
                            );

                    case ATE_30 ->
                            predicates.add(
                                    cb.between(
                                            root.get("dataHoraLimite"),
                                            agora,
                                            agora.plusDays(30)
                                    )
                            );
                }
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}