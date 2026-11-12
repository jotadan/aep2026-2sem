package br.com.triagemcomunitaria.service;

import br.com.triagemcomunitaria.dao.SintomaDAO;
import br.com.triagemcomunitaria.model.Sintoma;

import java.util.ArrayList;
import java.util.List;

public class SintomaService {
    private static final int TOTAL_CATEGORIAS = 9;

    private final SintomaDAO sintomaDAO = new SintomaDAO();
    private List<Sintoma> catalogo = new ArrayList<>();

    public void carregarCatalogo() {
        if (sintomaDAO.contar() == 0) {
            sintomaDAO.salvarTodos(List.of(
                    new Sintoma(Sintoma.FEBRE, "Elevação da temperatura corporal."),
                    new Sintoma("Lesões", "Lesões na pele ou no corpo."),
                    new Sintoma("Dor no corpo", "Dor muscular ou generalizada."),
                    new Sintoma("Inchaço", "Aumento de volume em alguma região do corpo."),
                    new Sintoma(Sintoma.DIFICULDADE_RESPIRATORIA, "Dificuldade ou desconforto para respirar."),
                    new Sintoma("Náuseas", "Enjoo ou vontade de vomitar."),
                    new Sintoma("Fadiga", "Cansaço ou fraqueza fora do comum."),
                    new Sintoma("Tontura", "Sensação de desequilíbrio ou cabeça leve."),
                    new Sintoma("Mau-estar", "Sensação geral de indisposição."),
                    new Sintoma("Dor de cabeça", "Dor na região da cabeça."),
                    new Sintoma("Arrepios", "Sensação de frio com tremores."),
                    new Sintoma("Aumento da frequência cardíaca", "Batimentos cardíacos acelerados."),
                    new Sintoma("Suor frio", "Suor acompanhado de sensação de frio."),
                    new Sintoma("Sudorese", "Suor em excesso."),
                    new Sintoma("Perda de apetite", "Redução da vontade de comer."),
                    new Sintoma("Vermelhidão no rosto", "Rosto avermelhado.")));
        }
        catalogo = sintomaDAO.listar();
    }

    public List<Sintoma> getCatalogo() {
        return catalogo;
    }

    public List<Sintoma> getCategorias() {
        return catalogo.subList(0, TOTAL_CATEGORIAS);
    }

    public Sintoma buscarPorNome(String nome) {
        return catalogo.stream()
                .filter(s -> s.getNome().equals(nome))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Sintoma não encontrado no catálogo: " + nome));
    }
}
