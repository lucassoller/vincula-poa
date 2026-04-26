package com.vincula.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndicadorValorDTO {

    private String indicador;
    private Object valor;
}