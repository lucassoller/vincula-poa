package com.vincula.service.indicador;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class IndicadorProcessoService {

    private final DemandaRepository demandaRepository;
    private final TentativaContatoRepository tentativaContatoRepository;

    public IndicadorProcessoService(DemandaRepository demandaRepository,
                                    TentativaContatoRepository tentativaContatoRepository) {
        this.demandaRepository = demandaRepository;
        this.tentativaContatoRepository = tentativaContatoRepository;
    }

    public IndicadorValorDTO percentualDemandasResolvidas() {
        double total = demandaRepository.countBy();
        double finalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);
        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("Percentual de demandas resolvidas (%)", arredondar(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorUnidade(Long unidadeResponsavelId) {
        double total = demandaRepository.countByUnidadeResponsavelId(unidadeResponsavelId);
        double finalizadas = demandaRepository.countByStatusAndUnidadeResponsavelId(StatusDemanda.FINALIZADA, unidadeResponsavelId);
        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("Percentual de demandas resolvidas (%)", arredondar(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        double total = demandaRepository.countByDataHoraCriacaoBetween(inicio, fim);
        double finalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim);
        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("Percentual de demandas resolvidas (%)", arredondar(percentual));
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        double total = demandaRepository.countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(unidadeResponsavelId, inicio, fim);
        double finalizadas = demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeResponsavelId, inicio, fim);
        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("Percentual de demandas resolvidas (%)", arredondar(percentual));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHoras() {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundos();
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", arredondar(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidade(Long unidadeResponsavelId) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidade(unidadeResponsavelId);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", arredondar(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorPeriodo(inicio, fim);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", arredondar(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim);
        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", arredondar(mediaHoras));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativa() {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHoras();
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidade(Long unidadeResponsavelId) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidade(unidadeResponsavelId);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorPeriodo(inicio, fim);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemanda() {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemanda();
        return new IndicadorValorDTO("Média de tentativas por demanda", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorUnidade(Long unidadeResponsavelId) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorUnidade(unidadeResponsavelId);
        return new IndicadorValorDTO("Média de tentativas por demanda", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorPeriodo(inicio, fim);
        return new IndicadorValorDTO("Média de tentativas por demanda", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorDemandaPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemandaPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim);
        return new IndicadorValorDTO("Média de tentativas por demanda", arredondar(valor == null ? 0.0 : valor));
    }

    private double arredondar(Double valor) {
        if (valor == null) {
            return 0.0;
        }

        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}