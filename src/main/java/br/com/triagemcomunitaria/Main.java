package br.com.triagemcomunitaria;

import br.com.triagemcomunitaria.model.Sintoma;
import br.com.triagemcomunitaria.service.SintomaService;
import br.com.triagemcomunitaria.util.Conexao;
import br.com.triagemcomunitaria.util.Entrada;
import br.com.triagemcomunitaria.view.MenuPrincipal;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final SintomaService sintomaService = new SintomaService();

    public static void main(String[] args) {
        try {
            Conexao.abrir();
            sintomaService.carregarCatalogo();
        } catch (RuntimeException e) {
            mostrarErroDeConexao(e);
            Conexao.fechar();
            return;
        }

        try {
            new MenuPrincipal(sintomaService).executar();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Encerrando sistema...");
        } finally {
            Conexao.fechar();
        }
    }

    private static void mostrarErroDeConexao(RuntimeException e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        System.out.println("Não foi possível conectar ao banco de dados. Verifique se o PostgreSQL está ativo e "
                + "as variáveis DB_URL, DB_USER e DB_PASSWORD.");
        System.out.println("Motivo: " + causa.getMessage());
    }

    static void reiniciar(Scanner novaEntrada, String urlBanco) {
        Conexao.usarBancoDeTeste(urlBanco);
        Entrada.usarScanner(novaEntrada);
    }

    static List<Sintoma> getCatalogo() {
        return sintomaService.getCatalogo();
    }
}
