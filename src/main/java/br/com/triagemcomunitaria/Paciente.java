package br.com.triagemcomunitaria;

import java.time.LocalDate;

public class Paciente extends Pessoa{
    private String contatoEmergencia;

    public Paciente(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String contatoEmergencia) {
        super(id, nome, cpf, dataNascimento, telefone);
        this.contatoEmergencia = contatoEmergencia;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public void atualizarDados(String nome, String telefone, String contatoEmergencia){
        setNome(nome);
        setTelefone(telefone);
        setContatoEmergencia(contatoEmergencia);

        System.out.println("Dados atualizados com sucesso!");
    }


}
