package com.vincula.dto.indicador;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicadorRankingDTO {

    private Long servicoId;
    private String servicoNome;
    private Object valor;
}