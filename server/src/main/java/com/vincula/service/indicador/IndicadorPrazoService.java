package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.vincula.util.IndicadorUtil.formatarTempo;
import static com.vincula.util.IndicadorUtil.percentual;

@Service
public class IndicadorPrazoService {

    private final DemandaRepository demandaRepository;

    public IndicadorPrazoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> gerarIndicadores(FiltroIndicadorRequestDTO filtro) {

        long dentroPrazo = demandaRepository.countDemandasDentroDoPrazo(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        long abertasAtrasadas = demandaRepository.countDemandasAtrasadas(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        long finalizadasComAtraso = demandaRepository.countDemandasFinalizadasComAtraso(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );


        double totalPrazos = dentroPrazo + abertasAtrasadas + finalizadasComAtraso;


        return List.of(
                new IndicadorValorDTO(
                        "Demandas dentro do prazo",
                        percentual(totalPrazos, dentroPrazo)
                ),

                new IndicadorValorDTO(
                        "Demandas atrasadas",
                        percentual(totalPrazos, abertasAtrasadas)
                ),

                new IndicadorValorDTO(
                        "Demandas finalizadas com atraso",
                        percentual(totalPrazos, finalizadasComAtraso)
                ),

                new IndicadorValorDTO(
                        "Tempo médio de atraso",
                        formatarTempo(
                                demandaRepository.calcularTempoMedioAtraso(
                                        filtro.getServicoResponsavelId(),
                                        filtro.getServicoSolicitanteId(),
                                        filtro.getDataInicial(),
                                        filtro.getDataFinal()
                                )
                        )
                )
        );
    }
}