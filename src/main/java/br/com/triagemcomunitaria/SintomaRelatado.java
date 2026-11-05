package br.com.triagemcomunitaria;

public record SintomaRelatado(String nome, int intensidade, int duracaoHoras) {
    public SintomaRelatado {
        if (nome == null || nome.isBlank() || intensidade < 1 || intensidade > 10 || duracaoHoras < 0) {
            throw new IllegalArgumentException("Informe sintoma, intensidade de 1 a 10 e duração não negativa.");
        }
        nome = nome.trim();
    }
}
