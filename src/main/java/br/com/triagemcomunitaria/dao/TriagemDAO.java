package br.com.triagemcomunitaria.dao;

import br.com.triagemcomunitaria.util.Conexao;
import br.com.triagemcomunitaria.model.SintomaRelatado;
import br.com.triagemcomunitaria.model.Triagem;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TriagemDAO {

    public void salvar(Triagem triagem) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(triagem);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public Triagem buscarPorId(Long id) {
        try (EntityManager em = Conexao.criarEntityManager()) {
            Triagem triagem = em.find(Triagem.class, id);
            if (triagem != null) {
                carregar(triagem);
            }
            return triagem;
        }
    }

    public List<Triagem> listarPorPaciente(Long pacienteId) {
        try (EntityManager em = Conexao.criarEntityManager()) {
            List<Triagem> triagens = em.createQuery(
                    "select t from Triagem t where t.paciente.id = :id order by t.dataHora", Triagem.class)
                    .setParameter("id", pacienteId).getResultList();
            triagens.forEach(this::carregar);
            return triagens;
        }
    }

    public long contarPorPaciente(Long pacienteId) {
        try (EntityManager em = Conexao.criarEntityManager()) {
            return em.createQuery("select count(t) from Triagem t where t.paciente.id = :id", Long.class)
                    .setParameter("id", pacienteId).getSingleResult();
        }
    }

    public void atualizar(Triagem triagem) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(triagem);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public void excluir(Long id) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.find(Triagem.class, id));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    private void carregar(Triagem triagem) {
        triagem.getPaciente().getNome();
        for (SintomaRelatado s : triagem.getSintomas()) {
            s.getSintoma().getNome();
        }
        triagem.getResultados().size();
    }
}
