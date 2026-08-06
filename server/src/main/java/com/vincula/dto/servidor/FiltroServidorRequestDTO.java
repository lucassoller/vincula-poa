package com.vincula.dto.servidor;

import com.vincula.enums.PerfilServidor;
import lombok.Data;
import java.util.List;

@Data
public class FiltroServidorRequestDTO {

    private Long id;
    private String nome;
    private List<PerfilServidor> perfil;
    private Long unidadeSaudeId;
}
