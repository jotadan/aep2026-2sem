package br.com.triagemcomunitaria.dao;

import br.com.triagemcomunitaria.util.Conexao;
import br.com.triagemcomunitaria.model.Paciente;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PacienteDAO {

    public void salvar(Paciente paciente) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public Paciente buscarPorId(Long id) {
        try (EntityManager em = Conexao.criarEntityManager()) {
            return em.find(Paciente.class, id);
        }
    }

    public boolean existeCpf(String cpf) {
        try (EntityManager em = Conexao.criarEntityManager()) {
            Long total = em.createQuery("select count(p) from Paciente p where p.cpf = :cpf", Long.class)
                    .setParameter("cpf", cpf).getSingleResult();
            return total > 0;
        }
    }

    public List<Paciente> listar() {
        try (EntityManager em = Conexao.criarEntityManager()) {
            return em.createQuery("select p from Paciente p order by p.id", Paciente.class).getResultList();
        }
    }

    public void atualizar(Long id, String nome, String telefone, String contatoEmergencia) {
        EntityManager em = Conexao.criarEntityManager();
        try {
            em.getTransaction().begin();
            Paciente paciente = em.find(Paciente.class, id);
            paciente.atualizarDados(nome, telefone, contatoEmergencia);
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
            em.remove(em.find(Paciente.class, id));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexao.desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }
}
