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

        double encontradoVinculado;
        double encontradoRecusou;
        double naoLocalizado;
        double enderecoIncorreto;
        double mudouTerritorio;
        double obito;
        double outro;

        if (inicio == null || fim == null) {
            encontradoVinculado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO_VINCULADO));
            encontradoRecusou = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.ENCONTRADO_RECUSOU));
            naoLocalizado = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.NAO_LOCALIZADO));
            enderecoIncorreto = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.ENDERECO_INCORRETO));
            mudouTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.MUDOU_TERRITORIO));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OBITO));
            outro = percentual(totalFinalizadas, demandaRepository.countByDesfecho(DesfechoDemanda.OUTRO));
        } else {
            encontradoVinculado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO_VINCULADO, inicio, fim));
            encontradoRecusou = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO_RECUSOU, inicio, fim));
            naoLocalizado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_LOCALIZADO, inicio, fim));
            enderecoIncorreto = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.ENDERECO_INCORRETO, inicio, fim));
            mudouTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.MUDOU_TERRITORIO, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, inicio, fim));
            outro = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndDataHoraCriacaoBetween(DesfechoDemanda.OUTRO, inicio, fim));
        }

        return List.of(
                new IndicadorValorDTO("ENCONTRADO_VINCULADO (%)", arredondar(encontradoVinculado)),
                new IndicadorValorDTO("ENCONTRADO_RECUSOU (%)", arredondar(encontradoRecusou)),
                new IndicadorValorDTO("NAO_LOCALIZADO (%)", arredondar(naoLocalizado)),
                new IndicadorValorDTO("ENDERECO_INCORRETO (%)", arredondar(enderecoIncorreto)),
                new IndicadorValorDTO("MUDOU_TERRITORIO (%)", arredondar(mudouTerritorio)),
                new IndicadorValorDTO("OBITO (%)", arredondar(obito)),
                new IndicadorValorDTO("OUTRO (%)", arredondar(outro))
        );
    }

    private List<IndicadorValorDTO> montarPercentuaisUnidade(double totalFinalizadas, Long unidadeResponsavelId, LocalDateTime inicio, LocalDateTime fim) {

        double encontradoVinculado;
        double encontradoRecusou;
        double naoLocalizado;
        double enderecoIncorreto;
        double mudouTerritorio;
        double obito;
        double outro;

        if (inicio == null || fim == null) {
            encontradoVinculado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.ENCONTRADO_VINCULADO, unidadeResponsavelId));
            encontradoRecusou = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.ENCONTRADO_RECUSOU, unidadeResponsavelId));
            naoLocalizado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.NAO_LOCALIZADO, unidadeResponsavelId));
            enderecoIncorreto = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.ENDERECO_INCORRETO, unidadeResponsavelId));
            mudouTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.MUDOU_TERRITORIO, unidadeResponsavelId));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OBITO, unidadeResponsavelId));
            outro = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelId(DesfechoDemanda.OUTRO, unidadeResponsavelId));
        } else {
            encontradoVinculado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO_VINCULADO, unidadeResponsavelId, inicio, fim));
            encontradoRecusou = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.ENCONTRADO_RECUSOU, unidadeResponsavelId, inicio, fim));
            naoLocalizado = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.NAO_LOCALIZADO, unidadeResponsavelId, inicio, fim));
            enderecoIncorreto = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.ENDERECO_INCORRETO, unidadeResponsavelId, inicio, fim));
            mudouTerritorio = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.MUDOU_TERRITORIO, unidadeResponsavelId, inicio, fim));
            obito = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OBITO, unidadeResponsavelId, inicio, fim));
            outro = percentual(totalFinalizadas, demandaRepository.countByDesfechoAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(DesfechoDemanda.OUTRO, unidadeResponsavelId, inicio, fim));
        }

        return List.of(
                new IndicadorValorDTO("ENCONTRADO_VINCULADO (%)", arredondar(encontradoVinculado)),
                new IndicadorValorDTO("ENCONTRADO_RECUSOU (%)", arredondar(encontradoRecusou)),
                new IndicadorValorDTO("NAO_LOCALIZADO (%)", arredondar(naoLocalizado)),
                new IndicadorValorDTO("ENDERECO_INCORRETO (%)", arredondar(enderecoIncorreto)),
                new IndicadorValorDTO("MUDOU_TERRITORIO (%)", arredondar(mudouTerritorio)),
                new IndicadorValorDTO("OBITO (%)", arredondar(obito)),
                new IndicadorValorDTO("OUTRO (%)", arredondar(outro))
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