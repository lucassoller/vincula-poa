package com.vincula.service;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorService {

    private final DemandaRepository demandaRepository;

    public IndicadorService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> indicadoresGerais() {
        return List.of(
                new IndicadorValorDTO("totalDemandas", demandaRepository.countBy()),
                new IndicadorValorDTO("demandasAbertas", demandaRepository.countByStatus(StatusDemanda.ABERTA)),
                new IndicadorValorDTO("demandasEmAndamento", demandaRepository.countByStatus(StatusDemanda.EM_ANDAMENTO)),
                new IndicadorValorDTO("demandasFinalizadas", demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidade(Long unidadeSaudeId) {
        return List.of(
                new IndicadorValorDTO("totalDemandas", demandaRepository.countByUnidadeSaudeId(unidadeSaudeId)),
                new IndicadorValorDTO("demandasAbertas", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.ABERTA, unidadeSaudeId)),
                new IndicadorValorDTO("demandasEmAndamento", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.EM_ANDAMENTO, unidadeSaudeId)),
                new IndicadorValorDTO("demandasFinalizadas", demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorDesfecho() {
        return List.of(
                new IndicadorValorDTO("encontrado", demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO)),
                new IndicadorValorDTO("naoEncontrado", demandaRepository.countByDesfecho(DesfechoDemanda.NAO_ENCONTRADO)),
                new IndicadorValorDTO("obito", demandaRepository.countByDesfecho(DesfechoDemanda.OBITO)),
                new IndicadorValorDTO("foraDoTerritorio", demandaRepository.countByDesfecho(DesfechoDemanda.FORA_DO_TERRITORIO)),
                new IndicadorValorDTO("outro", demandaRepository.countByDesfecho(DesfechoDemanda.OUTRO))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorDesfechoEUnidade(Long unidadeSaudeId) {
        return List.of(
                new IndicadorValorDTO("encontrado", demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.ENCONTRADO, unidadeSaudeId)),
                new IndicadorValorDTO("naoEncontrado", demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.NAO_ENCONTRADO, unidadeSaudeId)),
                new IndicadorValorDTO("obito", demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.OBITO, unidadeSaudeId)),
                new IndicadorValorDTO("foraDoTerritorio", demandaRepository.countByDesfechoAndUnidadeSaudeId(DesfechoDemanda.FORA_DO_TERRITORIO, unidadeSaudeId)),
                new IndicadorValorDTO("outro", demandaRepository.countByDesfecho(DesfechoDemanda.OUTRO))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("demandasCriadas", demandaRepository.countByDataHoraCriacaoBetween(inicio, fim)),
                new IndicadorValorDTO("demandasFinalizadas", demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim))
        );
    }

    public List<IndicadorValorDTO> indicadoresPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                new IndicadorValorDTO("demandasCriadas", demandaRepository.countByUnidadeSaudeIdAndDataHoraCriacaoBetween(unidadeSaudeId, inicio, fim)),
                new IndicadorValorDTO("demandasFinalizadas", demandaRepository.countByUnidadeSaudeIdAndDataHoraFinalizacaoBetween(unidadeSaudeId, inicio, fim))
        );
    }

    public IndicadorValorDTO percentualDemandasResolvidas() {
        double total = demandaRepository.countBy();
        double finalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);

        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", percentual);
    }

    public IndicadorValorDTO percentualDemandasResolvidasPorUnidade(Long unidadeSaudeId) {
        double total = demandaRepository.countByUnidadeSaudeId(unidadeSaudeId);
        double finalizadas = demandaRepository.countByStatusAndUnidadeSaudeId(StatusDemanda.FINALIZADA, unidadeSaudeId);

        double percentual = total == 0 ? 0.0 : (finalizadas / total) * 100.0;

        return new IndicadorValorDTO("percentualDemandasResolvidas", percentual);
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHoras() {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundos();

        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", mediaHoras);
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidade(Long unidadeSaudeId) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidade(unidadeSaudeId);

        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", mediaHoras);
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorPeriodo(inicio, fim);

        double mediaHoras = mediaSegundos == null ? 0.0 : mediaSegundos / 3600.0;

        return new IndicadorValorDTO("tempoMedioResolucaoHoras", mediaHoras);
    }

    public List<IndicadorValorDTO> percentualPorDesfecho() {
        double totalFinalizadas = demandaRepository.countByStatus(StatusDemanda.FINALIZADA);

        double encontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO) * 100.0 / totalFinalizadas;

        double naoEncontrado = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.NAO_ENCONTRADO) * 100.0 / totalFinalizadas;

        double obito = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.OBITO) * 100.0 / totalFinalizadas;

        double foraDoTerritorio = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.FORA_DO_TERRITORIO) * 100.0 / totalFinalizadas;

        double outro = totalFinalizadas == 0 ? 0.0 :
                demandaRepository.countByDesfecho(DesfechoDemanda.OUTRO) * 100.0 / totalFinalizadas;

        return List.of(
                new IndicadorValorDTO("percentualEncontrado", encontrado),
                new IndicadorValorDTO("percentualNaoEncontrado", naoEncontrado),
                new IndicadorValorDTO("percentualObito", obito),
                new IndicadorValorDTO("percentualForaDoTerritorio", foraDoTerritorio),
                new IndicadorValorDTO("outro", outro)
        );
    }
}