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
    "FALTOSO": "Faltoso",
    "ABANDONO": "Abandono",
    "CONDICAO_SAUDE": "Condição de saúde",
    "OUTRO": "Outro",
};

export const desfechoLabel = {
    "ENCONTRADO_VINCULADO" : "Encontrado e vinculado",
    "ENCONTRADO_RECUSOU": "Encontrado e recuso contato",
    "NAO_LOCALIZADO": "Não localizado",
    "ENDERECO_INCORRETO": "Endereço incorreto",
    "MUDOU_TERRITORIO": "Mudou de território",
    "OBITO": "Óbito",
    "OUTRO": "Outro",
};

export const tentativaContatoLabe = {
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