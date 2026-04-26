package com.vincula.dto.paciente;

import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.enums.Sexo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PacienteResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String telefone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;
    private String email;
    private Sexo sexo;
    private String cpf;
    private String cns;
    private EnderecoResponseDTO endereco;

    private Long unidadeSaudeId;
}