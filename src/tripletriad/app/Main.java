package tripletriad.app;

import javax.swing.SwingUtilities;

import tripletriad.controller.Jogo;
import tripletriad.model.*;
import tripletriad.gui.TripleTriadGUI;

public class Main {
    public static void main(String[] args) {
        Jogador jogador1 = new Jogador("Jogador 1");
        Jogador jogador2 = new Jogador("Jogador 2");

        Jogo jogo = new Jogo(jogador1, jogador2);

        String caminhoCSV = "src/resources/cards.csv";
        // Se você estiver executando a partir de um JAR, ou se 'src' não for o diretório de trabalho,
        // o carregamento de 'cards.csv' pode precisar ser ajustado para usar getResourceAsStream,
        // similar ao carregamento da imagem. Por enquanto, vamos manter como está se funcionar no seu ambiente.
        CartaLoader.distribuirCartas(caminhoCSV, jogador1, jogador2);

        SwingUtilities.invokeLater(() -> {
            TripleTriadGUI gui1 = new TripleTriadGUI(jogador1, jogo);
            gui1.setVisible(true); // Torna a GUI visível
        });

        SwingUtilities.invokeLater(() -> {
            TripleTriadGUI gui2 = new TripleTriadGUI(jogador2, jogo);
            gui2.setVisible(true); // Torna a GUI visível
        });
    }
}
