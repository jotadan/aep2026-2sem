package br.com.triagemcomunitaria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TriagemTest {
    public static void main(String[] args) {
        Paciente paciente = new Paciente(1L, "Ana", "123", LocalDate.of(2000, 1, 1), "123", "456");
        List<SintomaRelatado> sintomas = new ArrayList<>();
        sintomas.add(new SintomaRelatado("Febre", 7, 12));
        Triagem triagem = new Triagem(1, paciente, LocalDateTime.now(), sintomas, "Observação");
        sintomas.clear();
        if (triagem.sintomas().size() != 1 || triagem.paciente() != paciente) throw new AssertionError();
        try {
            new SintomaRelatado("Febre", 11, 12);
            throw new AssertionError("Intensidade inválida aceita");
        } catch (IllegalArgumentException expected) { }
        try {
            new Triagem(2, paciente, LocalDateTime.now(), List.of(), "");
            throw new AssertionError("Triagem sem sintomas aceita");
        } catch (IllegalArgumentException expected) { }
        System.out.println("Testes de triagem passaram.");
    }
}
