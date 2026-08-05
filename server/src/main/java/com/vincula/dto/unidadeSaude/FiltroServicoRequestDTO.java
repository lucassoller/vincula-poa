package com.vincula.dto.unidadeSaude;

import com.vincula.enums.TipoServico;
import lombok.Data;
import java.util.List;

@Data
public class FiltroServicoRequestDTO {

    private Long id;
    private String nome;
    private List<TipoServico> tipoServico;
}
