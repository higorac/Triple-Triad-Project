package tripletriad.model;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Responsável por carregar os dados das cartas de um arquivo CSV.
 * Após o carregamento, as cartas são embaralhadas e distribuídas
 * igualmente entre os dois jogadores participantes.
 */

public class CartaLoader {
    public static void distribuirCartas(String caminhoCSV, Jogador j1, Jogador j2) { //
        List<Carta> cartas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoCSV), "UTF-16"))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                // Regex para substituir ; por , apenas dentro dos colchetes (ranks)
                linha = java.util.regex.Pattern.compile("\\[(.*?)]") //
                        .matcher(linha)
                        .replaceAll(match -> "[" + match.group(1).replace(";", ",") + "]");

                String[] partes = linha.split(";"); //

                if (partes.length < 4) {
                    System.err.println("Formato de linha inválido no CSV: " + linha);
                    continue;
                }

                try {
                    int id = Integer.parseInt(partes[0].trim()); // <-- LER E CONVERTER ID
                    String nome = partes[1].trim(); //
                    String atributosBruto = partes[2].trim().replaceAll("[\\[\\]]", ""); //
                    String tipo = partes[3].trim(); //

                    int topo = -1, direita = -1, baixo = -1, esquerda = -1;

                    String[] pares = atributosBruto.split("[,]"); //
                    for (String par : pares) {
                        String[] kv = par.trim().split(":");
                        if (kv.length != 2) continue;
                        String key = kv[0].trim().toUpperCase();
                        int valor = Integer.parseInt(kv[1].trim());

                        switch (key) { //
                            case "UP" -> topo = valor;
                            case "RIGHT" -> direita = valor;
                            case "DOWN" -> baixo = valor;
                            case "LEFT" -> esquerda = valor;
                        }
                    }
                    cartas.add(new Carta(id, nome, topo, direita, baixo, esquerda, tipo)); //

                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter número na linha do CSV: " + linha + " | Erro: " + e.getMessage());
                }
            }

        } catch (UnsupportedEncodingException e) {
            System.err.println("Encoding não suportado ao ler CSV: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro de I/O ao ler o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }

        Collections.shuffle(cartas); //

        // Distribui 5 cartas para cada jogador
        for (int i = 0; i < 5 && cartas.size() >= 2; i++) { //
            Carta c1 = cartas.removeFirst();
            c1.setDono(j1);
            j1.adicionarCarta(c1);

            Carta c2 = cartas.removeFirst();
            c2.setDono(j2);
            j2.adicionarCarta(c2);
        }
    }
}