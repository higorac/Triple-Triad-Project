package tripletriad.app;

import java.util.Scanner;

import tripletriad.controller.Jogo;
import tripletriad.model.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o nome do Jogador 1: ");
        String nome1 = scanner.nextLine();
        Jogador jogador1 = new Jogador(nome1);

        System.out.print("Digite o nome do Jogador 2: ");
        String nome2 = scanner.nextLine();
        Jogador jogador2 = new Jogador(nome2);

        Jogo jogo = new Jogo(jogador1, jogador2);

        // Usa CartaLoader para distribuir 5 cartas do CSV para cada jogador
        String caminhoCSV = "src/resources/cards.csv"; // ou o caminho certo no seu projeto
        CartaLoader.distribuirCartas(caminhoCSV, jogador1, jogador2);

        // Começa o jogo
        jogo.iniciar(scanner);

        scanner.close();
    }
}
