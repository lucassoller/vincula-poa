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

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidade(Long unidadeSaudeId) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId);
        return montarPercentuaisUnidade(totalFinalizadas, unidadeSaudeId, null, null);
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, inicio, fim);
        return montarPercentuaisGerais(totalFinalizadas, inicio, fim);
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeSaudeIdAndDataHoraCriacaoBetween(StatusDemanda.FINALIZADA, unidadeSaudeId, inicio, fim);
        return montarPercentuaisUnidade(totalFinalizadas, unidadeSaudeId, inicio, fim);
    }

    private List<IndicadorValorDTO> montarPercentuaisGerais(double totalFinalizadas, LocalDateTime inicio, LocalDateTime fim) {
        double encontrado;
        double naoEncontrado;
        double obito;
        double foraDoTerritorio;

        if (inicio == null || fim == null) {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.NAO_ENCONTRADO));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.FORA_DO_TERRITORIO));
        } else {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO, inicio, fim));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_ENCONTRADO, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.FORA_DO_TERRITORIO, inicio, fim));
        }

        return List.of(
                new IndicadorValorDTO("Percentual de usuários encontrados (%)", arredondar(encontrado)),
                new IndicadorValorDTO("Percentual de usuários não encontrados (%)", arredondar(naoEncontrado)),
                new IndicadorValorDTO("Percentual de óbitos (%)", arredondar(obito)),
                new IndicadorValorDTO("Percentual fora do território (%)", arredondar(foraDoTerritorio))
        );
    }

    private List<IndicadorValorDTO> montarPercentuaisUnidade(double totalFinalizadas, Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        double encontrado;
        double naoEncontrado;
        double obito;
        double foraDoTerritorio;

        if (inicio == null || fim == null) {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.ENCONTRADO, unidadeSaudeId));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.NAO_ENCONTRADO, unidadeSaudeId));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.OBITO, unidadeSaudeId));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.FORA_DO_TERRITORIO, unidadeSaudeId));
        } else {
            encontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO, unidadeSaudeId, inicio, fim));
            naoEncontrado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_ENCONTRADO, unidadeSaudeId, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeSaudeId, inicio, fim));
            foraDoTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeSaudeIdAndDataHoraCriacaoBetween(DesfechoDemanda.FORA_DO_TERRITORIO, unidadeSaudeId, inicio, fim));
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