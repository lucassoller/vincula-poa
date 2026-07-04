package com.vincula.dto.servidor;

import com.vincula.enums.PerfilServidor;
import com.vincula.enums.TipoServico;
import lombok.Data;

@Data
public class ServidorResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private PerfilServidor perfil;
    private Long unidadeSaudeId;
    private String unidadeSaudeNome;
    private TipoServico tipoServico;
    private Boolean ativo;
}