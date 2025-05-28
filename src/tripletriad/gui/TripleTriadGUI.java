package tripletriad.gui;

import tripletriad.controller.Jogo;
import tripletriad.model.Carta; // Certifique-se que Carta está importado
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro; // Certifique-se que Tabuleiro está importado

import javax.swing.*;
import java.awt.*;
// ... outros imports

public class TripleTriadGUI extends JFrame {

    private static final int CARD_WIDTH = 100; // Mantenha consistente com GameCardPanel
    private static final int CARD_HEIGHT = 140; // Mantenha consistente com GameCardPanel

    private Jogador jogador;
    private Jogo jogo;

    public TripleTriadGUI(Jogador jogador, Jogo jogo) {
        this.jogador = jogador;
        this.jogo = jogo;

        setTitle("Triple Triad - " + jogador.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Referência para o painel de fundo
        BackgroundPanel backgroundPanel = new BackgroundPanel("/resources/images/back.png"); // Seu fundo do jogo
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        Jogador oponente = (jogo.getJogador1() == jogador) ? jogo.getJogador2() : jogo.getJogador1();

        // --- Mão do Jogador Principal (Embaixo) ---
        JPanel playerHandContent = createHandDisplayPanel(jogador.getCartasNaMao()); // Renomeado para clareza
        // ... (configurações do playerScrollPane como antes) ...
        JScrollPane playerScrollPane = new JScrollPane(playerHandContent);
        configureScrollPane(playerScrollPane, "Cartas de " + jogador.getNome());


        // --- Mão do Oponente (Em Cima) ---
        java.util.List<Carta> opponentVisibleCards = new java.util.ArrayList<>();
        String opponentName = "Oponente";
        if (oponente != null) {
            opponentName = oponente.getNome();
            java.util.List<Carta> oponentHand = oponente.getCartasNaMao();
            for(int i = 0; i < Math.min(3, oponentHand.size()); i++) {
                opponentVisibleCards.add(oponentHand.get(i));
            }
        }
        JPanel opponentHandContent = createHandDisplayPanel(opponentVisibleCards); // Renomeado
        JScrollPane opponentScrollPane = new JScrollPane(opponentHandContent);
        configureScrollPane(opponentScrollPane, "Cartas de " + opponentName);

        // --- Painel do Tabuleiro ---
        JPanel gameBoardDisplayPanel = createGameBoardDisplayPanel(); // Renomeado

        // --- Painéis Laterais ---
        // Você pode querer popular isso depois com informações ou cartas específicas
        JPanel leftPanel = createSideInfoPanel("Info Jogador 1", true); // Renomeado
        JPanel rightPanel = createSideInfoPanel("Info Jogador 2", false); // Renomeado

        // Adicionar os componentes ao backgroundPanel
        backgroundPanel.add(opponentScrollPane, BorderLayout.NORTH);
        backgroundPanel.add(playerScrollPane, BorderLayout.SOUTH);
        backgroundPanel.add(gameBoardDisplayPanel, BorderLayout.CENTER);
        backgroundPanel.add(leftPanel, BorderLayout.WEST);
        backgroundPanel.add(rightPanel, BorderLayout.EAST);

        setPreferredSize(new Dimension(1024, 768)); // Ajuste se necessário
        pack();
        setLocationRelativeTo(null);
    }

    // Novo método para configurar JScrollPanes
    private void configureScrollPane(JScrollPane scrollPane, String title) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
    }


    // Método para criar o painel de exibição da mão (substitui createHandContentPanel)
    private JPanel createHandDisplayPanel(java.util.List<Carta> cartasNaMao) {
        JPanel handDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handDisplayPanel.setOpaque(false);

        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        if (cartasNaMao != null && !cartasNaMao.isEmpty()) {
            for (Carta carta : cartasNaMao) {
                handDisplayPanel.add(new GameCardPanel(carta, cardDimension));
            }
        } else {
            for (int i = 0; i < 5; i++) { // Mostra 5 slots vazios por padrão
                handDisplayPanel.add(new GameCardPanel(null, cardDimension));
            }
        }
        // Ajustar o tamanho preferido
        int numCartasParaCalculo = (cartasNaMao != null && !cartasNaMao.isEmpty()) ? cartasNaMao.size() : 5;
        if (numCartasParaCalculo == 0) numCartasParaCalculo = 1;
        int preferredWidth = (CARD_WIDTH + 10) * numCartasParaCalculo + 10;
        int preferredHeight = CARD_HEIGHT + 20;
        handDisplayPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handDisplayPanel;
    }

    // Método para criar o painel de exibição do tabuleiro (substitui createGameBoardPanel)
    private JPanel createGameBoardDisplayPanel() {
        JPanel boardDisplayPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardDisplayPanel.setOpaque(false);
        boardDisplayPanel.setBorder(BorderFactory.createTitledBorder("Tabuleiro"));

        Tabuleiro tabuleiroModel = jogo.getTabuleiro();
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);

        if (tabuleiroModel != null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Carta cartaNoSlot = tabuleiroModel.getCarta(i, j);
                    boardDisplayPanel.add(new GameCardPanel(cartaNoSlot, cardDimension));
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                boardDisplayPanel.add(new GameCardPanel(null, cardDimension));
            }
        }
        return boardDisplayPanel;
    }

    // Método para criar painéis laterais de informação (substitui createSideTrianglePanel)
    private JPanel createSideInfoPanel(String title, boolean isLeft) {
        JPanel sidePanel = new JPanel(new BorderLayout(10, 10));
        sidePanel.setOpaque(false);
        sidePanel.setBorder(BorderFactory.createTitledBorder(title));
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);

        // Conteúdo de exemplo - você pode personalizar isso
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalGlue());
        // Adicionar placeholders ou informações relevantes aqui
        content.add(new GameCardPanel(null, cardDimension)); // Exemplo de slot de carta
        content.add(Box.createRigidArea(new Dimension(0,10)));
        content.add(new GameCardPanel(null, cardDimension));
        content.add(Box.createVerticalGlue());

        sidePanel.add(content, BorderLayout.CENTER);

        int preferredWidth = CARD_WIDTH + 40; // Ajuste conforme o conteúdo
        int preferredHeight = (CARD_HEIGHT + 10) * 2 + 40; // Ajuste
        sidePanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return sidePanel;
    }
}