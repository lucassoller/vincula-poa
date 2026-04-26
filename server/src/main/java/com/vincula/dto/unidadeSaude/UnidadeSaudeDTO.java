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

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "CNES é obrigatório")
    @Pattern(regexp = "\\d{7}", message = "CNES inválido")
    private String cnes;

    @Pattern(regexp = "\\d{10,11}")
    private String telefone;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;
}