package br.com.triagemcomunitaria.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "triagem")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 20)
public abstract class Triagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Prioridade prioridade;

    @OneToMany(mappedBy = "triagem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<SintomaRelatado> sintomas = new ArrayList<>();

    @OneToMany(mappedBy = "triagem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ResultadoTriagem> resultados = new ArrayList<>();

    @OneToOne(mappedBy = "triagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Encaminhamento encaminhamento;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    protected Triagem() {
    }

    protected Triagem(Paciente paciente, LocalDateTime dataHora, List<SintomaRelatado> sintomas, String observacao) {
        if (paciente == null || sintomas == null || sintomas.isEmpty()) {
            throw new IllegalArgumentException("A triagem precisa de paciente e de pelo menos um sintoma.");
        }
        this.paciente = paciente;
        this.dataHora = dataHora;
        for (SintomaRelatado s : sintomas) {
            s.setTriagem(this);
            this.sintomas.add(s);
        }
        this.observacao = observacao;
    }

    protected abstract Prioridade classificar(SintomaRelatado sintoma);

    public abstract Prioridade calcularPrioridade();

    protected Prioridade maiorPrioridade() {
        return sintomas.stream()
                .map(this::classificar)
                .max(Comparator.naturalOrder())
                .orElse(Prioridade.VERDE);
    }

    protected boolean ehDificuldadeRespiratoria(SintomaRelatado s) {
        return s.getSintoma().getNome().equals(Sintoma.DIFICULDADE_RESPIRATORIA);
    }

    protected boolean ehFebre(SintomaRelatado s) {
        return s.getSintoma().getNome().equals(Sintoma.FEBRE);
    }

    private boolean ehGrave(SintomaRelatado s) {
        if (s.getIntensidade() == 10) {
            return true;
        }
        return ehDificuldadeRespiratoria(s) && classificar(s) == Prioridade.VERMELHO;
    }

    private NivelAtendimento escolherNivel(Prioridade prioridade, boolean grave) {
        return switch (prioridade) {
            case VERDE -> NivelAtendimento.ORIENTACAO_DOMICILIAR;
            case AMARELO -> NivelAtendimento.UBS;
            case VERMELHO -> grave ? NivelAtendimento.EMERGENCIA : NivelAtendimento.UPA;
        };
    }

    public Encaminhamento gerarEncaminhamento() {
        boolean grave = sintomas.stream().anyMatch(this::ehGrave);
        NivelAtendimento nivel = escolherNivel(calcularPrioridade(), grave);
        return new Encaminhamento(nivel, nivel.getOrientacao());
    }

    public void concluir() {
        prioridade = calcularPrioridade();

        resultados.clear();
        for (SintomaRelatado s : sintomas) {
            NivelAtendimento nivel = escolherNivel(classificar(s), ehGrave(s));
            String sinais = "Intensidade " + s.getIntensidade() + "/10, duração de "
                    + s.getDuracaoHoras() + " hora(s)";
            if (s.getObservacao() != null && !s.getObservacao().isEmpty()) {
                sinais = sinais + ", " + s.getObservacao();
            }
            ResultadoTriagem resultado = new ResultadoTriagem(s.getSintoma().getNome(), sinais,
                    nivel.getOrientacao(), nivel);
            resultado.setTriagem(this);
            resultados.add(resultado);
        }

        Encaminhamento novo = gerarEncaminhamento();
        if (encaminhamento == null) {
            novo.setTriagem(this);
            encaminhamento = novo;
        } else {
            encaminhamento.atualizar(novo.getNivelAtendimento(), novo.getOrientacao());
        }
    }

    public boolean isConcluida() {
        return prioridade != null && !resultados.isEmpty() && encaminhamento != null;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public List<SintomaRelatado> getSintomas() {
        return sintomas;
    }

    public List<ResultadoTriagem> getResultados() {
        return resultados;
    }

    public Encaminhamento getEncaminhamento() {
        return encaminhamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
