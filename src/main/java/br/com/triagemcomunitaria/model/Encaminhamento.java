package br.com.triagemcomunitaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "encaminhamento")
public class Encaminhamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "triagem_id", nullable = false, unique = true)
    private Triagem triagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_atendimento", nullable = false, length = 30)
    private NivelAtendimento nivelAtendimento;

    @Column(columnDefinition = "TEXT")
    private String orientacao;

    protected Encaminhamento() {
    }

    public Encaminhamento(NivelAtendimento nivelAtendimento, String orientacao) {
        if (nivelAtendimento == null) {
            throw new IllegalArgumentException("O encaminhamento precisa de um nível de atendimento.");
        }
        this.nivelAtendimento = nivelAtendimento;
        this.orientacao = orientacao;
    }

    public String gerarOrientacao() {
        return "Nível de atendimento: " + nivelAtendimento + ". " + orientacao;
    }

    void atualizar(NivelAtendimento nivelAtendimento, String orientacao) {
        this.nivelAtendimento = nivelAtendimento;
        this.orientacao = orientacao;
    }

    public Long getId() {
        return id;
    }

    void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public NivelAtendimento getNivelAtendimento() {
        return nivelAtendimento;
    }

    public String getOrientacao() {
        return orientacao;
    }
}
