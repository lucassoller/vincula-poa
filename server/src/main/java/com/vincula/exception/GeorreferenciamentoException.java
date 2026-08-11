package com.vincula.exception;

import com.vincula.enums.CodigoErro;
import lombok.Getter;

@Getter
public class GeorreferenciamentoException extends RuntimeException {

    private final CodigoErro codigo;

    public GeorreferenciamentoException(
            CodigoErro codigo,
            String mensagem
    ) {
        super(mensagem);
        this.codigo = codigo;
    }
}