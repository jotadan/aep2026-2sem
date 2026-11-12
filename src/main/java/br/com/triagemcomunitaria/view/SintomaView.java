package br.com.triagemcomunitaria.view;

import br.com.triagemcomunitaria.model.Sintoma;
import br.com.triagemcomunitaria.model.SintomaRelatado;
import br.com.triagemcomunitaria.service.SintomaService;
import br.com.triagemcomunitaria.util.Entrada;

import java.util.ArrayList;
import java.util.List;

public class SintomaView {
    private static final List<String> SINTOMAS_COM_FEBRE = List.of(
            "Mau-estar", "Fadiga", "Dor de cabeça", "Arrepios", "Aumento da frequência cardíaca",
            "Suor frio", "Sudorese", "Perda de apetite", "Vermelhidão no rosto");

    private final SintomaService sintomaService;

    public SintomaView(SintomaService sintomaService) {
        this.sintomaService = sintomaService;
    }

    public List<SintomaRelatado> coletarSintomas() {
        List<SintomaRelatado> sintomas = new ArrayList<>();
        List<Sintoma> categorias = sintomaService.getCategorias();
        int opcao;
        do {
            exibirCategorias(categorias);
            opcao = Entrada.lerInteiro("Opção: ", 0, categorias.size());
            if (opcao != 0) {
                adicionarCategoria(categorias.get(opcao - 1), sintomas);
            }
        } while (opcao != 0);
        return sintomas;
    }

    private void exibirCategorias(List<Sintoma> categorias) {
        System.out.println("\n===== SINTOMAS =====\n");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNome());
        }
        System.out.println("0. Concluir (sem sintomas, cancela a triagem)");
    }

    private void adicionarCategoria(Sintoma sintoma, List<SintomaRelatado> sintomas) {
        if (jaRelatado(sintomas, sintoma)) {
            System.out.println("Este sintoma já foi registrado nesta triagem.");
        } else if (sintoma.getNome().equals(Sintoma.FEBRE)) {
            menuFebre(sintomas);
        } else {
            registrarOcorrencia(sintoma, "", sintomas);
        }
    }

    private void menuFebre(List<SintomaRelatado> sintomas) {
        double temperatura = Entrada.lerTemperatura();
        if (temperatura < 37) {
            System.out.println("Temperatura abaixo de 37 °C: febre não confirmada. Sintoma não registrado.");
            return;
        }
        if (temperatura >= 39) {
            alertarFebreAlta();
        }
        Sintoma febre = sintomaService.buscarPorNome(Sintoma.FEBRE);
        registrarOcorrencia(febre, "Temperatura: " + temperatura + " °C", sintomas);

        int opcao;
        do {
            exibirSintomasComFebre();
            opcao = Entrada.lerInteiro("Opção: ", 0, SINTOMAS_COM_FEBRE.size());
            if (opcao != 0) {
                Sintoma associado = sintomaService.buscarPorNome(SINTOMAS_COM_FEBRE.get(opcao - 1));
                registrarOcorrencia(associado, "", sintomas);
            }
        } while (opcao != 0);
    }

    private void exibirSintomasComFebre() {
        System.out.println("\n===== Sintomas que você está tendo junto com a febre =====");
        for (int i = 0; i < SINTOMAS_COM_FEBRE.size(); i++) {
            System.out.println((i + 1) + ". " + SINTOMAS_COM_FEBRE.get(i));
        }
        System.out.println("0. Nenhum dos anteriores / terminar");
    }

    private void alertarFebreAlta() {
        System.out.println("\n===== ATENÇÃO: FEBRE A PARTIR DE 39 °C =====");
        System.out.println("Procure atendimento médico imediatamente, especialmente com dor intensa,");
        System.out.println("convulsões, confusão mental, vômitos, manchas vermelhas, falta de ar,");
        System.out.println("dificuldade para acordar ou rigidez na nuca.");
    }

    private void registrarOcorrencia(Sintoma sintoma, String observacaoBase, List<SintomaRelatado> sintomas) {
        if (jaRelatado(sintomas, sintoma)) {
            System.out.println("Este sintoma já foi registrado nesta triagem.");
            return;
        }
        System.out.println("\nSintoma: " + sintoma.getNome());
        int intensidade = Entrada.lerInteiro("Intensidade (1 a 10): ", 1, 10);
        int duracao = Entrada.lerInteiro("Duração em horas: ", 0, 8760);
        System.out.print("Observação (opcional): ");
        String observacao = (observacaoBase + " " + Entrada.lerLinha()).trim();

        sintomas.add(new SintomaRelatado(sintoma, intensidade, duracao, observacao));
        System.out.println("Sintoma registrado: " + sintoma.getNome());
    }

    private boolean jaRelatado(List<SintomaRelatado> sintomas, Sintoma sintoma) {
        return sintomas.stream().anyMatch(s -> s.getSintoma().getId().equals(sintoma.getId()));
    }
}
