package com.vincula.dto.tentativaContato;

import com.vincula.enums.TipoTentativaContato;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TentativaContatoResponseDTO {

    private Long id;
    private Long demandaId;
    private TipoTentativaContato tipo;
    private String descricao;
    private LocalDateTime dataHora;
    private Long usuarioId;
    private String usuarioNome;
}