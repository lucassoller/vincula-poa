package com.vincula.dto.indicador;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndicadorValorDTO {

    private String indicador;
    private Object valor;
}