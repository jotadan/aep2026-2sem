package br.com.triagemcomunitaria.view;

import br.com.triagemcomunitaria.model.Paciente;
import br.com.triagemcomunitaria.service.PacienteService;
import br.com.triagemcomunitaria.util.Entrada;

import java.time.LocalDate;
import java.util.List;

public class PacienteView {
    private final PacienteService pacienteService;

    public PacienteView(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    public void cadastrar() {
        String nome = Entrada.lerObrigatorio("Nome: ", 150);
        String cpf = Entrada.lerCpf();
        if (pacienteService.cpfJaCadastrado(cpf)) {
            System.out.println("Já existe um paciente cadastrado com este CPF.");
            return;
        }
        LocalDate dataNascimento = Entrada.lerData("Data de nascimento (DD/MM/AAAA): ");
        String telefone = Entrada.lerObrigatorio("Telefone: ", 20);
        String contato = Entrada.lerObrigatorio("Contato de emergência: ", 150);

        Paciente paciente = pacienteService.cadastrar(nome, cpf, dataNascimento, telefone, contato);
        System.out.println("Paciente cadastrado com sucesso! ID: " + paciente.getId());
    }

    public void consultar() {
        Paciente paciente = lerPaciente();
        if (paciente != null) {
            imprimir(paciente);
        }
    }

    public void listar() {
        List<Paciente> pacientes = pacienteService.listar();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
        }
        for (Paciente paciente : pacientes) {
            imprimir(paciente);
            System.out.println("------------------------");
        }
    }

    public void atualizar() {
        Paciente atual = lerPaciente();
        if (atual == null) {
            return;
        }
        System.out.println("(Deixe em branco para manter o valor atual)");
        String nome = Entrada.lerOpcional("Novo nome [" + atual.getNome() + "]: ", atual.getNome());
        String telefone = Entrada.lerOpcional("Novo telefone [" + atual.getTelefone() + "]: ", atual.getTelefone());
        String contato = Entrada.lerOpcional("Novo contato de emergência [" + atual.getContatoEmergencia() + "]: ",
                atual.getContatoEmergencia());
        pacienteService.atualizar(atual.getId(), nome, telefone, contato);
    }

    public void excluir() {
        Paciente paciente = lerPaciente();
        if (paciente == null) {
            return;
        }
        pacienteService.excluir(paciente.getId());
        System.out.println("Paciente excluído com sucesso!");
    }

    public Paciente lerPaciente() {
        Long id = Entrada.lerId("ID do paciente (0 para cancelar): ");
        if (id == null) {
            return null;
        }
        Paciente paciente = pacienteService.buscar(id);
        if (paciente == null) {
            System.out.println("Paciente não encontrado!");
        }
        return paciente;
    }

    private void imprimir(Paciente paciente) {
        System.out.println("ID: " + paciente.getId());
        System.out.println("Nome: " + paciente.getNome());
        System.out.println("Idade: " + paciente.calcularIdade());
        System.out.println("CPF: " + paciente.getCpf());
        System.out.println("Telefone: " + paciente.getTelefone());
        System.out.println("Contato de emergência: " + paciente.getContatoEmergencia());
    }
}
