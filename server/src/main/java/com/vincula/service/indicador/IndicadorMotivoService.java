package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IndicadorMotivoService {

    private final DemandaRepository demandaRepository;

    public IndicadorMotivoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<MotivoQuantidadeDTO> gerarIndicadores(FiltroIndicadorRequestDTO filtro) {

        return demandaRepository.listarPrincipaisMotivos(
                        filtro.getServicoResponsavelId(),
                        filtro.getServicoSolicitanteId(),
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                )
                .stream()
                .map(item -> new MotivoQuantidadeDTO(
                        traduzirMotivo(item.getMotivo()),
                        item.getQuantidade()
                ))
                .toList();
    }

    private String traduzirMotivo(String motivo) {
        return switch (motivo) {
            case "COORDENACAO_CUIDADO" -> "Coordenação do Cuidado";
            case "BOLSA_FAMILIA" -> "Bolsa Família";
            case "SAUDE_MULHER" -> "Saúde da Mulher";
            case "SAUDE_CRIANCA" -> "Saúde da Criança";
            case "SAUDE_IDOSO" -> "Saúde do Idoso";
            case "VACINACAO" -> "Vacinação";
            case "DOENCA_CRONICA" -> "Doença Crônica";
            case "DOENCA_TRANSMITIVEL" -> "Doença Transmissível";
            case "VIOLENCIA_MORTALIDADE" -> "Violência e Mortalidade";
            case "OUTRO" -> "Outro";

            default -> motivo;
        };
    }
}