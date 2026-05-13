package com.vincula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MudancaSenhaDTO {
    @NotBlank(message = "Senha atual é obrigatório")
    @Size(max = 255, message = "Senha atual deve ter no máximo 255 caracteres")
    String senhaAtual;

    @NotBlank(message = "Nova senha é obrigatório")
    @Size(max = 255, message = "Login deve ter no máximo 255 caracteres")
    String novaSenha;

    @NotBlank(message = "Confirmar senha é obrigatório")
    @Size(max = 255, message = "Login deve ter no máximo 255 caracteres")
    String confirmarSenha;
}
