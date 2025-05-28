package tripletriad.gui;

import tripletriad.controller.Jogo;
import tripletriad.model.Jogador;

import javax.swing.*;
import java.awt.*;

public class TripleTriadGUI extends JFrame {

    private static final int CARD_WIDTH = 90;
    private static final int CARD_HEIGHT = 120;

    private Jogador jogador; // Jogador principal desta janela
    // Se precisar referenciar o oponente para o título da mão de cima,
    // você precisará passá-lo ou obter de 'jogo'
    private Jogo jogo;

    public TripleTriadGUI(Jogador jogador, Jogo jogo) {
        this.jogador = jogador;
        this.jogo = jogo;

        setTitle("Triple Triad - " + jogador.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Mão do Jogador Principal (Embaixo) ---
        JPanel playerHandContent = createHandContentPanel(5);
        JScrollPane playerScrollPane = new JScrollPane(playerHandContent);
        playerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        playerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        playerScrollPane.setBorder(BorderFactory.createTitledBorder("Cartas de " + jogador.getNome()));

        // --- Mão do Oponente (Em Cima) ---
        JPanel opponentHandContent = createHandContentPanel(5); // Reutiliza o método para criar o painel de cartas
        JScrollPane opponentScrollPane = new JScrollPane(opponentHandContent);
        opponentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        opponentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        // Você pode querer um título mais dinâmico se tiver a informação do oponente
        opponentScrollPane.setBorder(BorderFactory.createTitledBorder("Cartas do Oponente"));


        // --- Adicionando os painéis ao JFrame ---
        add(opponentScrollPane, BorderLayout.NORTH); // Adiciona a mão do oponente no TOPO
        add(playerScrollPane, BorderLayout.SOUTH);
        add(createGameBoardPanel(), BorderLayout.CENTER);
        add(createSideTrianglePanel("Lado Esquerdo", true), BorderLayout.WEST);
        add(createSideTrianglePanel("Lado Direito", false), BorderLayout.EAST);

        setPreferredSize(new Dimension(1024, 768));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createCardPlaceholder(String label) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardPanel.setBackground(Color.LIGHT_GRAY);
        JLabel cardLabel = new JLabel(label, SwingConstants.CENTER);
        cardPanel.add(cardLabel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createHandContentPanel(int cardCount) {
        JPanel handContentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (int i = 0; i < cardCount; i++) {
            // Alterado para "Carta" para ser genérico, já que o título do painel identifica o dono
            handContentPanel.add(createCardPlaceholder("Carta"));
        }
        int preferredWidth = (CARD_WIDTH + 10) * cardCount + 10;
        int preferredHeight = CARD_HEIGHT + 20;
        handContentPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handContentPanel;
    }

    private JPanel createGameBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBorder(BorderFactory.createTitledBorder("Tabuleiro"));
        for (int i = 0; i < 9; i++) {
            boardPanel.add(createCardPlaceholder("Slot"));
        }
        return boardPanel;
    }

    private JPanel createSideTrianglePanel(String title, boolean isLeft) {
        JPanel sidePanel = new JPanel(new BorderLayout(10, 10));

        JPanel peakPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        peakPanel.add(createCardPlaceholder("Carta")); // Rótulo genérico

        JPanel basePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        basePanel.add(createCardPlaceholder("Carta")); // Rótulo genérico
        basePanel.add(createCardPlaceholder("Carta")); // Rótulo genérico

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(peakPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(basePanel);
        contentPanel.add(Box.createVerticalGlue());

        sidePanel.add(contentPanel, BorderLayout.CENTER);
        sidePanel.setBorder(BorderFactory.createTitledBorder(title));

        int preferredWidth = CARD_WIDTH * 2 + 20 + (2 * 10);
        int preferredHeight = CARD_HEIGHT * 2 + 10 + (2 * 10);
        sidePanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        return sidePanel;
    }

    // Exemplo de como você poderia iniciar essa GUI (apenas para teste)
    // Você precisaria integrar isso com a lógica principal do seu jogo.
    public static void main(String[] args) {
        // Simulação de objetos Jogador e Jogo para teste
        Jogador demoJogador1 = new Jogador("Jogador 1");
        Jogador demoJogador2 = new Jogador("Jogador 2"); // Supondo que Jogo lida com dois jogadores
        Jogo demoJogo = new Jogo(demoJogador1, demoJogador2); // Ajuste o construtor de Jogo conforme necessário

        // Cria a GUI para o jogador 1
        SwingUtilities.invokeLater(() -> new TripleTriadGUI(demoJogador1, demoJogo));

        // Se você quisesse duas janelas, uma para cada jogador (mais complexo de sincronizar):
        // SwingUtilities.invokeLater(() -> new TripleTriadGUI(demoJogador2, demoJogo));
    }
}