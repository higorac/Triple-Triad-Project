package tripletriad.app;

import tripletriad.controller.Jogo;
import tripletriad.model.CartaLoader;
import tripletriad.model.Jogador;
import tripletriad.gui.TripleTriadGUI;
import tripletriad.util.SoundEffect;
import tripletriad.util.SoundManager;
import javax.swing.SwingUtilities;

public class Main {

    private static SoundManager soundManager;
    private static Jogador jogador1Global;
    private static Jogador jogador2Global;
    private static Jogo jogoAtual;
    private static TripleTriadGUI gui1Instance;
    private static TripleTriadGUI gui2Instance;

    private static volatile boolean jogador1QuerReiniciar = false;
    private static volatile boolean jogador2QuerReiniciar = false;
    private static volatile boolean reinicioEmProgressoGlobal = false;

    // GETTERS PÚBLICOS ESTÁTICOS para as flags de reinício
    public static boolean isJogador1QuerReiniciar() { //
        return jogador1QuerReiniciar; //
    }

    public static boolean isJogador2QuerReiniciar() { //
        return jogador2QuerReiniciar; //
    }

    public static void main(String[] args) {
        soundManager = SoundManager.getInstance();
        soundManager.playThemeSequence();
        iniciarNovoJogoEfetivamente();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (soundManager != null) {
                soundManager.stopAllSounds();
                soundManager.shutdownExecutor();
            }
        }));
    }

    private static void iniciarNovoJogoEfetivamente() {
        jogador1QuerReiniciar = false;
        jogador2QuerReiniciar = false;
        reinicioEmProgressoGlobal = false; // Certifique-se que esta flag também seja resetada aqui

        jogador1Global = new Jogador("Jogador 1");
        jogador2Global = new Jogador("Jogador 2");

        String caminhoCSV = "src/resources/cards.csv";
        CartaLoader.distribuirCartas(caminhoCSV, jogador1Global, jogador2Global);

        jogoAtual = new Jogo(jogador1Global, jogador2Global);

        if (gui1Instance != null) {
            gui1Instance.dispose();
        }
        if (gui2Instance != null) {
            gui2Instance.dispose();
        }

        final Jogo finalJogoAtual = jogoAtual;
        final Jogador finalJogador1 = jogador1Global;
        final Jogador finalJogador2 = jogador2Global;

        SwingUtilities.invokeLater(() -> {
            gui1Instance = new TripleTriadGUI(finalJogador1, finalJogoAtual, () -> Main.solicitarReiniciarJogo(finalJogador1));
            gui1Instance.setVisible(true);
        });

        SwingUtilities.invokeLater(() -> {
            gui2Instance = new TripleTriadGUI(finalJogador2, finalJogoAtual, () -> Main.solicitarReiniciarJogo(finalJogador2));
            gui2Instance.setVisible(true);
        });
    }

    public static synchronized void solicitarReiniciarJogo(Jogador jogadorSolicitante) {
        if (reinicioEmProgressoGlobal) {
            return;
        }

        boolean ambosConfirmaramAntes = isJogador1QuerReiniciar() && isJogador2QuerReiniciar(); // Use os getters

        if (jogadorSolicitante.equals(jogador1Global)) {
            jogador1QuerReiniciar = true;
            if (gui1Instance != null && !ambosConfirmaramAntes) {
                gui1Instance.exibirMensagemAguardandoOponente(true, jogador2Global.getNome());
            }
        } else if (jogadorSolicitante.equals(jogador2Global)) {
            jogador2QuerReiniciar = true;
            if (gui2Instance != null && !ambosConfirmaramAntes) {
                gui2Instance.exibirMensagemAguardandoOponente(true, jogador1Global.getNome());
            }
        }

        if (isJogador1QuerReiniciar() && isJogador2QuerReiniciar()) { // Use os getters
            reinicioEmProgressoGlobal = true;

            if (soundManager != null) {
                soundManager.stopSound(SoundEffect.WIN);
            }

            if (gui1Instance != null) gui1Instance.exibirMensagemAguardandoOponente(false, null);
            if (gui2Instance != null) gui2Instance.exibirMensagemAguardandoOponente(false, null);

            iniciarNovoJogoEfetivamente();
        }
    }
}