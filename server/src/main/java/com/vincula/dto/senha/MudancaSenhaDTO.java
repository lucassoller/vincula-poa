package com.vincula.dto.senha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MudancaSenhaDTO {
    @NotBlank(message = "A senha atual é obrigatória")
    @Size(max = 255, message = "A senha atual deve ter no máximo 255 caracteres")
    String senhaAtual;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(max = 255, message = "A nova senha deve ter no máximo 255 caracteres")
    String novaSenha;

    @NotBlank(message = "A confirmação de senha é obrigatória")
    @Size(max = 255, message = "A confirmação de senha deve ter no máximo 255 caracteres")
    String confirmarSenha;
}
