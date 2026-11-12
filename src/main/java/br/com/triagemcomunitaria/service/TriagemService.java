package br.com.triagemcomunitaria.service;

import br.com.triagemcomunitaria.dao.TriagemDAO;
import br.com.triagemcomunitaria.model.Paciente;
import br.com.triagemcomunitaria.model.SintomaRelatado;
import br.com.triagemcomunitaria.model.Triagem;
import br.com.triagemcomunitaria.model.TriagemAdulto;
import br.com.triagemcomunitaria.model.TriagemPediatrica;

import java.time.LocalDateTime;
import java.util.List;

public class TriagemService {
    private final TriagemDAO triagemDAO = new TriagemDAO();

    public Triagem registrar(Paciente paciente, List<SintomaRelatado> sintomas, String observacao) {
        if (sintomas == null || sintomas.isEmpty()) {
            throw new IllegalArgumentException("Informe pelo menos um sintoma.");
        }
        LocalDateTime agora = LocalDateTime.now();
        Triagem triagem = paciente.isPediatrico()
                ? new TriagemPediatrica(paciente, agora, sintomas, observacao)
                : new TriagemAdulto(paciente, agora, sintomas, observacao);
        triagem.concluir();
        triagemDAO.salvar(triagem);
        return triagem;
    }

    public Triagem buscar(Long id) {
        return triagemDAO.buscarPorId(id);
    }

    public List<Triagem> historico(Long pacienteId) {
        return triagemDAO.listarPorPaciente(pacienteId);
    }

    public Triagem atualizar(Triagem triagem) {
        triagem.concluir();
        triagemDAO.atualizar(triagem);
        return triagemDAO.buscarPorId(triagem.getId());
    }

    public void excluir(Long id) {
        triagemDAO.excluir(id);
    }
}
