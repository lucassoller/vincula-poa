package com.vincula.dto.demanda;

import com.vincula.enums.DesfechoDemanda;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EncerrarDemandaDTO {

    @NotNull(message = "Desfecho da demanda é obrigatório")
    private DesfechoDemanda desfechoDemanda;

    @NotNull(message = "Descrição do desfecho da demanda é obrigatório")
    private String descricaoDesfecho;
}