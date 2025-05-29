// Em src/tripletriad/app/Main.java
package tripletriad.app;

import javax.swing.SwingUtilities;
import tripletriad.controller.Jogo;
import tripletriad.model.*;
import tripletriad.gui.TripleTriadGUI;

public class Main {
    public static void main(String[] args) {
        Jogador jogador1 = new Jogador("Jogador 1"); //
        Jogador jogador2 = new Jogador("Jogador 2"); //

        // PASSO 1: Distribuir cartas PRIMEIRO
        String caminhoCSV = "src/resources/cards.csv"; //
        CartaLoader.distribuirCartas(caminhoCSV, jogador1, jogador2); //

        // PASSO 2: Criar o Jogo DEPOIS que os jogadores têm cartas
        Jogo jogo = new Jogo(jogador1, jogador2); //
        // O construtor do Jogo agora poderá registrar as cartas inicialmente visíveis

        SwingUtilities.invokeLater(() -> { //
            TripleTriadGUI gui1 = new TripleTriadGUI(jogador1, jogo); //
            gui1.setVisible(true); //
        });

        SwingUtilities.invokeLater(() -> { //
            TripleTriadGUI gui2 = new TripleTriadGUI(jogador2, jogo); //
            gui2.setVisible(true); //
        });
    }
}