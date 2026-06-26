export const prazoLabel = {
    D1: "1 dia",
    D2: "2 dias",
    D3: "3 dias",
    D7: "7 dias",
    D15: "15 dias",
    D20: "20 dias",
    D30: "30 dias",
};

export const statusLabel = {
    "ABERTA": "Aberto",
    "EM_ANDAMENTO": "Em andamento",
    "FINALIZADA": "Finalizado"
};

export const motivoBuscaLabel = {
    "COORDENACAO_CUIDADO": "Coordenação do Cuidado",
    "BOLSA_FAMILIA": "Bolsa Família",
    "SAUDE_MULHER": "Saúde da Mulher",
    "SAUDE_CRIANCA": "Saúde da Criança",
    "SAUDE_IDOSO": "Saúde do Idoso",
    "VACINACAO": "Vacinação",
    "DOENCA_CRONICA": "Doença Crônica",
    "DOENCA_TRANSMITIVEL": "Doença Transmissível",
    "VIOLENCIA_MORTALIDADE": "Violência e Mortalidade",
    "OUTRO": "Outro"
};

export const motivoComplementoLabel = {
    "ABANDONO_TRATAMENTO": "Abandono de tratamento",
    "AVISO_CONSULTA": "Aviso de consulta",
    "EGRESSO_HOSPITALAR": "Egresso hospitalar",
    "FALTOSO_CONSULTA": "Faltoso a consulta",
    "FALTOSO_EXAME": "Faltoso a exame",
    "FALTOSO_PROCEDIMENTO": "Faltoso a procedimento",
    "CRIANCA_MENOR": "Criança menor",
    "DEMAIS_BENEFICIARIOS": "Demais beneficiários",
    "GESTANTE": "Gestante",
    "MULHER_IDADE_FERTIL": "Mulher em idade fértil",
    "VACINACAO_CRIANCA_MENOR": "Vacinação criança menor",
    "GESTANTE_EXPOSTA": "Gestante exposta",
    "PRE_NATAL": "Pré-natal",
    "PUERPERIO": "Puerpério",
    "RASTREAMENTO_CANCER_COLO_UTERO": "Rastreamento câncer do colo do útero",
    "RASTREAMENTO_CANCER_MAMA": "Rastreamento câncer de mama",
    "BAIXO_PESO": "Baixo peso",
    "BINOMIO": "Binômio",
    "CRIANCA_EXPOSTA": "Criança exposta",
    "DESENVOLVIMENTO_INFANTIL": "Desenvolvimento infantil",
    "PUERICULTURA": "Puericultura",
    "TRIAGEM_NEONATAL": "Triagem neonatal",
    "AVALIACAO_MULTIDIMENSIONAL": "Avaliação multidimensional",
    "DECLINIO_COGNITIVO": "Declínio cognitivo",
    "POLIFARMACIA": "Polifarmácia",
    "VISITA_DOMICILIAR": "Visita domiciliar",
    "VACINACAO": "Vacinação",
    "ADOLESCENTE": "Adolescente",
    "ADULTO": "Adulto",
    "CRIANCA": "Criança",
    "VACINACAO_GESTANTE": "Vacinação gestante",
    "IDOSO": "Idoso",
    "DIABETES": "Diabetes",
    "DOENCA_FALCIFORME": "Doença falciforme",
    "HIPERTENSAO_ARTERIAL": "Hipertensão arterial",
    "OUTROS_AGRAVOS_CRONICOS": "Outros agravos crônicos",
    "HANSENIASE": "Hanseníase",
    "HEPATITES_VIRAIS": "Hepatites virais",
    "HIV_AIDS": "HIV/AIDS",
    "OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA": "Outras doenças de notificação compulsória",
    "SIFILIS": "Sífilis",
    "TUBERCULOSE_ABANDONO_TRATAMENTO": "Tuberculose - abandono de tratamento",
    "TUBERCULOSE_INVESTIGACAO_CONTATOS": "Tuberculose - investigação de contatos",
    "MORTALIDADE_INFANTIL": "Mortalidade infantil",
    "MORTALIDADE_MATERNA": "Mortalidade materna",
    "TRABALHO_INFANTIL": "Trabalho infantil",
    "VIOLENCIA_CONTRA_CRIANCAS": "Violência contra crianças",
    "VIOLENCIA_CONTRA_IDOSOS": "Violência contra idosos",
    "VIOLENCIA_CONTRA_MULHERES": "Violência contra mulheres"
}

export const desfechoLabel = {
    "ENCONTRADO_VINCULADO" : "Encontrado e vinculado",
    "ENCONTRADO_RECUSOU": "Encontrado e recuso contato",
    "NAO_LOCALIZADO": "Não localizado",
    "ENDERECO_INCORRETO": "Endereço incorreto",
    "MUDOU_TERRITORIO": "Mudou de território",
    "OBITO": "Óbito",
    "OUTRO": "Outro",
};

export const tentativaContatoLabel = {
    "LIGACAO": "Ligação",
    "VISITA": "Visita domiciliar",
    "WHATSAPP": "WhatsApp",
    "OUTRO": "Outro",
};

export const sexoLabel = {
    "MASCULINO": "Masculino",
    "FEMININO": "Feminino",
    "NAO_INFORMADO": "Não informado",
    "OUTRO": "Outro",
};

export const tipoServico = {
    "UBS": "UBS",
    "OUTRO": "Outro",
};

export const prioridadeLabel = {
    "ALTA": "Alta",
    "MEDIA": "Média",
    "BAIXA": "Baixa"
};

export function formatarDataHora(data) {
    if (!data) return "-";

    const d = new Date(data);

    const dia = String(d.getDate()).padStart(2, "0");
    const mes = String(d.getMonth() + 1).padStart(2, "0");
    const ano = d.getFullYear();

    const hora = String(d.getHours()).padStart(2, "0");
    const minuto = String(d.getMinutes()).padStart(2, "0");

    return `${dia}/${mes}/${ano} ${hora}:${minuto}`;
}

export function formatarEnum(valor) {

    if (!valor) return "-";

    return valor
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, (letra) => letra.toUpperCase());
}

export function formatarValorIndicador(item) {
    const nome = item.indicador.toLowerCase();
    const ehPercentual =
        nome.includes("percentual") ||
        nome.includes("(%)") ||
        nome.includes("dentro do prazo") ||
        nome.includes("atrasadas") ||
        nome.includes("finalizadas com atraso");

    if (ehPercentual && typeof item.valor === "number") {
        return `${item.valor} %`;
    }
    return item.valor;
}

export function diasRestantes(dataCriacao, dataLimite) {
    if (!dataCriacao || !dataLimite) {
        return "-";
    }

    const hoje = new Date();
    const limite = new Date(dataLimite);

    hoje.setHours(0, 0, 0, 0);
    limite.setHours(0, 0, 0, 0);

    const diff = Math.ceil((limite - hoje) / (1000 * 60 * 60 * 24));

    if (diff < 0) {
        return `${Math.abs(diff)} dia(s) atrasado`;
    }

    if (diff === 0) {
        return "Vence hoje";
    }

    return `${diff} dia(s)`;
}