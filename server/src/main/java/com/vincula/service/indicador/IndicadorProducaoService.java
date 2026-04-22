package com.vincula.service.indicador;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorProducaoService {

    private final DemandaRepository demandaRepository;

    public IndicadorProducaoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> indicadoresGerais() {
        return List.of(
                new IndicadorValorDTO("Total de demandas", demandaRepository.countBy()),
                new IndicadorValorDTO("Demandas abertas", demandaRepository.countByStatus(StatusDemanda.ABERTA)),
                new IndicadorValorDTO("Demandas em andamento", demandaRepository.countByStatus(StatusDemanda.EM_ANDAMENTO)),
                new IndicadorValorDTO("Demandas finalizadas", demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidade(Long unidadeSaudeId) {
        return List.of(
                new IndicadorValorDTO("Total de demandas", demandaRepository.countByUnidadeSaudeId(unidadeSaudeId)),
                new IndicadorValorDTO("Demandas abertas", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.ABERTA, unidadeSaudeId)),
                new IndicadorValorDTO("Demandas em andamento", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.EM_ANDAMENTO, unidadeSaudeId)),
                new IndicadorValorDTO("Demandas finalizadas", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("Demandas criadas no período", demandaRepository.countByDataHoraCriacaoBetween(inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas no período", demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("Demandas criadas no período", demandaRepository.countByUnidadeSaudeIdAndDataHoraCriacaoBetween(unidadeSaudeId, inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas no período", demandaRepository.countByUnidadeSaudeIdAndDataHoraFinalizacaoBetween(unidadeSaudeId, inicio, fim))
        );
    }
}