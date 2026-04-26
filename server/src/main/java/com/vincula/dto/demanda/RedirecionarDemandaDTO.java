package com.vincula.dto.demanda;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RedirecionarDemandaDTO {

    @NotNull(message = "Nova unidade responsável é obrigatória")
    private Long novaUnidadeResponsavelId;

    @NotBlank(message = "Motivo do redirecionamento é obrigatório")
    private String motivoRedirecionamento;
}