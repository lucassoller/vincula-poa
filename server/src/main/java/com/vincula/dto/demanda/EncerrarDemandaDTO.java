package com.vincula.dto.demanda;

import com.vincula.enums.DesfechoDemanda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EncerrarDemandaDTO {

    @NotNull(message = "O desfecho da demanda é obrigatório")
    private DesfechoDemanda desfechoDemanda;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricaoDesfecho;
}