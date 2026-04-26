package com.vincula.service.indicador;

import com.vincula.dto.dashboard.IndicadorValorDTO;
import com.vincula.dto.projection.DesfechoQuantidadeProjection;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;

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

        return demandaRepository.agruparPorDesfecho()
                .stream()
                .map(item -> toIndicador(item, totalFinalizadas))
                .toList();
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidade(Long unidadeResponsavelId) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeResponsavelId(
                StatusDemanda.FINALIZADA, unidadeResponsavelId
        );

        return demandaRepository.agruparPorDesfechoEUnidade(unidadeResponsavelId)
                .stream()
                .map(item -> toIndicador(item, totalFinalizadas))
                .toList();
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA, inicio, fim
        );

        return demandaRepository.agruparPorDesfechoPorPeriodo(inicio, fim)
                .stream()
                .map(item -> toIndicador(item, totalFinalizadas))
                .toList();
    }

    public List<IndicadorValorDTO> percentualPorDesfechoPorUnidadeEPeriodo(Long unidadeResponsavelId,
                                                                           LocalDateTime inicio,
                                                                           LocalDateTime fim) {
        double totalFinalizadas = demandaRepository.countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA, unidadeResponsavelId, inicio, fim
        );

        return demandaRepository.agruparPorDesfechoEUnidadePorPeriodo(unidadeResponsavelId, inicio, fim)
                .stream()
                .map(item -> toIndicador(item, totalFinalizadas))
                .toList();
    }

    private IndicadorValorDTO toIndicador(DesfechoQuantidadeProjection item, double totalFinalizadas) {
        return new IndicadorValorDTO(
                traduzirDesfecho(item.getDesfecho()),
                arredondar(percentual(totalFinalizadas, item.getQuantidade()))
        );
    }

    private double percentual(double total, long valor) {
        return total == 0 ? 0.0 : (valor * 100.0 / total);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String traduzirDesfecho(String desfecho) {
        return switch (desfecho) {
            case "ENCONTRADO_VINCULADO" -> "Encontrado e vinculado à APS (%)";
            case "ENCONTRADO_RECUSOU" -> "Encontrado e recusou atendimento (%)";
            case "NAO_LOCALIZADO" -> "Não localizado (%)";
            case "ENDERECO_INCORRETO" -> "Endereço incorreto (%)";
            case "MUDOU_TERRITORIO" -> "Mudou de território (%)";
            case "OBITO" -> "Óbito (%)";
            case "OUTRO" -> "Outro (%)";
            default -> desfecho;
        };
    }
}