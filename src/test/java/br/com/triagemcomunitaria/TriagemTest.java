package br.com.triagemcomunitaria;

import br.com.triagemcomunitaria.model.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TriagemTest {
    private static int verificacoes = 0;
    private static int bancos = 0;

    public static void main(String[] args) {
        testarModelo();
        testarRegrasDePrioridade();
        testarCrudPacientes();
        testarValidacoesDeEntrada();
        testarTriagemCompleta();
        testarFebreECategorias();
        System.out.println("Testes passaram (" + verificacoes + " verificações).");
    }

    private static void verificar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void esperarErro(Runnable acao, String mensagem) {
        try {
            acao.run();
        } catch (IllegalArgumentException esperado) {
            verificacoes++;
            return;
        }
        throw new AssertionError(mensagem);
    }

    private static String executar(String entrada) {
        Main.reiniciar(new Scanner(entrada), "jdbc:h2:mem:teste" + (++bancos) + ";DB_CLOSE_DELAY=-1");
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void testarModelo() {
        Sintoma febre = new Sintoma("Febre", "Temperatura alta");
        febre.atualizarDescricao("Nova descrição");
        verificar(febre.getDescricao().equals("Nova descrição"), "atualizarDescricao");

        SintomaRelatado a = new SintomaRelatado(febre, 7, 12, "madrugada");
        SintomaRelatado b = new SintomaRelatado(febre, 3, 2, "");
        verificar(a.getSintoma() == b.getSintoma(), "catálogo reutilizado");
        verificar(a.validarIntensidade() && a.getObservacao().equals("madrugada"), "dados do sintoma relatado");
        esperarErro(() -> new SintomaRelatado(febre, 11, 1, ""), "Intensidade 11 aceita");
        esperarErro(() -> new SintomaRelatado(febre, 0, 1, ""), "Intensidade 0 aceita");
        esperarErro(() -> new SintomaRelatado(febre, 5, -1, ""), "Duração negativa aceita");
        esperarErro(() -> new SintomaRelatado(null, 5, 1, ""), "Sintoma nulo aceito");
        esperarErro(() -> a.setIntensidade(99), "Intensidade inválida em setter");
        verificar(a.getIntensidade() == 7, "intensidade preservada após erro");

        Paciente p = new Paciente("Ana", "123", LocalDate.now().minusYears(30), "1", "2");
        verificar(p.calcularIdade() == 30 && !p.isPediatrico(), "calcularIdade");
        Paciente crianca = new Paciente("Leo", "456", LocalDate.now().minusYears(5), "1", "2");
        verificar(crianca.isPediatrico(), "paciente pediátrico");
        p.atualizarDados("Ana Maria", "9", "8");
        verificar(p.getNome().equals("Ana Maria") && p.getTelefone().equals("9"), "atualizarDados");
        verificar(Prioridade.values().length == 3 && NivelAtendimento.values().length == 4, "enums");
    }

    private static Triagem adulto(Sintoma sintoma, int intensidade, int duracao) {
        Paciente p = new Paciente("Ana", "1", LocalDate.of(1990, 1, 1), "1", "2");
        return new TriagemAdulto(p, LocalDateTime.now(), List.of(new SintomaRelatado(sintoma, intensidade, duracao, "")), "");
    }

    private static Triagem pediatrica(Sintoma sintoma, int intensidade, int duracao) {
        Paciente p = new Paciente("Leo", "2", LocalDate.now().minusYears(4), "1", "2");
        return new TriagemPediatrica(p, LocalDateTime.now(), List.of(new SintomaRelatado(sintoma, intensidade, duracao, "")), "");
    }

    private static void testarRegrasDePrioridade() {
        Sintoma dor = new Sintoma("Dor no corpo", "");
        Sintoma febre = new Sintoma(Sintoma.FEBRE, "");
        Sintoma respiracao = new Sintoma(Sintoma.DIFICULDADE_RESPIRATORIA, "");

        verificar(adulto(dor, 3, 2).calcularPrioridade() == Prioridade.VERDE, "adulto verde");
        verificar(adulto(dor, 6, 2).calcularPrioridade() == Prioridade.AMARELO, "adulto amarelo por intensidade");
        verificar(adulto(dor, 4, 80).calcularPrioridade() == Prioridade.AMARELO, "adulto amarelo por duração");
        verificar(adulto(dor, 9, 1).calcularPrioridade() == Prioridade.VERMELHO, "adulto vermelho");
        verificar(adulto(respiracao, 7, 1).calcularPrioridade() == Prioridade.VERMELHO, "adulto respiratório");

        verificar(pediatrica(dor, 5, 2).calcularPrioridade() == Prioridade.AMARELO, "pediátrica mais cautelosa");
        verificar(adulto(dor, 5, 2).calcularPrioridade() == Prioridade.VERDE, "mesmo caso é verde no adulto");
        verificar(pediatrica(febre, 2, 30).calcularPrioridade() == Prioridade.AMARELO, "febre longa em criança");
        verificar(pediatrica(respiracao, 5, 1).calcularPrioridade() == Prioridade.VERMELHO, "respiratório em criança");

        Triagem t = adulto(dor, 3, 2);
        verificar(!t.isConcluida(), "triagem ainda não concluída");
        t.concluir();
        verificar(t.isConcluida() && t.getPrioridade() == Prioridade.VERDE, "triagem concluída");
        verificar(t.getResultados().size() == 1, "um resultado por sintoma");
        verificar(t.getEncaminhamento().getNivelAtendimento() == NivelAtendimento.ORIENTACAO_DOMICILIAR, "nível verde");

        Triagem amarela = adulto(dor, 6, 2);
        amarela.concluir();
        verificar(amarela.getEncaminhamento().getNivelAtendimento() == NivelAtendimento.UBS, "nível amarelo");
        Triagem vermelha = adulto(dor, 9, 2);
        vermelha.concluir();
        verificar(vermelha.getEncaminhamento().getNivelAtendimento() == NivelAtendimento.UPA, "nível vermelho");
        Triagem critica = adulto(dor, 10, 2);
        critica.concluir();
        verificar(critica.getEncaminhamento().getNivelAtendimento() == NivelAtendimento.EMERGENCIA, "nível crítico");
        critica.concluir();
        verificar(critica.getResultados().size() == 1, "reclassificação não duplica resultados");
        verificar(critica.getEncaminhamento().gerarOrientacao().contains("EMERGENCIA"), "gerarOrientacao");
        verificar(critica.getResultados().get(0).gerarResumo().contains("Dor no corpo"), "gerarResumo");

        esperarErro(() -> new TriagemAdulto(new Paciente("A", "1", LocalDate.of(1990, 1, 1), "1", "2"),
                LocalDateTime.now(), List.of(), ""), "Triagem sem sintomas aceita");
    }

    private static String cadastroAna() {
        return String.join("\n", "1", "Ana", "111.111.111-11", "01/01/1990", "99", "Joao") + "\n";
    }

    private static String cadastroCrianca() {
        String nascimento = LocalDate.now().minusYears(5).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        return String.join("\n", "1", "Leo", "22222222222", nascimento, "98", "Maria") + "\n";
    }

    private static void testarCrudPacientes() {
        String saida = executar(String.join("\n",
                "1", "Ana Silva", "111.222.333-44", "10/05/1990", "1199", "Maria",
                "1", "Outro", "11122233344",
                "2", "1",
                "3", "1", "", "5555", "",
                "5",
                "4", "1",
                "2", "1", "0",
                "0", ""));
        verificar(saida.contains("Paciente cadastrado com sucesso! ID: 1"), "cadastro");
        verificar(saida.contains("Já existe um paciente cadastrado com este CPF"), "CPF duplicado");
        verificar(saida.contains("Nome: Ana Silva"), "consulta");
        verificar(saida.contains("CPF: 111.222.333-44"), "CPF formatado");
        verificar(saida.contains("Dados atualizados com sucesso"), "atualização");
        verificar(saida.contains("Telefone: 5555"), "listagem mostra dado atualizado");
        verificar(saida.contains("Paciente excluído com sucesso"), "exclusão");
        int primeira = saida.indexOf("Paciente não encontrado!");
        verificar(primeira >= 0 && saida.indexOf("Paciente não encontrado!", primeira + 1) < 0,
                "mensagem de não encontrado exibida uma vez");
    }

    private static void testarValidacoesDeEntrada() {
        String saida = executar(String.join("\n",
                "abc", "99", "",
                "1", "", "Ana", "123", "11111111111", "31/02/2000", "01/01/2999", "01/01/2000", "", "1", "", "2",
                "2", "xyz", "0",
                "0", ""));
        verificar(saida.contains("Entrada inválida. Digite apenas números inteiros"), "não numérico");
        verificar(saida.contains("Opção inexistente"), "opção fora do menu");
        verificar(saida.contains("Este campo é obrigatório"), "campo obrigatório");
        verificar(saida.contains("CPF inválido"), "CPF inválido");
        verificar(saida.contains("Data inválida"), "data inexistente");
        verificar(saida.contains("Data fora do intervalo"), "data futura");
        verificar(saida.contains("ID inválido"), "ID inválido");
        verificar(saida.contains("Paciente cadastrado com sucesso"), "cadastro após correções");
    }

    private static void testarTriagemCompleta() {
        String saida = executar(cadastroAna() + cadastroCrianca() + String.join("\n",
                "6", "1",
                "1", "abc", "38,5",
                "11", "7", "12", "começou à noite",
                "3", "4", "6", "",
                "0",
                "1",
                "0",
                "teste",
                "6", "2", "0",
                "6", "2", "3", "5", "2", "", "0", "pediátrica",
                "4", "1",
                "7", "1",
                "8", "2",
                "9", "1", "9", "", "", "", "novaobs",
                "10", "1", "n",
                "10", "1", "s",
                "8", "1",
                "4", "1",
                "0", ""));
        verificar(saida.contains("Tipo de triagem: Adulto"), "tipo adulto pela idade");
        verificar(saida.contains("Tipo de triagem: Pediátrica"), "tipo pediátrico pela idade");
        verificar(saida.contains("Opção inexistente"), "intensidade 11 rejeitada");
        verificar(saida.contains("Triagem registrada com sucesso. ID: 1"), "triagem 1");
        verificar(saida.contains("Triagem registrada com sucesso. ID: 2"), "triagem 2");
        verificar(saida.contains("Este sintoma já foi registrado"), "sintoma duplicado");
        verificar(saida.contains("Triagem cancelada"), "cancelamento");
        verificar(saida.contains("Prioridade: AMARELO"), "prioridade calculada");
        verificar(saida.contains("Encaminhamento: Nível de atendimento: UBS"), "encaminhamento UBS");
        verificar(saida.contains("Febre | intensidade: 7/10 | duração: 12 hora(s) | Temperatura: 38.5 °C começou à noite"),
                "febre no histórico");
        verificar(saida.contains("Dor de cabeça | intensidade: 4/10"), "sintoma acompanhante");
        verificar(saida.contains("Triagem 2 (Pediátrica)"), "triagem pediátrica");
        verificar(saida.contains("Dor no corpo | intensidade: 5/10"), "dor no corpo");
        verificar(saida.contains("Não é possível excluir um paciente com triagens"), "proteção de exclusão");
        verificar(saida.contains("Triagem atualizada e reclassificada"), "atualização de triagem");
        verificar(saida.contains("Prioridade: VERMELHO"), "reclassificação");
        verificar(saida.contains("Nível de atendimento: UPA"), "novo encaminhamento");
        verificar(saida.contains("Operação cancelada."), "exclusão de triagem cancelável");
        verificar(saida.contains("Triagem excluída com sucesso"), "exclusão de triagem");
        verificar(saida.contains("Paciente excluído com sucesso"), "exclusão de paciente após remover triagens");
    }

    private static void testarFebreECategorias() {
        String saida = executar(cadastroAna() + String.join("\n",
                "6", "1", "1", "36,5", "0",
                "6", "1", "1", "39,5", "9", "24", "", "0", "0", "alerta",
                "7", "1", "0", ""));
        verificar(saida.contains("febre não confirmada"), "febre abaixo de 37");
        verificar(saida.contains("Nenhum sintoma informado"), "sem sintomas cancela");
        verificar(saida.contains("FEBRE A PARTIR DE 39"), "alerta de febre alta");
        verificar(saida.contains("Temperatura: 39.5 °C"), "temperatura registrada");
        verificar(saida.contains("Prioridade: VERMELHO"), "febre alta e intensa");
        verificar(Main.getCatalogo().size() == 16, "catálogo gravado no banco");

        StringBuilder entrada = new StringBuilder(cadastroAna()).append("6\n1\n");
        for (int i = 2; i <= 9; i++) {
            entrada.append(i).append("\n3\n2\n\n");
        }
        entrada.append("0\nobs\n7\n1\n0\n");
        String todas = executar(entrada.toString());
        for (String nome : new String[]{"Lesões", "Dor no corpo", "Inchaço", "Dificuldade respiratória",
                "Náuseas", "Fadiga", "Tontura", "Mau-estar"}) {
            verificar(todas.contains(nome + " | intensidade: 3/10 | duração: 2 hora(s)"), "categoria " + nome);
        }
        verificar(todas.contains("Prioridade: VERDE"), "tudo leve resulta em verde");
        verificar(executar("6\n").contains("Encerrando sistema"), "fim da entrada tratado");
    }
}
