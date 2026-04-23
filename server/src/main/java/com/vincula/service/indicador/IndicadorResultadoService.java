package com.vincula.service.indicador;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorResultadoService {

    private final DemandaRepository demandaRepository;

    public IndicadorResultadoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> percentualPorDesfecho() {
        double totalFinalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);
        return montarPercentuaisGerais(totalFinalizadas, null, null);
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidade(Long unidadeResponsavelId) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeResponsavelId(StatusDemanda.FINALIZADA, unidadeResponsavelId);
        return montarPercentuaisUnidade(totalFinalizadas, unidadeResponsavelId, null, null);
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim);
        return montarPercentuaisGerais(totalFinalizadas, inicio, fim);
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeResponsavelId, inicio, fim);
        return montarPercentuaisUnidade(totalFinalizadas, unidadeResponsavelId, inicio, fim);
    }

    private List<IndicadorValorDTO> montarPercentuaisGerais(double totalFinalizadas, LocalDateTime inicio, LocalDateTime fim) {
        double encontrado;
        double naoEncontrado;
        double obito;
        double foraDoTerritorio;

        if (inicio == null || fim == null) {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
        } else {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
        }

        return List.of(
                new IndicadorValorDTO("Percentual de usuários encontrados (%)", arredondar(encontrado)),
                new IndicadorValorDTO("Percentual de usuários não encontrados (%)", arredondar(naoEncontrado)),
                new IndicadorValorDTO("Percentual de óbitos (%)", arredondar(obito)),
                new IndicadorValorDTO("Percentual fora do território (%)", arredondar(foraDoTerritorio))
        );
    }

    private List<IndicadorValorDTO> montarPercentuaisUnidade(double totalFinalizadas, Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        double encontrado;
        double naoEncontrado;
        double obito;
        double foraDoTerritorio;

        if (inicio == null || fim == null) {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OBITO, unidadeResponsavelId));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OBITO, unidadeResponsavelId));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OBITO, unidadeResponsavelId));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OBITO, unidadeResponsavelId));
        } else {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeResponsavelId, inicio, fim));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeResponsavelId, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeResponsavelId, inicio, fim));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeResponsavelId, inicio, fim));
        }

        return List.of(
                new IndicadorValorDTO("Percentual de usuários encontrados (%)", arredondar(encontrado)),
                new IndicadorValorDTO("Percentual de usuários não encontrados (%)", arredondar(naoEncontrado)),
                new IndicadorValorDTO("Percentual de óbitos (%)", arredondar(obito)),
                new IndicadorValorDTO("Percentual fora do território (%)", arredondar(foraDoTerritorio))
        );
    }

    private double percentual(double total, double valor) {
        return total == 0 ? 0.0 : (valor / total) * 100.0;
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