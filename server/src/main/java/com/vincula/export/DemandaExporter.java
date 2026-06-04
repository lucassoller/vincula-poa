package com.vincula.export;

import com.vincula.entity.Demanda;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DemandaExporter {

    private static final DateTimeFormatter FORMATADOR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String exportar(List<Demanda> demandas) {

        StringBuilder sb = new StringBuilder();

        sb.append(
                "id,usuario,motivo_busca,descricao_busca,prazo,status," +
                        "data_criacao,data_limite,data_finalizacao," +
                        "desfecho,descricao_desfecho," +
                        "foi_redirecionada,motivo_redirecionamento," +
                        "unidade_solicitante,unidade_responsavel," +
                        "servidor_criador\n"
        );

        for (Demanda d : demandas) {

            sb.append(d.getId()).append(",")

                    .append(escapar(
                            d.getUsuario() != null
                                    ? d.getUsuario().getNomeCompleto()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(
                            d.getMotivoBuscaAtiva() != null
                                    ? d.getMotivoBuscaAtiva().name()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(d.getDescricaoBusca()))
                    .append(",")

                    .append(escapar(
                            d.getPrazoDemanda() != null
                                    ? d.getPrazoDemanda().name()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(
                            d.getStatus() != null
                                    ? d.getStatus().name()
                                    : ""
                    ))
                    .append(",")

                    .append(formatarData(d.getDataHoraCriacao()))
                    .append(",")

                    .append(formatarData(d.getDataHoraLimite()))
                    .append(",")

                    .append(formatarData(d.getDataHoraFinalizacao()))
                    .append(",")

                    .append(escapar(
                            d.getDesfecho() != null
                                    ? d.getDesfecho().name()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(d.getDescricaoDesfecho()))
                    .append(",")

                    .append(d.getFoiRedirecionada())
                    .append(",")

                    .append(escapar(d.getMotivoRedirecionamento()))
                    .append(",")

                    .append(escapar(
                            d.getUnidadeSolicitante() != null
                                    ? d.getUnidadeSolicitante().getNome()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(
                            d.getUnidadeResponsavel() != null
                                    ? d.getUnidadeResponsavel().getNome()
                                    : ""
                    ))
                    .append(",")

                    .append(escapar(
                            d.getServidorCriador() != null
                                    ? d.getServidorCriador().getNome()
                                    : ""
                    ))
                    .append("\n");
        }

        return "\uFEFF" + sb;
    }

    private String formatarData(java.time.LocalDateTime data) {

        if (data == null) {
            return "null";
        }

        return data.format(FORMATADOR);
    }

    private String escapar(String valor) {

        if (valor == null || valor.isEmpty()) {
            return "null";
        }

        String texto = valor.replace("\"", "\"\"");

        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto + "\"";
        }
        return texto;
    }
}