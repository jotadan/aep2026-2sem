package br.com.triagemcomunitaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sintoma")
public class Sintoma {
    public static final String FEBRE = "Febre";
    public static final String DIFICULDADE_RESPIRATORIA = "Dificuldade respiratória";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    protected Sintoma() {
    }

    public Sintoma(String nome, String descricao) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O sintoma precisa de nome.");
        }
        this.nome = nome.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
    }

    public void atualizarDescricao(String novaDescricao) {
        this.descricao = novaDescricao == null ? "" : novaDescricao.trim();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
