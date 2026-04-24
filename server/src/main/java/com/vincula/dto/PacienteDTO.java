package com.vincula.dto;

import com.vincula.enums.Sexo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PacienteDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nomeCompleto;

    @Pattern(regexp = "\\d{10,11}")
    private String telefone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @Size(max = 50, message = "Email deve ter no máximo 50 caracteres")
    private String email;

    private Sexo sexo;

    @Pattern(regexp = "\\d{11}", message = "CPF inválido")
    private String cpf;

    @Pattern(regexp = "\\d{15}", message = "CNS inválido")
    private String cns;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;

    @NotNull(message = "Unidade de saúde é obrigatória")
    private Long unidadeSaudeId;
}