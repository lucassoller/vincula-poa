export const prazoLabel = {
    D1: "1 dia",
    D2: "2 dias",
    D3: "3 dias",
    D7: "7 dias",
    D15: "15 dias",
    D20: "20 dias",
    D30: "30 dias",
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