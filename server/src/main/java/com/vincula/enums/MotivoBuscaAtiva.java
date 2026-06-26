package com.vincula.enums;

import java.util.Set;

public enum MotivoBuscaAtiva {
    COORDENACAO_CUIDADO(Set.of(
        MotivoComplemento.ABANDONO_TRATAMENTO,
        MotivoComplemento.AVISO_CONSULTA,
        MotivoComplemento.EGRESSO_HOSPITALAR,
        MotivoComplemento.FALTOSO_CONSULTA,
        MotivoComplemento.FALTOSO_EXAME,
        MotivoComplemento.FALTOSO_PROCEDIMENTO
    )),

    BOLSA_FAMILIA(Set.of(
        MotivoComplemento.CRIANCA_MENOR,
        MotivoComplemento.DEMAIS_BENEFICIARIOS,
        MotivoComplemento.GESTANTE,
        MotivoComplemento.MULHER_IDADE_FERTIL,
        MotivoComplemento.VACINACAO_CRIANCA_MENOR
    )),

    SAUDE_MULHER(Set.of(
        MotivoComplemento.GESTANTE_EXPOSTA,
        MotivoComplemento.PRE_NATAL,
        MotivoComplemento.PUERPERIO,
        MotivoComplemento.RASTREAMENTO_CANCER_COLO_UTERO,
        MotivoComplemento.RASTREAMENTO_CANCER_MAMA
    )),

    SAUDE_CRIANCA(Set.of(
        MotivoComplemento.BAIXO_PESO,
        MotivoComplemento.BINOMIO,
        MotivoComplemento.CRIANCA_EXPOSTA,
        MotivoComplemento.DESENVOLVIMENTO_INFANTIL,
        MotivoComplemento.PUERICULTURA,
        MotivoComplemento.TRIAGEM_NEONATAL
    )),

    SAUDE_IDOSO(Set.of(
        MotivoComplemento.AVALIACAO_MULTIDIMENSIONAL,
        MotivoComplemento.DECLINIO_COGNITIVO,
        MotivoComplemento.POLIFARMACIA,
        MotivoComplemento.VISITA_DOMICILIAR
    )),

    VACINACAO(Set.of(
        MotivoComplemento.VACINACAO,
        MotivoComplemento.ADOLESCENTE,
        MotivoComplemento.ADULTO,
        MotivoComplemento.CRIANCA,
        MotivoComplemento.VACINACAO_GESTANTE,
        MotivoComplemento.IDOSO
    )),

    DOENCA_CRONICA(Set.of(
        MotivoComplemento.DIABETES,
        MotivoComplemento.DOENCA_FALCIFORME,
        MotivoComplemento.HIPERTENSAO_ARTERIAL,
        MotivoComplemento.OUTROS_AGRAVOS_CRONICOS
    )),

    DOENCA_TRANSMITIVEL(Set.of(
        MotivoComplemento.HANSENIASE,
        MotivoComplemento.HEPATITES_VIRAIS,
        MotivoComplemento.HIV_AIDS,
        MotivoComplemento.OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA,
        MotivoComplemento.SIFILIS,
        MotivoComplemento.TUBERCULOSE_ABANDONO_TRATAMENTO,
        MotivoComplemento.TUBERCULOSE_INVESTIGACAO_CONTATOS
    )),

    VIOLENCIA_MORTALIDADE(Set.of(
        MotivoComplemento.MORTALIDADE_INFANTIL,
        MotivoComplemento.MORTALIDADE_MATERNA,
        MotivoComplemento.TRABALHO_INFANTIL,
        MotivoComplemento.VIOLENCIA_CONTRA_CRIANCAS,
        MotivoComplemento.VIOLENCIA_CONTRA_IDOSOS,
        MotivoComplemento.VIOLENCIA_CONTRA_MULHERES
    )),
    
     OUTRO(Set.of());


    private final Set<MotivoComplemento> complementosPermitidos;

    MotivoBuscaAtiva(Set<MotivoComplemento> complementosPermitidos) {
        this.complementosPermitidos = complementosPermitidos;
    }

    public Set<MotivoComplemento> getComplementosPermitidos() {
        return complementosPermitidos;
    }

    public String getDescricao() {
        return switch (this) {
            case COORDENACAO_CUIDADO -> "Coordenação do Cuidado";
            case BOLSA_FAMILIA -> "Bolsa Família";
            case SAUDE_MULHER -> "Saúde da Mulher";
            case SAUDE_CRIANCA -> "Saúde da Criança";
            case SAUDE_IDOSO -> "Saúde do Idoso";
            case VACINACAO -> "Vacinação";
            case DOENCA_CRONICA -> "Doença Crônica";
            case DOENCA_TRANSMITIVEL -> "Doença Transmissível";
            case VIOLENCIA_MORTALIDADE -> "Violência e Mortalidade";
            case OUTRO -> "Outro";
        };
    }
}