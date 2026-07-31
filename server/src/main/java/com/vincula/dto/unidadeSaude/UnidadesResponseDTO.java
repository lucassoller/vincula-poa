package com.vincula.dto.unidadeSaude;

import java.util.List;

public class UnidadesResponseDTO {

    private List<UnidadeSaudeShortResponseDTO> ubs;
    private List<UnidadeSaudeShortResponseDTO> especializadas;

    public UnidadesResponseDTO(
            List<UnidadeSaudeShortResponseDTO> ubs,
            List<UnidadeSaudeShortResponseDTO> especializadas) {
        this.ubs = ubs;
        this.especializadas = especializadas;
    }

    public List<UnidadeSaudeShortResponseDTO> getUbs() {
        return ubs;
    }

    public List<UnidadeSaudeShortResponseDTO> getEspecializadas() {
        return especializadas;
    }
}