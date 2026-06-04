package com.vincula.dto.usuario;

import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.enums.Sexo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String telefone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;
    private Sexo sexo;
    private String documento;
    private EnderecoResponseDTO endereco;

    private Long idServidorCadastro;
    private Long unidadeSaudeId;
    private String unidadeSaudeNome;
}