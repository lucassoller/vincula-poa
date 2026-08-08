package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.projection.DesfechoQuantidadeProjection;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

import static com.vincula.util.IndicadorUtil.percentual;

@Service
public class IndicadorResultadoService {

    private final DemandaRepository demandaRepository;

    public IndicadorResultadoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<IndicadorValorDTO> gerarIndicadores(FiltroIndicadorRequestDTO filtro) {

        Long servicoResponsavelId = filtro.getServicoResponsavelId();
        Long servicoSolicitanteId = filtro.getServicoSolicitanteId();
        LocalDate inicio = filtro.getDataInicial();
        LocalDate fim = filtro.getDataFinal();

        Long totalFinalizadas =
                 demandaRepository.countDemandasFinalizadas(
                        servicoResponsavelId,
                        servicoSolicitanteId,
                        inicio,
                        fim
                );

        return demandaRepository.agruparPorDesfecho(
                        servicoResponsavelId,
                        servicoSolicitanteId,
                        inicio,
                        fim
                )
                .stream()
                .map(item -> toIndicador(item, totalFinalizadas))
                .toList();
    }


    private IndicadorValorDTO toIndicador(
            DesfechoQuantidadeProjection item,
            double totalFinalizadas
    ) {
        return new IndicadorValorDTO(
                traduzirDesfecho(item.getDesfecho()),
                percentual(totalFinalizadas, item.getQuantidade())
        );
    }


    private String traduzirDesfecho(String desfecho) {
        return switch (desfecho) {
            case "ENCONTRADO_VINCULADO" -> "Encontrado e vinculado à APS";
            case "ENCONTRADO_RECUSOU" -> "Encontrado e recusou atendimento";
            case "NAO_LOCALIZADO" -> "Não localizado";
            case "ENDERECO_INCORRETO" -> "Endereço incorreto";
            case "MUDOU_TERRITORIO" -> "Mudou de território";
            case "OBITO" -> "Óbito";
            case "OUTRO" -> "Outro";
            default -> desfecho;
        };
    }
}