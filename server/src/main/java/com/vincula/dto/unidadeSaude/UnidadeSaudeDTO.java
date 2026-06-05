package com.vincula.dto.unidadeSaude;

import com.vincula.dto.endereco.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnidadeSaudeDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O CNES é obrigatório")
    @Pattern(regexp = "\\d{7}", message = "CNES inválido")
    private String cnes;

    @Pattern(regexp = "^$|\\d{10,11}", message = "Telefone inválido")
    private String telefone;

    @Pattern(regexp = "^$|\\d{10,11}", message = "Telefone inválido")
    private String telefone2;

    @NotNull(message = "O endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;
}