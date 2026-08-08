package com.vincula.dto.auditoria;

import com.vincula.enums.PerfilServidor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FiltroAuditoriaRequestDTO {

    private Long id;
    private String nome;
    private List<PerfilServidor> perfil;
    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;
    private Long servicoId;
}
