package com.vincula.dto.demanda;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedirecionarDemandaDTO {

    @NotNull(message = "O novo serviço responsável é obrigatório")
    private Long novoServicoResponsavelId;

    @NotBlank(message = "O motivo do redirecionamento é obrigatório")
    @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
    private String motivoRedirecionamento;
}