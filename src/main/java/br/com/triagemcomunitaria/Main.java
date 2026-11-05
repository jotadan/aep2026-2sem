package br.com.triagemcomunitaria;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static final List<Paciente> pacientes = new ArrayList<>();
    private static final List<Triagem> triagens = new ArrayList<>();
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
                            "6. Registrar triagem\n" +
                            "7. Histórico de triagens do paciente\n" +
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
                case 6 -> registrarTriagem();
                case 7 -> historicoTriagens();
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

        if (triagens.stream().anyMatch(triagem -> triagem.paciente().getId().equals(id))) {
            System.out.println("Não é possível excluir um paciente com triagens registradas.");
            return;
        }
        boolean removido = pacientes.removeIf(paciente -> paciente.getId().equals(id));

        if (removido) {
            System.out.println("Paciente excluído com sucesso!");
        } else {
            System.out.println("Paciente não encontrado!");
        }
    }

    public static void registrarTriagem() {
        System.out.print("ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Paciente paciente = pacientes.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
        if (paciente == null) {
            System.out.println("Paciente não encontrado!");
            return;
        }

        List<SintomaRelatado> sintomas = new ArrayList<>();
        while (true) {
            System.out.print("Sintoma: ");
            String nome = scanner.nextLine();
            System.out.print("Intensidade (1 a 10): ");
            String intensidade = scanner.nextLine();
            System.out.print("Duração em horas: ");
            String duracao = scanner.nextLine();
            try {
                sintomas.add(new SintomaRelatado(nome, Integer.parseInt(intensidade), Integer.parseInt(duracao)));
            } catch (IllegalArgumentException e) {
                System.out.println("Dados inválidos. Informe sintoma, intensidade de 1 a 10 e duração em horas não negativa.");
                continue;
            }
            System.out.print("Adicionar outro sintoma? (s/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) break;
        }
        System.out.print("Observações adicionais: ");
        String observacao = scanner.nextLine();
        Triagem triagem = new Triagem(triagens.size() + 1L, paciente, java.time.LocalDateTime.now(), sintomas, observacao);
        triagens.add(triagem);
        System.out.println("Triagem registrada com sucesso. ID: " + triagem.id());
    }

    public static void historicoTriagens() {
        System.out.print("ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        if (pacientes.stream().noneMatch(p -> p.getId().equals(id))) {
            System.out.println("Paciente não encontrado!");
            return;
        }
        boolean encontrou = false;
        for (Triagem triagem : triagens) {
            if (!triagem.paciente().getId().equals(id)) continue;
            encontrou = true;
            System.out.println("Triagem " + triagem.id() + " - " + triagem.data().format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")));
            for (SintomaRelatado sintoma : triagem.sintomas()) {
                System.out.println(sintoma.nome() + " | intensidade: " + sintoma.intensidade() + "/10 | duração: " + sintoma.duracaoHoras() + " hora(s)");
            }
            System.out.println("Observações: " + triagem.observacao());
        }
        if (!encontrou) System.out.println("Nenhuma triagem registrada para este paciente.");
    }

}
