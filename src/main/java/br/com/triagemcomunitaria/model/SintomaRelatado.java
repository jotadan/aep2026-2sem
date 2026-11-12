package br.com.triagemcomunitaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sintoma_relatado")
public class SintomaRelatado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "triagem_id", nullable = false)
    private Triagem triagem;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sintoma_id", nullable = false)
    private Sintoma sintoma;

    @Column(nullable = false)
    private Integer intensidade;

    @Column(name = "duracao_horas", nullable = false)
    private Integer duracaoHoras;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    protected SintomaRelatado() {
    }

    public SintomaRelatado(Sintoma sintoma, Integer intensidade, Integer duracaoHoras, String observacao) {
        if (sintoma == null) {
            throw new IllegalArgumentException("Informe o sintoma.");
        }
        this.sintoma = sintoma;
        setIntensidade(intensidade);
        setDuracaoHoras(duracaoHoras);
        this.observacao = observacao;
    }

    public boolean validarIntensidade() {
        return intensidade != null && intensidade >= 1 && intensidade <= 10;
    }

    public Long getId() {
        return id;
    }

    void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public Sintoma getSintoma() {
        return sintoma;
    }

    public Integer getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(Integer intensidade) {
        if (intensidade == null || intensidade < 1 || intensidade > 10) {
            throw new IllegalArgumentException("A intensidade deve ser de 1 a 10.");
        }
        this.intensidade = intensidade;
    }

    public Integer getDuracaoHoras() {
        return duracaoHoras;
    }

    public void setDuracaoHoras(Integer duracaoHoras) {
        if (duracaoHoras == null || duracaoHoras < 0) {
            throw new IllegalArgumentException("A duração não pode ser negativa.");
        }
        this.duracaoHoras = duracaoHoras;
    }

    public String getObservacao() {
        return observacao;
    }
}
