package br.com.triagemcomunitaria.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao {
    private static EntityManagerFactory emf;
    private static String urlTeste = null;
    private static final Logger logHibernate = Logger.getLogger("org.hibernate");

    public static void abrir() {
        logHibernate.setLevel(Level.SEVERE);

        Map<String, Object> propriedades = new HashMap<>();
        if (urlTeste != null) {
            propriedades.put("jakarta.persistence.jdbc.url", urlTeste);
            propriedades.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
            propriedades.put("jakarta.persistence.jdbc.user", "sa");
            propriedades.put("jakarta.persistence.jdbc.password", "");
        } else {
            propriedades.put("jakarta.persistence.jdbc.url",
                    variavel("DB_URL", "jdbc:postgresql://localhost:5433/triagem"));
            propriedades.put("jakarta.persistence.jdbc.user", variavel("DB_USER", "postgres"));
            propriedades.put("jakarta.persistence.jdbc.password", variavel("DB_PASSWORD", "postgres"));
        }
        emf = Persistence.createEntityManagerFactory("triagem", propriedades);
    }

    public static void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

    public static EntityManager criarEntityManager() {
        return emf.createEntityManager();
    }

    public static void desfazer(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    public static void usarBancoDeTeste(String url) {
        fechar();
        urlTeste = url;
    }

    private static String variavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
