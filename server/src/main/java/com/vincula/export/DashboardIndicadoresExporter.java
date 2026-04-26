package com.vincula.export;

import com.vincula.dto.dashboard.DashboardIndicadoresDTO;
import com.vincula.dto.dashboard.IndicadorRankingDTO;
import com.vincula.dto.dashboard.IndicadorValorDTO;
import com.vincula.dto.dashboard.MotivoQuantidadeDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardIndicadoresExporter {

    public String exportar(DashboardIndicadoresDTO dashboard) {
        StringBuilder sb = new StringBuilder();

        sb.append("categoria,indicador,valor\n");

        adicionarIndicadores(sb, "producao", dashboard.getProducao());
        adicionarIndicadores(sb, "processo", dashboard.getProcesso());
        adicionarIndicadores(sb, "resultado", dashboard.getResultado());
        adicionarMotivos(sb, dashboard.getPrincipaisMotivosInsucesso());

        adicionarRanking(sb, "ranking_total_demandas", dashboard.getRankingTotalDemandas());
        adicionarRanking(sb, "ranking_percentual_resolucao", dashboard.getRankingPercentualResolucao());
        adicionarRanking(sb, "ranking_tempo_medio_resolucao", dashboard.getRankingTempoMedioResolucao());
        adicionarRanking(sb, "ranking_tempo_primeira_tentativa", dashboard.getRankingTempoPrimeiraTentativa());

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