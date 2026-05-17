package com.vincula.export;

import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IndicadorExporter {

    public String exportar(IndicadorDTO indicador) {
        StringBuilder sb = new StringBuilder();

        sb.append("categoria,indicador,valor\n");

        adicionarIndicadores(sb, "producao", indicador.getProducao());
        adicionarIndicadores(sb, "processo", indicador.getProcesso());
        adicionarIndicadores(sb, "resultado", indicador.getResultado());
        adicionarMotivos(sb, indicador.getPrincipaisMotivosInsucesso());
        adicionarIndicadores(sb, "prazos", indicador.getPrazos());

        adicionarRanking(sb, "ranking_total_demandas", indicador.getRankingTotalDemandas());
        adicionarRanking(sb, "ranking_percentual_resolucao", indicador.getRankingPercentualResolucao());
        adicionarRanking(sb, "ranking_tempo_medio_resolucao", indicador.getRankingTempoMedioResolucao());
        adicionarRanking(sb, "ranking_tempo_primeira_tentativa", indicador.getRankingTempoPrimeiraTentativa());

        return "\uFEFF" + sb;
    }

    private void adicionarIndicadores(StringBuilder sb, String categoria, List<IndicadorValorDTO> indicadores) {
        for (IndicadorValorDTO item : indicadores) {
            sb.append(categoria).append(",")
                    .append(escapar(item.getIndicador())).append(",")
                    .append(item.getValor()).append("\n");
        }
    }

    private void adicionarMotivos(StringBuilder sb, List<MotivoQuantidadeDTO> motivos) {
        for (MotivoQuantidadeDTO item : motivos) {
            sb.append("motivo_insucesso,")
                    .append(escapar(item.getMotivo())).append(",")
                    .append(item.getQuantidade()).append("\n");
        }
    }

    private void adicionarRanking(StringBuilder sb, String categoria, List<IndicadorRankingDTO> ranking) {
        for (IndicadorRankingDTO item : ranking) {
            sb.append(categoria).append(",")
                    .append(escapar(item.getUnidadeSaudeNome())).append(",")
                    .append(item.getValor()).append("\n");
        }
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        String texto = valor.replace("\"", "\"\"");

        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto + "\"";
        }

        return texto;
    }
}