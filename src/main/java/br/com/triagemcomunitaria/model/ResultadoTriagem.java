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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "resultado_triagem")
public class ResultadoTriagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "triagem_id", nullable = false)
    private Triagem triagem;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "sinais_associados", columnDefinition = "TEXT")
    private String sinaisAssociados;

    @Column(columnDefinition = "TEXT")
    private String orientacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_atendimento", nullable = false, length = 30)
    private NivelAtendimento nivelAtendimento;

    protected ResultadoTriagem() {
    }

    public ResultadoTriagem(String titulo, String sinaisAssociados,
                            String orientacao, NivelAtendimento nivelAtendimento) {
        if (titulo == null || titulo.isBlank() || nivelAtendimento == null) {
            throw new IllegalArgumentException("O resultado precisa de título e nível de atendimento.");
        }
        this.titulo = titulo;
        this.sinaisAssociados = sinaisAssociados;
        this.orientacao = orientacao;
        this.nivelAtendimento = nivelAtendimento;
    }

    public String gerarResumo() {
        return titulo + " [" + nivelAtendimento + "] - Sinais: " + sinaisAssociados
                + " - Orientação: " + orientacao;
    }

    public Long getId() {
        return id;
    }

    void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinaisAssociados() {
        return sinaisAssociados;
    }

    public String getOrientacao() {
        return orientacao;
    }

    public NivelAtendimento getNivelAtendimento() {
        return nivelAtendimento;
    }
}
