package com.vincula.dto.servidor;

import com.vincula.enums.PerfilServidor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferirServidorDTO {

    @NotNull(message = "O perfil é obrigatório")
    private PerfilServidor perfil;

    @NotNull(message = "O serviço é obrigatório")
    private Long unidadeSaudeId;
}