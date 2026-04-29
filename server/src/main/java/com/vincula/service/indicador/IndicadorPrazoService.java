package com.vincula.service.indicador;

import com.vincula.dto.dashboard.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class IndicadorPrazoService {

    private final DemandaRepository demandaRepository;

    public IndicadorPrazoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> indicadoresPrazo() {
        double total = demandaRepository.countBy();

        long dentroPrazo = demandaRepository.countDemandasDentroDoPrazo();
        long atrasadas = demandaRepository.countDemandasAtrasadas();
        long finalizadasComAtraso = demandaRepository.countDemandasFinalizadasComAtraso();

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo (%)", percentual(total, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas (%)", percentual(total, atrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso (%)", percentual(total, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(demandaRepository.tempoMedioAtrasoEmSegundos()))
        );
    }

    public List<IndicadorValorDTO> indicadoresPrazoPorUnidade(Long unidadeId) {
        double total = demandaRepository.countByUnidadeResponsavelId(unidadeId);

        long dentroPrazo = demandaRepository.countDentroPrazoPorUnidade(unidadeId);
        long atrasadas = demandaRepository.countAtrasadasPorUnidade(unidadeId);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorUnidade(unidadeId);

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo (%)", percentual(total, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas (%)", percentual(total, atrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso (%)", percentual(total, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(demandaRepository.tempoMedioAtrasoEmSegundos()))
        );
    }

    private double percentual(double total, double valor) {
        if (total == 0) {
            return 0.0;
        }

        return arredondar(valor * 100.0 / total);
    }

    private double arredondar(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String formatarTempo(Double totalSegundos) {
        if (totalSegundos == null || totalSegundos <= 0) {
            return "0h 0m 0s";
        }

        long segundosTotais = Math.round(totalSegundos);

        long horas = segundosTotais / 3600;
        long minutos = (segundosTotais % 3600) / 60;
        long segundos = segundosTotais % 60;

        return horas + "h " + minutos + "m " + segundos + "s";
    }
}