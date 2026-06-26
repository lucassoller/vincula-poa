package com.vincula.service.indicador;

import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorMotivoService {

    private final DemandaRepository demandaRepository;

    public IndicadorMotivoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<MotivoQuantidadeDTO> principaisMotivos() {
        return demandaRepository.listarPrincipaisMotivos()
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosPorUnidade(Long unidadeSaudeId) {
        return demandaRepository.listarPrincipaisMotivosPorUnidade(unidadeSaudeId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosPorServidor(Long servidorId) {
        return demandaRepository.listarPrincipaisMotivosPorUnidadeSolicitante(servidorId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisMotivosPorPeriodo(inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisMotivosPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisMotivosPorServidorEPeriodo(Long servidorId, LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisMotivosPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirMotivo(item.getMotivo()), item.getQuantidade()))
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