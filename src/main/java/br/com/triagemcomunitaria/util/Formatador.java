package br.com.triagemcomunitaria.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Formatador {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    public static LocalDate lerData(String texto) {
        return LocalDate.parse(texto, FORMATO_DATA);
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora.format(FORMATO_DATA_HORA);
    }

    public static String formatarCpf(String digitos) {
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    public static String soDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }
}
