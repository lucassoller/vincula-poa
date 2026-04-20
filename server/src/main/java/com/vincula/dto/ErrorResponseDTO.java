package com.vincula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private String message;
    private Map<String, String> errors;
    private String path;
}