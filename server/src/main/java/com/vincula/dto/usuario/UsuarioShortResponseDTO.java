package com.vincula.dto.usuario;

import lombok.Data;

@Data
public class UsuarioShortResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String documento;
    private String unidadeSaudeNome;
    private Long unidadeSaudeId;
    private Long unidadeSolicitanteId;
    private String unidadeSolicitanteNome;

}