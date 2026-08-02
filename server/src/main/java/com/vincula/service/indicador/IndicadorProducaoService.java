package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndicadorProducaoService {

    private final DemandaRepository demandaRepository;

    public IndicadorProducaoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> gerarIndicadores(FiltroIndicadorRequestDTO filtro) {
        List<IndicadorValorDTO> lista = new ArrayList<>(
                demandaRepository
                        .agruparPorStatus(
                                filtro.getUnidadeResponsavelId(),
                                filtro.getUnidadeSolicitanteId(),
                                filtro.getDataInicial(),
                                filtro.getDataFinal()
                        )
                        .stream()
                        .map(item -> new IndicadorValorDTO(
                                traduzirStatus(item.getStatus()),
                                item.getQuantidade()))
                        .toList()
        );

        lista.add(new IndicadorValorDTO("Total de demandas", demandaRepository.countDemandas(
                filtro.getUnidadeResponsavelId(),
                filtro.getUnidadeSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal())));

        return lista;
    }

    private String traduzirStatus(String status) {
        return switch (status) {
            case "ABERTA" -> "Demandas abertas";
            case "EM_ANDAMENTO" -> "Demandas em andamento";
            case "FINALIZADA" -> "Demandas finalizadas";
            default -> status;
        };
    }
}