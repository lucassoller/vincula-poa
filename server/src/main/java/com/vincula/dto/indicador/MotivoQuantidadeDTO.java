package com.vincula.dto.indicador;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotivoQuantidadeDTO {
    private String motivo;
    private Long quantidade;
}