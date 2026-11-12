package br.com.triagemcomunitaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("ADULTO")
public class TriagemAdulto extends Triagem {

    protected TriagemAdulto() {
    }

    public TriagemAdulto(Paciente paciente, LocalDateTime dataHora,
                         List<SintomaRelatado> sintomas, String observacao) {
        super(paciente, dataHora, sintomas, observacao);
    }

    @Override
    protected Prioridade classificar(SintomaRelatado sintoma) {
        int intensidade = sintoma.getIntensidade();
        int duracao = sintoma.getDuracaoHoras();
        if (intensidade >= 9 || (ehDificuldadeRespiratoria(sintoma) && intensidade >= 7)) {
            return Prioridade.VERMELHO;
        }
        if (intensidade >= 6 || (intensidade >= 4 && duracao > 72)) {
            return Prioridade.AMARELO;
        }
        return Prioridade.VERDE;
    }

    @Override
    public Prioridade calcularPrioridade() {
        return maiorPrioridade();
    }
}
