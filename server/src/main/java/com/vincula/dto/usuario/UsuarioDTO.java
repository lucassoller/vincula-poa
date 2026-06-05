package com.vincula.dto.usuario;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.enums.Sexo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UsuarioDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nomeCompleto;

    @Pattern(regexp = "^$|\\d{10,11}", message = "Telefone inválido")
    private String telefone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    private Sexo sexo;
    private Long idServidorCadastro;

    @NotBlank(message = "O CPF ou CNS é obrigatório")
    @Pattern(regexp = "\\d{11,15}", message = "CPF ou CNS inválido")
    private String documento;

    @NotNull(message = "O endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;
}