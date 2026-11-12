package br.com.triagemcomunitaria.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Entrada {
    private static Scanner scanner = new Scanner(System.in);

    public static void usarScanner(Scanner novo) {
        scanner = novo;
    }

    public static String lerLinha() {
        return scanner.nextLine().trim();
    }

    public static Long lerId(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();
            try {
                long id = Long.parseLong(texto);
                if (id == 0) {
                    System.out.println("Operação cancelada.");
                    return null;
                }
                if (id > 0) {
                    return id;
                }
                System.out.println("ID inválido. Informe um número positivo.");
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Digite apenas números.");
            }
        }
    }

    public static int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Opção inexistente. Informe um número entre " + minimo + " e " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números inteiros.");
            }
        }
    }

    public static int lerInteiroOpcional(String mensagem, int minimo, int maximo, int atual) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();
            if (texto.isEmpty()) {
                return atual;
            }
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Valor fora do intervalo. Informe um número entre " + minimo + " e " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números inteiros.");
            }
        }
    }

    public static double lerTemperatura() {
        while (true) {
            System.out.print("Informe sua temperatura (°C): ");
            String texto = lerLinha().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (valor >= 30 && valor <= 45) {
                    return valor;
                }
                System.out.println("Temperatura fora do intervalo plausível (30 a 45 °C).");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Exemplo: 38,5");
            }
        }
    }

    public static String lerObrigatorio(String mensagem, int tamanhoMaximo) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();
            if (texto.isEmpty()) {
                System.out.println("Este campo é obrigatório.");
            } else if (texto.length() > tamanhoMaximo) {
                System.out.println("Tamanho máximo de " + tamanhoMaximo + " caracteres.");
            } else {
                return texto;
            }
        }
    }

    public static String lerOpcional(String mensagem, String valorAtual) {
        System.out.print(mensagem);
        String texto = lerLinha();
        if (texto.isEmpty()) {
            return valorAtual;
        }
        return texto;
    }

    public static String lerCpf() {
        while (true) {
            System.out.print("CPF: ");
            String digitos = Formatador.soDigitos(lerLinha());
            if (digitos.length() == 11) {
                return Formatador.formatarCpf(digitos);
            }
            System.out.println("CPF inválido. Informe os 11 dígitos.");
        }
    }

    public static LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                LocalDate data = Formatador.lerData(lerLinha());
                if (data.isAfter(LocalDate.now()) || data.getYear() < 1900) {
                    System.out.println("Data fora do intervalo permitido.");
                } else {
                    return data;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato DD/MM/AAAA, com uma data existente.");
            }
        }
    }
}
