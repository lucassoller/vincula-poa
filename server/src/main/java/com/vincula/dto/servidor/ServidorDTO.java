package com.vincula.dto.servidor;

import com.vincula.enums.PerfilServidor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServidorDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 50, message = "O email deve ter no máximo 50 caracteres")
    private String email;

    @NotBlank(message = "O login é obrigatório")
    @Size(max = 50, message = "O login deve ter no máximo 50 caracteres")
    private String login;

    @NotBlank(message = "A senha é obrigatória")
    @Size(max = 255, message = "A senha deve ter no máximo 255 caracteres")
    private String senha;

    @NotBlank(message = "A confirmação de senha é obrigatória")
    @Size(max = 255, message = "A confirmação de senha deve ter no máximo 255 caracteres")
    private String confirmarSenha;

    @NotNull(message = "O perfil é obrigatório")
    private PerfilServidor perfil;

    private Long servicoId;

    private Boolean ativo;
}