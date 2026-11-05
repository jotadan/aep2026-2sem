package br.com.triagemcomunitaria;

import java.time.LocalDateTime;
import java.util.List;

public record Triagem(long id, Paciente paciente, LocalDateTime data, List<SintomaRelatado> sintomas, String observacao) {
    public Triagem {
        if (paciente == null || data == null || sintomas == null || sintomas.isEmpty() || observacao == null) {
            throw new IllegalArgumentException("A triagem precisa de paciente, data e ao menos um sintoma.");
        }
        sintomas = List.copyOf(sintomas);
    }
}
