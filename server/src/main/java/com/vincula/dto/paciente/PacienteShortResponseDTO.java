package com.vincula.dto.paciente;

import lombok.Data;

@Data
public class PacienteShortResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String documento;
    private String unidadeSaudeNome;
    private Long unidadeSaudeId;
}