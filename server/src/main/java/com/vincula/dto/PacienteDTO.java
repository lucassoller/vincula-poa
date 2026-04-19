package com.vincula.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PacienteDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Size(max = 100, message = "Sobrenome deve ter no máximo 100 caracteres")
    private String sobrenome;

    @Pattern(regexp = "\\d{10,11}")
    private String telefone; // opcional

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "CNS é obrigatório")
    @Pattern(regexp = "\\d{15}", message = "CNS inválido")
    private String cns;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;
}