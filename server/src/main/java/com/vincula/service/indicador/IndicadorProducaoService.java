package com.vincula.service.indicador;

import com.vincula.dto.dashboard.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndicadorProducaoService {

    private final DemandaRepository demandaRepository;

    public IndicadorProducaoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> indicadoresGerais() {
        List<IndicadorValorDTO> lista = new ArrayList<>(
                demandaRepository.agruparPorStatus()
                        .stream()
                        .map(item -> new IndicadorValorDTO(traduzirStatus(item.getStatus()), item.getQuantidade()))
                        .toList()
        );

        lista.add(new IndicadorValorDTO("Total de demandas", demandaRepository.countBy()));
        return lista;
    }

    public List<IndicadorValorDTO> indicadoresPorUnidade(Long unidadeResponsavelId) {
        List<IndicadorValorDTO> lista = new ArrayList<>(
                demandaRepository.agruparPorStatusPorUnidade(unidadeResponsavelId)
                        .stream()
                        .map(item -> new IndicadorValorDTO(traduzirStatus(item.getStatus()), item.getQuantidade()))
                        .toList()
        );

        lista.add(new IndicadorValorDTO("Total de demandas", demandaRepository.countByUnidadeResponsavelId(unidadeResponsavelId)));
        return lista;
    }

    public List<IndicadorValorDTO> indicadoresPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<IndicadorValorDTO> lista = new ArrayList<>(
                demandaRepository.agruparPorStatusPorPeriodo(inicio, fim)
                        .stream()
                        .map(item -> new IndicadorValorDTO(traduzirStatus(item.getStatus()), item.getQuantidade()))
                        .toList()
        );

        lista.add(new IndicadorValorDTO("Total de demandas", demandaRepository.countByDataHoraCriacaoBetween(inicio, fim)));
        lista.add(new IndicadorValorDTO("Demandas finalizadas com data de finalização no período", demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim)));

        return lista;
    }

    public List<IndicadorValorDTO> indicadoresPorUnidadeEPeriodo(Long unidadeResponsavelId,
                                                                 LocalDateTime inicio,
                                                                 LocalDateTime fim) {

        List<IndicadorValorDTO> lista = new ArrayList<>(
                demandaRepository.agruparPorStatusPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim)
                        .stream()
                        .map(item -> new IndicadorValorDTO(traduzirStatus(item.getStatus()), item.getQuantidade()))
                        .toList()
        );

        lista.add(new IndicadorValorDTO("Total de demandas", demandaRepository.countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(unidadeResponsavelId, inicio, fim)));
        lista.add(new IndicadorValorDTO("Demandas finalizadas com data de finalização no período", demandaRepository.countByUnidadeResponsavelIdAndDataHoraFinalizacaoBetween(unidadeResponsavelId, inicio, fim)));

        return lista;
    }

    private String traduzirStatus(String status) {
        return switch (status) {
            case "ABERTA" -> "Demandas abertas";
            case "EM_ANDAMENTO" -> "Demandas em andamento";
            case "FINALIZADA" -> "Demandas finalizadas com data de criação no período";
            default -> status;
        };
    }
}