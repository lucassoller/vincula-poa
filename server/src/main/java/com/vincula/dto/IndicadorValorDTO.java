package com.vincula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndicadorValorDTO {

    private String indicador;
    private Double valor;
}