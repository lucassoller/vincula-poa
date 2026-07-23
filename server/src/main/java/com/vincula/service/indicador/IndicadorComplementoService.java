package com.vincula.service.indicador;

import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.repository.DemandaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndicadorComplementoService {

    private final DemandaRepository demandaRepository;

    public IndicadorComplementoService(DemandaRepository demandaRepository) {
        this.demandaRepository = demandaRepository;
    }

    public List<MotivoQuantidadeDTO> principaisComplementos() {
        return demandaRepository.listarPrincipaisComplementos()
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisComplementosPorUnidade(Long unidadeSaudeId) {
        return demandaRepository.listarPrincipaisComplementosPorUnidade(unidadeSaudeId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisComplementosPorServidor(Long servidorId) {
        return demandaRepository.listarPrincipaisComplementosPorUnidadeSolicitante(servidorId)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisComplementosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisComplementosPorPeriodo(inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisComplementosPorUnidadeEPeriodo(Long unidadeSaudeId, LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisComplementosPorUnidadeEPeriodo(unidadeSaudeId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    public List<MotivoQuantidadeDTO> principaisComplementosPorServidorEPeriodo(Long servidorId, LocalDateTime inicio, LocalDateTime fim) {
        return demandaRepository.listarPrincipaisComplementosPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim)
                .stream()
                .map(item -> new MotivoQuantidadeDTO(traduzirComplemento(item.getMotivo()), item.getQuantidade()))
                .toList();
    }

    private String traduzirComplemento(String complemento) {
        return switch (complemento) {
            case "ABANDONO_TRATAMENTO" -> "Abandono de tratamento";
            case "AVISO_CONSULTA" -> "Aviso de consulta";
            case "EGRESSO_HOSPITALAR" -> "Egresso hospitalar";
            case "FALTOSO_CONSULTA" -> "Faltoso a consulta";
            case "FALTOSO_EXAME" -> "Faltoso a exame";
            case "FALTOSO_PROCEDIMENTO" -> "Faltoso a procedimento";
            case "CRIANCA_MENOR" -> "Criança menor";
            case "DEMAIS_BENEFICIARIOS" -> "Demais beneficiários";
            case "GESTANTE" -> "Gestante";
            case "MULHER_IDADE_FERTIL" -> "Mulher em idade fértil";
            case "VACINACAO_CRIANCA_MENOR" -> "Vacinação criança menor";
            case "GESTANTE_EXPOSTA" -> "Gestante exposta";
            case "PRE_NATAL" -> "Pré-natal";
            case "PUERPERIO" -> "Puerpério";
            case "RASTREAMENTO_CANCER_COLO_UTERO" -> "Rastreamento câncer do colo do útero";
            case "RASTREAMENTO_CANCER_MAMA" -> "Rastreamento câncer de mama";
            case "BAIXO_PESO" -> "Baixo peso";
            case "BINOMIO" -> "Binômio";
            case "CRIANCA_EXPOSTA" -> "Criança exposta";
            case "DESENVOLVIMENTO_INFANTIL" -> "Desenvolvimento infantil";
            case "PUERICULTURA" -> "Puericultura";
            case "TRIAGEM_NEONATAL" -> "Triagem neonatal";
            case "AVALIACAO_MULTIDIMENSIONAL" -> "Avaliação multidimensional";
            case "DECLINIO_COGNITIVO" -> "Declínio cognitivo";
            case "POLIFARMACIA" -> "Polifarmácia";
            case "VISITA_DOMICILIAR" -> "Visita domiciliar";
            case "VACINACAO" -> "Vacinação";
            case "ADOLESCENTE" -> "Adolescente";
            case "ADULTO" -> "Adulto";
            case "CRIANCA" -> "Criança";
            case "VACINACAO_GESTANTE" -> "Vacinação gestante";
            case "IDOSO" -> "Idoso";
            case "DIABETES" -> "Diabetes";
            case "DOENCA_FALCIFORME" -> "Doença falciforme";
            case "HIPERTENSAO_ARTERIAL" -> "Hipertensão arterial";
            case "OUTROS_AGRAVOS_CRONICOS" -> "Outros agravos crônicos";
            case "HANSENIASE" -> "Hanseníase";
            case "HEPATITES_VIRAIS" -> "Hepatites virais";
            case "HIV_AIDS" -> "HIV/AIDS";
            case "OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA" -> "Outras doenças de notificação compulsória";
            case "SIFILIS" -> "Sífilis";
            case "TUBERCULOSE" -> "Tuberculose";
            case "MORTALIDADE_INFANTIL" -> "Mortalidade infantil";
            case "MORTALIDADE_MATERNA" -> "Mortalidade materna";
            case "TRABALHO_INFANTIL" -> "Trabalho infantil";
            case "VIOLENCIA_CONTRA_CRIANCAS" -> "Violência contra crianças";
            case "VIOLENCIA_CONTRA_IDOSOS" -> "Violência contra idosos";
            case "VIOLENCIA_CONTRA_MULHERES" -> "Violência contra mulheres";

            default -> complemento;
        };
    }
}