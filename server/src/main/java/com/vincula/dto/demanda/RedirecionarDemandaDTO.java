package com.vincula.dto.demanda;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedirecionarDemandaDTO {

    @NotNull(message = "A nova unidade responsável é obrigatória")
    private Long novaUnidadeResponsavelId;

    @NotBlank(message = "O motivo do redirecionamento é obrigatório")
    @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
    private String motivoRedirecionamento;
}