package com.vincula.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnidadeSaudeRefDTO {

    @NotNull(message = "ID da unidade de saúde é obrigatório")
    private Long id;
}