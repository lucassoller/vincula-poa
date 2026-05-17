package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorPrazoService {

    private final DemandaRepository demandaRepository;

    public IndicadorPrazoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> indicadoresPrazo() {
        long dentroPrazo = demandaRepository.countDemandasDentroDoPrazo();
        long abertasAtrasadas = demandaRepository.countDemandasAtrasadas();
        long finalizadasComAtraso = demandaRepository.countDemandasFinalizadasComAtraso();

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(demandaRepository.tempoMedioAtrasoEmSegundos()))
        );
    }

    public List<IndicadorValorDTO> indicadoresPrazoPorUnidade(Long unidadeId) {
        long dentroPrazo = demandaRepository.countDentroPrazoPorUnidade(unidadeId);
        long abertasAtrasadas = demandaRepository.countAtrasadasPorUnidade(unidadeId);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorUnidade(unidadeId);

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(demandaRepository.tempoMedioAtrasoPorUnidade(unidadeId)))
        );
    }

    public List<IndicadorValorDTO> indicadoresPrazoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        long dentroPrazo = demandaRepository.countDentroPrazoPorPeriodo(inicio, fim);
        long abertasAtrasadas = demandaRepository.countDemandasAtrasadasPorPeriodo(inicio, fim);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorPeriodo(inicio, fim);

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(
                        demandaRepository.tempoMedioAtrasoEmSegundosPorPeriodo(inicio, fim)
                ))
        );
    }

    public List<IndicadorValorDTO> indicadoresPrazoPorUnidadeEPeriodo(
            Long unidadeId,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        long dentroPrazo = demandaRepository.countDentroPrazoPorUnidadeEPeriodo(unidadeId, inicio, fim);
        long abertasAtrasadas = demandaRepository.countDemandasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim);

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(
                        demandaRepository.tempoMedioAtrasoEmSegundosPorUnidadeEPeriodo(unidadeId, inicio, fim)
                ))
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