package br.com.triagemcomunitaria.service;

import br.com.triagemcomunitaria.dao.PacienteDAO;
import br.com.triagemcomunitaria.dao.TriagemDAO;
import br.com.triagemcomunitaria.model.Paciente;

import java.time.LocalDate;
import java.util.List;

public class PacienteService {
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final TriagemDAO triagemDAO = new TriagemDAO();

    public boolean cpfJaCadastrado(String cpf) {
        return pacienteDAO.existeCpf(cpf);
    }

    public Paciente cadastrar(String nome, String cpf, LocalDate dataNascimento,
                              String telefone, String contatoEmergencia) {
        if (nome == null || nome.isBlank() || telefone == null || telefone.isBlank()
                || contatoEmergencia == null || contatoEmergencia.isBlank()) {
            throw new IllegalArgumentException("Nome, telefone e contato de emergência são obrigatórios.");
        }
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }
        if (cpfJaCadastrado(cpf)) {
            throw new IllegalArgumentException("Já existe um paciente cadastrado com este CPF.");
        }
        Paciente paciente = new Paciente(nome, cpf, dataNascimento, telefone, contatoEmergencia);
        pacienteDAO.salvar(paciente);
        return paciente;
    }

    public Paciente buscar(Long id) {
        return pacienteDAO.buscarPorId(id);
    }

    public List<Paciente> listar() {
        return pacienteDAO.listar();
    }

    public void atualizar(Long id, String nome, String telefone, String contatoEmergencia) {
        pacienteDAO.atualizar(id, nome, telefone, contatoEmergencia);
    }

    public void excluir(Long id) {
        if (triagemDAO.contarPorPaciente(id) > 0) {
            throw new IllegalStateException("Não é possível excluir um paciente com triagens registradas.");
        }
        pacienteDAO.excluir(id);
    }
}
