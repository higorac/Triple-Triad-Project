package tripletriad.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class CartaLoader {
    public static void distribuirCartas(String caminhoCSV, Jogador j1, Jogador j2) {
        List<Carta> cartas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {
            String linha;
            br.readLine(); // pula o cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");

                int topo = Integer.parseInt(partes[0]);
                int direita = Integer.parseInt(partes[1]);
                int baixo = Integer.parseInt(partes[2]);
                int esquerda = Integer.parseInt(partes[3]);
                String tipo = partes[4];

                cartas.add(new Carta(topo, direita, baixo, esquerda, tipo, null)); // dono será definido depois
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        // embaralha
        Collections.shuffle(cartas);

        // distribui 5 pra cada jogador
        for (int i = 0; i < 5; i++) {
            Carta c1 = cartas.removeFirst();
            c1.setDono(j1);
            j1.adicionarCarta(c1);

            Carta c2 = cartas.removeFirst();
            c2.setDono(j2);
            j2.adicionarCarta(c2);
        }
    }

}
