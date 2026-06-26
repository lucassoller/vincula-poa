package com.vincula.enums;

public enum MotivoComplemento {
    ABANDONO_TRATAMENTO,
    AVISO_CONSULTA,
    EGRESSO_HOSPITALAR,
    FALTOSO_CONSULTA,
    FALTOSO_EXAME,
    FALTOSO_PROCEDIMENTO,
    CRIANCA_MENOR,
    DEMAIS_BENEFICIARIOS,
    GESTANTE,
    MULHER_IDADE_FERTIL,
    VACINACAO_CRIANCA_MENOR,
    GESTANTE_EXPOSTA,
    PRE_NATAL,
    PUERPERIO,
    RASTREAMENTO_CANCER_COLO_UTERO,
    RASTREAMENTO_CANCER_MAMA,
    BAIXO_PESO,
    BINOMIO,
    CRIANCA_EXPOSTA,
    DESENVOLVIMENTO_INFANTIL,
    PUERICULTURA,
    TRIAGEM_NEONATAL,
    AVALIACAO_MULTIDIMENSIONAL,
    DECLINIO_COGNITIVO,
    POLIFARMACIA,
    VISITA_DOMICILIAR,
    VACINACAO,
    ADOLESCENTE,
    ADULTO,
    CRIANCA,
    VACINACAO_GESTANTE,
    IDOSO,
    DIABETES,
    DOENCA_FALCIFORME,
    HIPERTENSAO_ARTERIAL,
    OUTROS_AGRAVOS_CRONICOS,
    HANSENIASE,
    HEPATITES_VIRAIS,
    HIV_AIDS,
    OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA,
    SIFILIS,
    TUBERCULOSE_ABANDONO_TRATAMENTO,
    TUBERCULOSE_INVESTIGACAO_CONTATOS,
    MORTALIDADE_INFANTIL,
    MORTALIDADE_MATERNA,
    TRABALHO_INFANTIL,
    VIOLENCIA_CONTRA_CRIANCAS,
    VIOLENCIA_CONTRA_IDOSOS,
    VIOLENCIA_CONTRA_MULHERES;

    public String getDescricao() {
        return switch (this) {
            case ABANDONO_TRATAMENTO -> "Abandono de tratamento";
            case AVISO_CONSULTA -> "Aviso de consulta";
            case EGRESSO_HOSPITALAR -> "Egresso hospitalar";
            case FALTOSO_CONSULTA -> "Faltoso a consulta";
            case FALTOSO_EXAME -> "Faltoso a exame";
            case FALTOSO_PROCEDIMENTO -> "Faltoso a procedimento";
            case CRIANCA_MENOR -> "Criança menor";
            case DEMAIS_BENEFICIARIOS -> "Demais beneficiários";
            case GESTANTE -> "Gestante";
            case MULHER_IDADE_FERTIL -> "Mulher em idade fértil";
            case VACINACAO_CRIANCA_MENOR -> "Vacinação criança menor";
            case GESTANTE_EXPOSTA -> "Gestante exposta";
            case PRE_NATAL -> "Pré-natal";
            case PUERPERIO -> "Puerpério";
            case RASTREAMENTO_CANCER_COLO_UTERO -> "Rastreamento câncer do colo do útero";
            case RASTREAMENTO_CANCER_MAMA -> "Rastreamento câncer de mama";
            case BAIXO_PESO -> "Baixo peso";
            case BINOMIO -> "Binômio";
            case CRIANCA_EXPOSTA -> "Criança exposta";
            case DESENVOLVIMENTO_INFANTIL -> "Desenvolvimento infantil";
            case PUERICULTURA -> "Puericultura";
            case TRIAGEM_NEONATAL -> "Triagem neonatal";
            case AVALIACAO_MULTIDIMENSIONAL -> "Avaliação multidimensional";
            case DECLINIO_COGNITIVO -> "Declínio cognitivo";
            case POLIFARMACIA -> "Polifarmácia";
            case VISITA_DOMICILIAR -> "Visita domiciliar";
            case VACINACAO -> "Vacinação";
            case ADOLESCENTE -> "Adolescente";
            case ADULTO -> "Adulto";
            case CRIANCA -> "Criança";
            case VACINACAO_GESTANTE -> "Vacinação gestante";
            case IDOSO -> "Idoso";
            case DIABETES -> "Diabetes";
            case DOENCA_FALCIFORME -> "Doença falciforme";
            case HIPERTENSAO_ARTERIAL -> "Hipertensão arterial";
            case OUTROS_AGRAVOS_CRONICOS -> "Outros agravos crônicos";
            case HANSENIASE -> "Hanseníase";
            case HEPATITES_VIRAIS -> "Hepatites virais";
            case HIV_AIDS -> "HIV/AIDS";
            case OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA -> "Outras doenças de notificação compulsória";
            case SIFILIS -> "Sífilis";
            case TUBERCULOSE_ABANDONO_TRATAMENTO -> "Tuberculose - abandono de tratamento";
            case TUBERCULOSE_INVESTIGACAO_CONTATOS -> "Tuberculose - investigação de contatos";
            case MORTALIDADE_INFANTIL -> "Mortalidade infantil";
            case MORTALIDADE_MATERNA -> "Mortalidade materna";
            case TRABALHO_INFANTIL -> "Trabalho infantil";
            case VIOLENCIA_CONTRA_CRIANCAS -> "Violência contra crianças";
            case VIOLENCIA_CONTRA_IDOSOS -> "Violência contra idosos";
            case VIOLENCIA_CONTRA_MULHERES -> "Violência contra mulheres";
        };
    }
}