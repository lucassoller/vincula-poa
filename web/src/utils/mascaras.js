export function somenteNumeros(valor) {
    return valor?.replace(/\D/g, "") || "";
}

export function mascaraTelefone(valor) {
    const numeros = somenteNumeros(valor).slice(0, 11);

    if (numeros.length <= 10) {
        return numeros
            .replace(/(\d{2})(\d)/, "($1) $2")
            .replace(/(\d{4})(\d)/, "$1-$2");
    }

    return numeros
        .replace(/(\d{2})(\d)/, "($1) $2")
        .replace(/(\d{5})(\d)/, "$1-$2");
}

export function mascaraCNES(valor) {
    return somenteNumeros(valor).slice(0, 7);
}

export function mascaraCEP(valor) {
    return somenteNumeros(valor)
        .slice(0, 8)
        .replace(/(\d{5})(\d)/, "$1-$2");
}

export function mascaraDocumento(valor) {
    if(valor.length === 15){
        return somenteNumeros(valor)
            .slice(0, 15)
            .replace(/(\d{3})(\d)/, "$1 $2")
            .replace(/(\d{4})(\d)/, "$1 $2")
            .replace(/(\d{4})(\d)/, "$1 $2");
    }

    const numeros = somenteNumeros(valor).slice(0, 11);

    return numeros
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})$/, "$1-$2");

}