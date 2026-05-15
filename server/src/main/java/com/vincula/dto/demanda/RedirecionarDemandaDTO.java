package com.vincula.dto.demanda;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedirecionarDemandaDTO {

    @NotNull(message = "Nova unidade responsável é obrigatória")
    private Long novaUnidadeResponsavelId;

    @NotBlank(message = "Motivo do redirecionamento é obrigatório")
    @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
    private String motivoRedirecionamento;
}