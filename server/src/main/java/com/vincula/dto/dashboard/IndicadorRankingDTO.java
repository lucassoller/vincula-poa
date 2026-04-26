package com.vincula.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicadorRankingDTO {

    private Long unidadeSaudeId;
    private String unidadeSaudeNome;
    private Object valor;
}