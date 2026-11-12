package br.com.triagemcomunitaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("PEDIATRICA")
public class TriagemPediatrica extends Triagem {

    protected TriagemPediatrica() {
    }

    public TriagemPediatrica(Paciente paciente, LocalDateTime dataHora,
                             List<SintomaRelatado> sintomas, String observacao) {
        super(paciente, dataHora, sintomas, observacao);
    }

    @Override
    protected Prioridade classificar(SintomaRelatado sintoma) {
        int intensidade = sintoma.getIntensidade();
        int duracao = sintoma.getDuracaoHoras();
        if (intensidade >= 8 || (ehDificuldadeRespiratoria(sintoma) && intensidade >= 5)) {
            return Prioridade.VERMELHO;
        }
        if (intensidade >= 5 || (intensidade >= 3 && duracao > 48) || (ehFebre(sintoma) && duracao > 24)) {
            return Prioridade.AMARELO;
        }
        return Prioridade.VERDE;
    }

    @Override
    public Prioridade calcularPrioridade() {
        return maiorPrioridade();
    }
}
