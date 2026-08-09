package com.vincula.dto;

import com.vincula.entity.Endereco;
import lombok.Data;

@Data
public class TerritorioUbsDTO {

    private String nome;
    private String cnes;
    private String distrito;
    private String geom;
    private String telefone;
    private String telefone2;
    private Endereco endereco;

}
