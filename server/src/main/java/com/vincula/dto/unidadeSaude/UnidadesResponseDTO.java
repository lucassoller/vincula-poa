package com.vincula.dto.unidadeSaude;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UnidadesResponseDTO {
    private List<UnidadeSaudeShortResponseDTO> todos;
    private List<UnidadeSaudeShortResponseDTO> ubs;
    private List<UnidadeSaudeShortResponseDTO> servicos;
    private List<UnidadeSaudeShortResponseDTO> outros;
    private List<UnidadeSaudeShortResponseDTO> especializados;
}