package tripletriad.model;

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class CartaLoader {
    public static void distribuirCartas(String caminhoCSV, Jogador j1, Jogador j2) {
        List<Carta> cartas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoCSV), "UTF-16"))) {
            String linha;
            br.readLine(); // pula o cabeçalho

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] partes = linha.split(";");
                for (String parte : partes) {
                    System.out.println(parte);
                }
                System.out.println();

                if (partes.length < 4) {
                    System.out.println("Formato de linha inválido: " + linha);
                    continue;
                }

                try {

                    String nome = partes[1].trim();
                    String tipo = partes[4].trim();
                    String atributos = partes[2].trim();
                    System.out.println("atributos: " + atributos);

                    int topo = -1, direita = -1, baixo = -1, esquerda = -1;

                    String[] pares = atributos.split("[;,]");
                    for (int i = 0; i < pares.length; i++) {
                        System.out.println("[" + i + "] " + pares[i] + " - " + tipo);
                    }
                    System.out.println();
                    for (String par : pares) {
                        String[] kv = par.trim().split(":");
                        if (kv.length != 2) continue;
                        String key = kv[0].trim().toUpperCase();
                        int valor = Integer.parseInt(kv[1].trim());

                        switch (key) {
                            case "UP" -> topo = valor;
                            case "RIGHT" -> direita = valor;
                            case "DOWN" -> baixo = valor;
                            case "LEFT" -> esquerda = valor;
                        }
                    }

//                    if (topo == -1 || direita == -1 || baixo == -1 || esquerda == -1) {
//                        System.out.println("direita = " + direita);
//                        System.out.println("Atributos incompletos: " + linha);
//                        continue;
//                    }

                    cartas.add(new Carta(nome, topo, direita, baixo, esquerda, tipo, null));

                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter linha para inteiro: " + linha);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        Collections.shuffle(cartas);

        for (int i = 0; i < 5 && cartas.size() >= 2; i++) {
            Carta c1 = cartas.removeFirst();
            c1.setDono(j1);
            j1.adicionarCarta(c1);

            Carta c2 = cartas.removeFirst();
            c2.setDono(j2);
            j2.adicionarCarta(c2);
        }
    }
}
