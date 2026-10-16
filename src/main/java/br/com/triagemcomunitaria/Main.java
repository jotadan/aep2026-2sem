package br.com.triagemcomunitaria;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static final List<Paciente> pacientes = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    private static Long proximoId = 0L;

    public static void main(String[] args) {
        int opcao;

        do {
            System.out.println("===== TRIAGEM COMUNITÁRIA =====\n");
            System.out.printf(
                            "1. Cadastrar paciente\n" +
                            "2. Consultar paciente \n" +
                            "3. Atualizar paciente \n" +
                            "4. Excluir paciente \n" +
                            "5. Listar pacientes \n" +
                            "0. Sair\n" +
                            "Opção: "
            );

            opcao =  scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> cadastrarPaciente();
                case 2 -> consultarPaciente();
                case 3 -> atualizarPaciente();
                case 4 -> excluirPaciente();
                case 5 -> listarPaciente();
                case 0 -> System.out.println("Encerrando sistema...");
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 0);

        scanner.close();

    }

    public static void consultarPaciente(){
        System.out.println("ID do Paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(id)) {

                System.out.println("Nome: " + paciente.getNome());
                System.out.println("Idade " + paciente.getIdade());
                System.out.println("CPF: " + paciente.getCpf());
                System.out.println("Telefone: " + paciente.getTelefone());
                System.out.println("Contato de emergência: " + paciente.getContatoEmergencia());
            }
        }

        System.out.println("Paciente não encontrado!");
    }

    public static void cadastrarPaciente() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        System.out.print("Data de nascimento (DD/MM/AAAA): ");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");

        LocalDate dataNascimento = LocalDate.parse(scanner.nextLine(), formato);

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Contato de emergência: ");
        String contatoEmergencia = scanner.nextLine();

        Paciente paciente = new Paciente(proximoId++, nome, cpf, dataNascimento, telefone, contatoEmergencia);

        pacientes.add(paciente);

        System.out.println("Paciente cadastrado com sucesso!");
    }

    public static void listarPaciente(){
        for (Paciente paciente: pacientes){
            System.out.println("ID: " + paciente.getId());
            System.out.println("Nome: " + paciente.getNome());
            System.out.println("Idade: " + paciente.getIdade());
            System.out.println("CPF: " + paciente.getCpf());
            System.out.println("Telefone: " + paciente.getTelefone());
            System.out.println("Contato de Emergência: " + paciente.getContatoEmergencia());
            System.out.println("------------------------");
        }
    }

    public static void atualizarPaciente() {
        System.out.print("ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(id)) {

                System.out.print("Novo nome: ");
                String nome = scanner.nextLine();

                System.out.print("Novo telefone: ");
                String telefone = scanner.nextLine();

                System.out.print("Novo contato de emergência: ");
                String contatoEmergencia = scanner.nextLine();

                paciente.atualizarDados(nome, telefone, contatoEmergencia);

                return;
            } else {
                System.out.println("Paciente não encontrado!");
            }
        }
    }

    public static void excluirPaciente() {
        System.out.print("ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        boolean removido = pacientes.removeIf(paciente -> paciente.getId().equals(id));

        if (removido) {
            System.out.println("Paciente excluído com sucesso!");
        } else {
            System.out.println("Paciente não encontrado!");
        }
    }

}