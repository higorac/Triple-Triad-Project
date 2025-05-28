package tripletriad.app;

import javax.swing.SwingUtilities;

import tripletriad.controller.Jogo;
import tripletriad.model.*;
import tripletriad.gui.TripleTriadGUI;

public class Main {
    public static void main(String[] args) {
        // Criação dos jogadores com nomes fixos ou definidos dinamicamente depois
        Jogador jogador1 = new Jogador("Jogador 1");
        Jogador jogador2 = new Jogador("Jogador 2");

        // Inicialização do jogo
        Jogo jogo = new Jogo(jogador1, jogador2);

        // Distribuição das cartas
        String caminhoCSV = "src/resources/cards.csv"; // Certifique-se que o caminho está correto
        CartaLoader.distribuirCartas(caminhoCSV, jogador1, jogador2);

        // Lançar as janelas Swing para os dois jogadores
        SwingUtilities.invokeLater(() -> new TripleTriadGUI(jogador1, jogo));
        SwingUtilities.invokeLater(() -> new TripleTriadGUI(jogador2, jogo));
    }
}
