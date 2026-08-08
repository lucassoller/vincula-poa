package com.vincula.dto.usuario;

import lombok.Data;

@Data
public class AutocompleteUsuarioRequestDTO {

    private String nomeCompleto;
    private Long servicoId;
    private Long servicoSolicitanteId;
}
