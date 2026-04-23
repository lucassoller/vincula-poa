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

    public List<IndicadorValorDTO> indicadoresPorUnidade(Long unidadeResponsavelId) {
        return List.of(
                new IndicadorValorDTO("Total de demandas", demandaRepository.countByUnidadeResponsavelId(unidadeResponsavelId)),
                new IndicadorValorDTO("Demandas abertas", demandaRepository.countByStatusAndUnidadeResponsavelId(StatusDemanda.ABERTA, unidadeResponsavelId)),
                new IndicadorValorDTO("Demandas em andamento", demandaRepository.countByStatusAndUnidadeResponsavelId(StatusDemanda.EM_ANDAMENTO, unidadeResponsavelId)),
                new IndicadorValorDTO("Demandas finalizadas", demandaRepository.countByStatusAndUnidadeResponsavelId(StatusDemanda.FINALIZADA, unidadeResponsavelId))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("Total de demandas no período", demandaRepository.countByDataHoraCriacaoBetween(inicio, fim)),
                new IndicadorValorDTO("Demandas abertas no período", demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.ABERTA, inicio, fim)),
                new IndicadorValorDTO("Demandas em andamento no período", demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.EM_ANDAMENTO, inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas que foram criadas no período", demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas no período", demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("Total de demandas no período", demandaRepository.countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(unidadeResponsavelId, inicio, fim)),
                new IndicadorValorDTO("Demandas abertas no período", demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda.ABERTA, unidadeResponsavelId, inicio, fim)),
                new IndicadorValorDTO("Demandas em andamento no período", demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda.EM_ANDAMENTO, unidadeResponsavelId, inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas que foram criadas no período", demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeResponsavelId, inicio, fim)),
                new IndicadorValorDTO("Demandas finalizadas no período", demandaRepository.countByUnidadeResponsavelIdAndDataHoraFinalizacaoBetween(unidadeResponsavelId, inicio, fim))
        );
    }
}