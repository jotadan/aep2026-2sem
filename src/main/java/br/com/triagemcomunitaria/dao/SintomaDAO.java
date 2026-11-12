package br.com.triagemcomunitaria.dao;

import br.com.triagemcomunitaria.util.Conexao;
import br.com.triagemcomunitaria.model.Sintoma;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SintomaDAO {

    public long contar() {
        try (EntityManager em = Conexao.criarEntityManager()) {
            return em.createQuery("select count(s) from Sintoma s", Long.class).getSingleResult();
        }
    }

    public void salvarTodos(List<Sintoma> sintomas) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            for (Sintoma sintoma : sintomas) {
                em.persist(sintoma);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Sintoma> listar() {
        try (EntityManager em = Conexao.criarEntityManager()) {
            return em.createQuery("select s from Sintoma s order by s.id", Sintoma.class).getResultList();
        }
    }
}
