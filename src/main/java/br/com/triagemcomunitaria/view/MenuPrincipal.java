package br.com.triagemcomunitaria.view;

import br.com.triagemcomunitaria.service.PacienteService;
import br.com.triagemcomunitaria.service.SintomaService;
import br.com.triagemcomunitaria.service.TriagemService;
import br.com.triagemcomunitaria.util.Entrada;
import jakarta.persistence.PersistenceException;

public class MenuPrincipal {
    private final PacienteView pacienteView;
    private final TriagemView triagemView;

    public MenuPrincipal(SintomaService sintomaService) {
        this.pacienteView = new PacienteView(new PacienteService());
        SintomaView sintomaView = new SintomaView(sintomaService);
        this.triagemView = new TriagemView(new TriagemService(), pacienteView, sintomaView);
    }

    public void executar() {
        int opcao;
        do {
            exibir();
            opcao = Entrada.lerInteiro("Opção: ", 0, 10);
            try {
                tratarOpcao(opcao);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            } catch (PersistenceException e) {
                System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void tratarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> pacienteView.cadastrar();
            case 2 -> pacienteView.consultar();
            case 3 -> pacienteView.atualizar();
            case 4 -> pacienteView.excluir();
            case 5 -> pacienteView.listar();
            case 6 -> triagemView.registrar();
            case 7 -> triagemView.historico();
            case 8 -> triagemView.consultar();
            case 9 -> triagemView.atualizar();
            case 10 -> triagemView.excluir();
            case 0 -> System.out.println("Encerrando sistema...");
        }
    }

    private void exibir() {
        System.out.println("""

                ===== TRIAGEM COMUNITÁRIA =====

                1. Cadastrar paciente
                2. Consultar paciente
                3. Atualizar paciente
                4. Excluir paciente
                5. Listar pacientes
                6. Registrar triagem
                7. Histórico de triagens do paciente
                8. Consultar triagem
                9. Atualizar triagem
                10. Excluir triagem
                0. Sair""");
    }
}
