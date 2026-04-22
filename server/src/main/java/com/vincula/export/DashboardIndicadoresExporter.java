package com.vincula.export;

import com.vincula.dto.DashboardIndicadoresDTO;
import com.vincula.dto.IndicadorValorDTO;
import com.vincula.dto.MotivoQuantidadeDTO;
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

        return "\uFEFF" + sb.toString();
    }

    private void adicionarIndicadores(StringBuilder sb,
                                      String categoria,
                                      List<IndicadorValorDTO> indicadores) {

        for (IndicadorValorDTO item : indicadores) {
            sb.append(categoria).append(",")
                    .append(escapar(item.getIndicador())).append(",")
                    .append(item.getValor()).append("\n");
        }
    }

    private void adicionarMotivos(StringBuilder sb,
                                  List<MotivoQuantidadeDTO> motivos) {

        for (MotivoQuantidadeDTO item : motivos) {
            sb.append("motivo_insucesso,")
                    .append(escapar(item.getMotivo())).append(",")
                    .append(item.getQuantidade()).append("\n");
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