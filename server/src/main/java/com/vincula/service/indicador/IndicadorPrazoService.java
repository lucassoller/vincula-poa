package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import static com.vincula.util.IndicadorUtil.formatarTempo;
import static com.vincula.util.IndicadorUtil.percentual;

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

    public List<IndicadorValorDTO> indicadoresPrazoPorServidor(Long servidorId) {
        long dentroPrazo = demandaRepository.countDentroPrazoPorUnidadeSolicitante(servidorId);
        long abertasAtrasadas = demandaRepository.countAtrasadasPorUnidadeSolicitante(servidorId);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorUnidadeSolicitante(servidorId);

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(demandaRepository.tempoMedioAtrasoPorUnidadeSolicitante(servidorId)))
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

    public List<IndicadorValorDTO> indicadoresPrazoPorServidorEPeriodo(
            Long unidadeId,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        long dentroPrazo = demandaRepository.countDentroPrazoPorUnidadeSolicitanteEPeriodo(unidadeId, inicio, fim);
        long abertasAtrasadas = demandaRepository.countDemandasAtrasadasPorUnidadeSolicitanteEPeriodo(unidadeId, inicio, fim);
        long finalizadasComAtraso = demandaRepository.countFinalizadasAtrasadasPorUnidadeSolicitanteEPeriodo(unidadeId, inicio, fim);

        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;

        return List.of(
                new IndicadorValorDTO("Demandas dentro do prazo", percentual(totalPrazos, dentroPrazo)),
                new IndicadorValorDTO("Demandas atrasadas", percentual(totalPrazos, abertasAtrasadas)),
                new IndicadorValorDTO("Demandas finalizadas com atraso", percentual(totalPrazos, finalizadasComAtraso)),
                new IndicadorValorDTO("Tempo médio de atraso", formatarTempo(
                        demandaRepository.tempoMedioAtrasoEmSegundosPorUnidadeSolicitanteEPeriodo(unidadeId, inicio, fim)
                ))
        );
    }
}