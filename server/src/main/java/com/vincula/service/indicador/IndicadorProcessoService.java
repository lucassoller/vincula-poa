package com.vincula.service.indicador;

import com.vincula.dto.IndicadorValorDTO;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

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

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", formatarTempo(mediaSegundos));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidade(Long unidadeResponsavelId) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidade(unidadeResponsavelId);

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", formatarTempo(mediaSegundos));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorPeriodo(inicio, fim);

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", formatarTempo(mediaSegundos));
    }

    public IndicadorValorDTO tempoMedioResolucaoEmHorasPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        Double mediaSegundos = demandaRepository.calcularTempoMedioResolucaoEmSegundosPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim);

        return new IndicadorValorDTO("Tempo médio de resolução (horas)", formatarTempo(mediaSegundos));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativa() {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHoras();
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", formatarTempo(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidade(Long unidadeResponsavelId) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidade(unidadeResponsavelId);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", formatarTempo(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorPeriodo(inicio, fim);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)",formatarTempo(valor));
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativaPorUnidadeEPeriodo(Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorUnidadeEPeriodo(unidadeResponsavelId, inicio, fim);
        return new IndicadorValorDTO("Tempo até a primeira tentativa (horas)", formatarTempo(valor));
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

    public IndicadorValorDTO mediaTentativasPorUsuario() {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorUsuario();
        return new IndicadorValorDTO("Média de tentativas por usuário", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorUsuarioPorUnidade(Long unidadeSaudeId) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorUsuarioPorUnidade(unidadeSaudeId);
        return new IndicadorValorDTO("Média de tentativas por usuário", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorUsuarioPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorUsuarioPorPeriodo(inicio, fim);
        return new IndicadorValorDTO("Média de tentativas por usuário", arredondar(valor == null ? 0.0 : valor));
    }

    public IndicadorValorDTO mediaTentativasPorUsuarioPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorUsuarioPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim);
        return new IndicadorValorDTO("Média de tentativas por usuário", arredondar(valor == null ? 0.0 : valor));
    }

    public List<IndicadorValorDTO> montarProcessoGeral() {
        return List.of(
                percentualDemandasResolvidas(),
                tempoMedioResolucaoEmHoras(),
                tempoMedioAtePrimeiraTentativa(),
                mediaTentativasPorDemanda(),
                mediaTentativasPorUsuario()
        );
    }

    public List<IndicadorValorDTO> montarProcessoPorUnidade(Long unidadeSaudeId) {
        return List.of(
                percentualDemandasResolvidasPorUnidade(unidadeSaudeId),
                tempoMedioResolucaoEmHorasPorUnidade(unidadeSaudeId),
                tempoMedioAtePrimeiraTentativaPorUnidade(unidadeSaudeId),
                mediaTentativasPorDemandaPorUnidade(unidadeSaudeId),
                mediaTentativasPorUsuarioPorUnidade(unidadeSaudeId)
        );
    }

    public List<IndicadorValorDTO> montarProcessoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                percentualDemandasResolvidasPorPeriodo(inicio, fim),
                tempoMedioResolucaoEmHorasPorPeriodo(inicio, fim),
                tempoMedioAtePrimeiraTentativaPorPeriodo(inicio, fim),
                mediaTentativasPorDemandaPorPeriodo(inicio, fim),
                mediaTentativasPorUsuarioPorPeriodo(inicio, fim)
        );
    }

    public List<IndicadorValorDTO> montarProcessoPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return List.of(
                percentualDemandasResolvidasPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                tempoMedioResolucaoEmHorasPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                tempoMedioAtePrimeiraTentativaPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                mediaTentativasPorDemandaPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim),
                mediaTentativasPorUsuarioPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
        );
    }

    private double arredondar(Double valor) {
        if (valor == null) {
            return 0.0;
        }

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