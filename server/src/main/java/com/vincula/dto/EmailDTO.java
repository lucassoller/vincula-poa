package com.vincula.dto;

import lombok.Data;

@Data
public class EmailDTO {
    private String para;
    private String assunto;
    private String mensagem;
}