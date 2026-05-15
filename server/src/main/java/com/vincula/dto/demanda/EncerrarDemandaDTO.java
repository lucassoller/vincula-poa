package com.vincula.dto.demanda;

import com.vincula.enums.DesfechoDemanda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EncerrarDemandaDTO {

    @NotNull(message = "Desfecho da demanda é obrigatório")
    private DesfechoDemanda desfechoDemanda;

    @NotBlank(message = "Descrição do desfecho da demanda é obrigatório")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricaoDesfecho;
}