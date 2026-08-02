package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.vincula.util.IndicadorUtil.arredondar;
import static com.vincula.util.IndicadorUtil.formatarTempo;

@Service
public class IndicadorProcessoService {

    private final DemandaRepository demandaRepository;
    private final TentativaContatoRepository tentativaContatoRepository;

    public IndicadorProcessoService(DemandaRepository demandaRepository,
                                    TentativaContatoRepository tentativaContatoRepository) {
        this.demandaRepository = demandaRepository;
        this.tentativaContatoRepository = tentativaContatoRepository;
    }

    public List<IndicadorValorDTO> gerarIndicadores(FiltroIndicadorRequestDTO filtro) {
        return List.of(
                percentualDemandasResolvidas(filtro),
                tempoMedioResolucao(filtro),
                tempoMedioAtePrimeiraTentativa(filtro),
                mediaTentativasPorDemanda(filtro),
                mediaTentativasPorServidor(filtro)
        );
    }

    public IndicadorValorDTO percentualDemandasResolvidas(FiltroIndicadorRequestDTO filtro) {

        double total = demandaRepository.countDemandas(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        double finalizadas = demandaRepository.countDemandasFinalizadas(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        double percentual = total == 0 ? 0 : (finalizadas / total) * 100;

        return new IndicadorValorDTO(
                "Percentual de demandas resolvidas",
                arredondar(percentual)
        );
    }

    public IndicadorValorDTO tempoMedioResolucao(FiltroIndicadorRequestDTO filtro) {

        Double media = demandaRepository.calcularTempoMedioResolucao(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        return new IndicadorValorDTO(
                "Tempo médio para resolução da demanda",
                formatarTempo(media)
        );
    }

    public IndicadorValorDTO tempoMedioAtePrimeiraTentativa(FiltroIndicadorRequestDTO filtro) {

        Double media = tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativa(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        return new IndicadorValorDTO(
                "Tempo até a primeira tentativa de contato",
                formatarTempo(media)
        );
    }

    public IndicadorValorDTO mediaTentativasPorDemanda(FiltroIndicadorRequestDTO filtro) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorDemanda(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        return new IndicadorValorDTO(
                "Média de tentativas de contato por demanda",
                arredondar(valor)
        );
    }

    public IndicadorValorDTO mediaTentativasPorServidor(FiltroIndicadorRequestDTO filtro) {
        Double valor = tentativaContatoRepository.calcularMediaTentativasPorServidor(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        return new IndicadorValorDTO(
                "Média de tentativas de contato por servidor",
                arredondar(valor)
        );
    }
}