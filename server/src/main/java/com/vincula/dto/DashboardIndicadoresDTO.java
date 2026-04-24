package com.vincula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardIndicadoresDTO {

    private List<IndicadorValorDTO> producao;
    private List<IndicadorValorDTO> processo;
    private List<IndicadorValorDTO> resultado;
    private List<MotivoQuantidadeDTO> principaisMotivosInsucesso;
    private List<IndicadorRankingDTO> rankingTotalDemandas;
    private List<IndicadorRankingDTO> rankingPercentualResolucao;
    private List<IndicadorRankingDTO> rankingTempoMedioResolucao;
    private List<IndicadorRankingDTO> rankingTempoPrimeiraTentativa;
}