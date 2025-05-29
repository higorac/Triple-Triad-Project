package tripletriad.app; // Pacote correto para a classe Main

// Importações corretas
import tripletriad.controller.Jogo; // Importa a classe Jogo do pacote controller
import tripletriad.model.CartaLoader;
import tripletriad.model.Jogador;
import tripletriad.gui.TripleTriadGUI;
import tripletriad.util.SoundManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize SoundManager early
        final SoundManager soundManager = SoundManager.getInstance();

        // Start the theme music sequence
        soundManager.playThemeSequence();

        Jogador jogador1 = new Jogador("Jogador 1");
        Jogador jogador2 = new Jogador("Jogador 2");

        String caminhoCSV = "src/resources/cards.csv";
        CartaLoader.distribuirCartas(caminhoCSV, jogador1, jogador2);

        Jogo jogo = new Jogo(jogador1, jogador2); // Agora deve encontrar a classe Jogo

        SwingUtilities.invokeLater(() -> {
            TripleTriadGUI gui1 = new TripleTriadGUI(jogador1, jogo);
            gui1.setVisible(true);
        });

        SwingUtilities.invokeLater(() -> {
            TripleTriadGUI gui2 = new TripleTriadGUI(jogador2, jogo);
            gui2.setVisible(true);
        });

        // Add a shutdown hook to stop sounds and the executor when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // System.out.println("Shutdown hook triggered. Stopping sounds and executor.");
            soundManager.stopAllSounds();
            soundManager.shutdownExecutor();
        }));
    }
}