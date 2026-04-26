package com.vincula.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotivoQuantidadeDTO {
    private String motivo;
    private Long quantidade;
}