package com.vincula.dto.tentativaContato;

import com.vincula.enums.TipoTentativaContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TentativaContatoDTO {

    @NotNull(message = "Demanda é obrigatória")
    private Long demandaId;

    @NotNull(message = "Tipo é obrigatório")
    private TipoTentativaContato tipo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
}