package com.vincula.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class IndicadorUtil {

    private IndicadorUtil() {
        throw new UnsupportedOperationException(
                "Classe utilitária não pode ser instanciada."
        );
    }

    public static double percentual(double total, double valor) {
        if (total == 0) {
            return 0.0;
        }

        return arredondar(valor * 100.0 / total);
    }

    public static double arredondar(Double valor) {
        if(valor == null){
            return 0.0;
        }
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static String formatarTempo(Double totalSegundos) {
        if (totalSegundos == null || totalSegundos <= 0) {
            return "0d 0h 0m";
        }

        long segundosTotais = Math.round(totalSegundos);

        long dias = segundosTotais / 86400;
        long horas = (segundosTotais % 86400) / 3600;
        long minutos = (segundosTotais % 3600) / 60;

        return dias + "d " + horas + "h " + minutos + "m";
    }
}