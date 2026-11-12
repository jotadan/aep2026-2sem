package br.com.triagemcomunitaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "paciente")
public class Paciente extends Pessoa {
    public static final int IDADE_LIMITE_PEDIATRICO = 18;

    @Column(name = "contato_emergencia", nullable = false, length = 150)
    private String contatoEmergencia;

    protected Paciente() {
    }

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String telefone, String contatoEmergencia) {
        super(nome, cpf, dataNascimento, telefone);
        this.contatoEmergencia = contatoEmergencia;
    }

    public boolean isPediatrico() {
        return calcularIdade() < IDADE_LIMITE_PEDIATRICO;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public void atualizarDados(String nome, String telefone, String contatoEmergencia) {
        setNome(nome);
        setTelefone(telefone);
        setContatoEmergencia(contatoEmergencia);
        System.out.println("Dados atualizados com sucesso!");
    }
}
