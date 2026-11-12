package br.com.triagemcomunitaria.view;

import br.com.triagemcomunitaria.model.Paciente;
import br.com.triagemcomunitaria.model.ResultadoTriagem;
import br.com.triagemcomunitaria.model.SintomaRelatado;
import br.com.triagemcomunitaria.model.Triagem;
import br.com.triagemcomunitaria.model.TriagemAdulto;
import br.com.triagemcomunitaria.service.TriagemService;
import br.com.triagemcomunitaria.util.Entrada;
import br.com.triagemcomunitaria.util.Formatador;

import java.util.List;

public class TriagemView {
    private final TriagemService triagemService;
    private final PacienteView pacienteView;
    private final SintomaView sintomaView;

    public TriagemView(TriagemService triagemService, PacienteView pacienteView, SintomaView sintomaView) {
        this.triagemService = triagemService;
        this.pacienteView = pacienteView;
        this.sintomaView = sintomaView;
    }

    public void registrar() {
        Paciente paciente = pacienteView.lerPaciente();
        if (paciente == null) {
            return;
        }
        System.out.println("\n===== REGISTRAR TRIAGEM =====");
        System.out.println("Paciente: " + paciente.getNome() + " (" + paciente.calcularIdade() + " anos)");
        System.out.println("Tipo de triagem: " + (paciente.isPediatrico() ? "Pediátrica" : "Adulto")
                + " (definido pela idade do cadastro)");

        List<SintomaRelatado> sintomas = sintomaView.coletarSintomas();
        if (sintomas.isEmpty()) {
            System.out.println("Nenhum sintoma informado. Triagem cancelada.");
            return;
        }
        System.out.print("Observações adicionais: ");
        String observacao = Entrada.lerLinha();

        Triagem triagem = triagemService.registrar(paciente, sintomas, observacao);
        System.out.println("Triagem registrada com sucesso. ID: " + triagem.getId());
        imprimir(triagem);
    }

    public void historico() {
        Paciente paciente = pacienteView.lerPaciente();
        if (paciente == null) {
            return;
        }
        List<Triagem> triagens = triagemService.historico(paciente.getId());
        if (triagens.isEmpty()) {
            System.out.println("Nenhuma triagem registrada para este paciente.");
        }
        triagens.forEach(this::imprimir);
    }

    public void consultar() {
        Triagem triagem = lerTriagem();
        if (triagem != null) {
            imprimir(triagem);
        }
    }

    public void atualizar() {
        Triagem triagem = lerTriagem();
        if (triagem == null) {
            return;
        }
        imprimir(triagem);
        System.out.println("\n(Deixe em branco para manter o valor atual)");
        for (SintomaRelatado s : triagem.getSintomas()) {
            System.out.println("\nSintoma: " + s.getSintoma().getNome());
            s.setIntensidade(Entrada.lerInteiroOpcional("Intensidade (1 a 10) [" + s.getIntensidade() + "]: ",
                    1, 10, s.getIntensidade()));
            s.setDuracaoHoras(Entrada.lerInteiroOpcional("Duração em horas [" + s.getDuracaoHoras() + "]: ",
                    0, 8760, s.getDuracaoHoras()));
        }
        triagem.setObservacao(Entrada.lerOpcional("Observações [" + triagem.getObservacao() + "]: ",
                triagem.getObservacao()));

        Triagem atualizada = triagemService.atualizar(triagem);
        System.out.println("\nTriagem atualizada e reclassificada.");
        imprimir(atualizada);
    }

    public void excluir() {
        Triagem triagem = lerTriagem();
        if (triagem == null) {
            return;
        }
        System.out.print("Confirma a exclusão da triagem " + triagem.getId() + "? (s/n): ");
        if (Entrada.lerLinha().equalsIgnoreCase("s")) {
            triagemService.excluir(triagem.getId());
            System.out.println("Triagem excluída com sucesso!");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private Triagem lerTriagem() {
        Long id = Entrada.lerId("ID da triagem (0 para cancelar): ");
        if (id == null) {
            return null;
        }
        Triagem triagem = triagemService.buscar(id);
        if (triagem == null) {
            System.out.println("Triagem não encontrada!");
        }
        return triagem;
    }

    private void imprimir(Triagem triagem) {
        String tipo = triagem instanceof TriagemAdulto ? "Adulto" : "Pediátrica";
        System.out.println("\nTriagem " + triagem.getId() + " (" + tipo + ") - "
                + Formatador.formatarDataHora(triagem.getDataHora()) + " - Paciente: "
                + triagem.getPaciente().getNome());
        for (SintomaRelatado s : triagem.getSintomas()) {
            String observacao = s.getObservacao() == null || s.getObservacao().isEmpty()
                    ? "" : " | " + s.getObservacao();
            System.out.println("  " + s.getSintoma().getNome() + " | intensidade: " + s.getIntensidade()
                    + "/10 | duração: " + s.getDuracaoHoras() + " hora(s)" + observacao);
        }
        System.out.println("  Observações: " + triagem.getObservacao());
        System.out.println("  Prioridade: " + triagem.getPrioridade());
        for (ResultadoTriagem resultado : triagem.getResultados()) {
            System.out.println("  Resultado: " + resultado.gerarResumo());
        }
        System.out.println("  Encaminhamento: " + triagem.getEncaminhamento().gerarOrientacao());
        System.out.println("  Aviso: classificação orientativa, sem diagnóstico médico.");
    }
}
