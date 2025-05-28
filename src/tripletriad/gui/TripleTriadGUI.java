package tripletriad.gui;

import tripletriad.controller.Jogo;
import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TripleTriadGUI extends JFrame {

    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140;

    private Jogador jogador;
    private Jogo jogo;

    public TripleTriadGUI(Jogador jogador, Jogo jogo) {
        this.jogador = jogador;
        this.jogo = jogo;

        setTitle("Triple Triad - " + jogador.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/resources/images/back.png"); //
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        Jogador oponente = (jogo.getJogador1() == jogador) ? jogo.getJogador2() : jogo.getJogador1(); //

        // --- Mão do Jogador Principal (Embaixo) ---
        JPanel playerHandPanel = createPlayerHandDisplayPanel(jogador.getCartasNaMao()); //
        JScrollPane playerScrollPane = new JScrollPane(playerHandPanel);
        configureScrollPane(playerScrollPane, "Cartas de " + jogador.getNome()); //

        // --- Mão Oculta do Oponente (Em Cima) ---
        JPanel opponentHiddenHandPanel = createFaceDownCardsPanel(5);
        JScrollPane opponentHiddenScrollPane = new JScrollPane(opponentHiddenHandPanel);
        configureScrollPane(opponentHiddenScrollPane, "Mão do Oponente (Oculta)");

        // --- Painel do Tabuleiro (Centro) ---
        JPanel gameBoardDisplayPanel = createGameBoardDisplayPanel(); //

        // --- Painel Lateral Esquerdo (3 Cartas Visíveis do Oponente em Triângulo) ---
        List<Carta> opponentVisibleCards = new ArrayList<>();
        if (oponente != null) {
            List<Carta> oponentFullHand = oponente.getCartasNaMao(); //
            for (int i = 0; i < oponentFullHand.size() && i < 3; i++) { // Pega até 3 cartas
                opponentVisibleCards.add(oponentFullHand.get(i));
            }
        }
        JPanel leftOpponentCardsPanel = createSideTrianglePanel("Oponente Visível", opponentVisibleCards, false); // false = não viradas para baixo

        // --- Painel Lateral Direito (3 Cartas Ocultas em Triângulo) ---
        JPanel rightPlaceholderPanel = createSideTrianglePanel("Cartas Reserva", null, true); // true = viradas para baixo

        backgroundPanel.add(opponentHiddenScrollPane, BorderLayout.NORTH);
        backgroundPanel.add(playerScrollPane, BorderLayout.SOUTH);
        backgroundPanel.add(gameBoardDisplayPanel, BorderLayout.CENTER);
        backgroundPanel.add(leftOpponentCardsPanel, BorderLayout.WEST);
        backgroundPanel.add(rightPlaceholderPanel, BorderLayout.EAST);

        // Define o tamanho preferido e torna a janela não redimensionável
        setPreferredSize(new Dimension(860, 840)); // Ajustado após cálculos e um pouco de folga
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void configureScrollPane(JScrollPane scrollPane, String title) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
    }

    private JPanel createPlayerHandDisplayPanel(List<Carta> cartasNaMao) {
        JPanel handDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handDisplayPanel.setOpaque(false);
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);

        if (cartasNaMao != null && !cartasNaMao.isEmpty()) {
            for (Carta carta : cartasNaMao) {
                handDisplayPanel.add(new GameCardPanel(carta, cardDimension, false));
            }
            for (int i = cartasNaMao.size(); i < 5; i++) {
                handDisplayPanel.add(new GameCardPanel(null, cardDimension, false));
            }
        } else {
            for (int i = 0; i < 5; i++) {
                handDisplayPanel.add(new GameCardPanel(null, cardDimension, false));
            }
        }

        int numCartasParaCalculo = 5;
        int preferredWidth = (CARD_WIDTH + 10) * numCartasParaCalculo + 10; // Largura para 5 cartas
        int preferredHeight = CARD_HEIGHT + 30;
        handDisplayPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handDisplayPanel;
    }

    private JPanel createFaceDownCardsPanel(int numCards) {
        JPanel faceDownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        faceDownPanel.setOpaque(false);
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        for (int i = 0; i < numCards; i++) {
            faceDownPanel.add(new GameCardPanel(null, cardDimension, true));
        }
        int preferredWidth = (CARD_WIDTH + 10) * numCards + 10;
        int preferredHeight = CARD_HEIGHT + 30;
        faceDownPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return faceDownPanel;
    }

    private JPanel createGameBoardDisplayPanel() {
        JPanel boardDisplayPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardDisplayPanel.setOpaque(false);
        boardDisplayPanel.setBorder(BorderFactory.createTitledBorder("Tabuleiro"));
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);

        Tabuleiro tabuleiroModel = jogo.getTabuleiro(); //
        if (tabuleiroModel != null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Carta cartaNoSlot = tabuleiroModel.getCarta(i, j); //
                    boardDisplayPanel.add(new GameCardPanel(cartaNoSlot, cardDimension, false));
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                boardDisplayPanel.add(new GameCardPanel(null, cardDimension, false));
            }
        }
        // Definindo um tamanho preferido para o painel do tabuleiro
        int boardPrefWidth = (CARD_WIDTH * 3) + (5 * 2) + 30; // 3 cartas, 2 vãos, padding
        int boardPrefHeight = (CARD_HEIGHT * 3) + (5 * 2) + 30; // 3 cartas, 2 vãos, padding
        boardDisplayPanel.setPreferredSize(new Dimension(boardPrefWidth, boardPrefHeight));
        return boardDisplayPanel;
    }

    // Novo método para criar painéis laterais em formato de triângulo /_\
    private JPanel createSideTrianglePanel(String title, List<Carta> cards, boolean showFaceDown) {
        JPanel mainSidePanel = new JPanel(new BorderLayout(5, 5));
        mainSidePanel.setOpaque(false);
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(title));

        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);

        JPanel triangleContainer = new JPanel();
        triangleContainer.setLayout(new BoxLayout(triangleContainer, BoxLayout.Y_AXIS));
        triangleContainer.setOpaque(false);

        // Linha superior: 1 carta, centralizada
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Sem vãos internos
        topRow.setOpaque(false);
        Carta card1 = (cards != null && cards.size() >= 1) ? cards.get(0) : null;
        topRow.add(new GameCardPanel(card1, cardDimension, showFaceDown));

        // Linha inferior: 2 cartas
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Vão de 5px entre as cartas
        bottomRow.setOpaque(false);
        Carta card2 = (cards != null && cards.size() >= 2) ? cards.get(1) : null;
        Carta card3 = (cards != null && cards.size() >= 3) ? cards.get(2) : null;
        bottomRow.add(new GameCardPanel(card2, cardDimension, showFaceDown));
        bottomRow.add(new GameCardPanel(card3, cardDimension, showFaceDown));

        // Adiciona um preenchimento flexível para centralizar verticalmente o triângulo
        triangleContainer.add(Box.createVerticalGlue());
        triangleContainer.add(topRow);
        triangleContainer.add(Box.createRigidArea(new Dimension(0, 5))); // Vão entre as linhas
        triangleContainer.add(bottomRow);
        triangleContainer.add(Box.createVerticalGlue());

        mainSidePanel.add(triangleContainer, BorderLayout.CENTER);

        // Calcula o tamanho preferido para o painel lateral
        // Largura: baseada na linha inferior (2 cartas + vão) + padding para borda
        // Altura: baseada em 2 alturas de carta + vão entre linhas + padding para borda
        int prefWidth = (CARD_WIDTH * 2) + 5 + 30; // (100*2) + 5 + 30 = 235
        int prefHeight = (CARD_HEIGHT * 2) + 5 + 40; // (140*2) + 5 + 40 = 325
        mainSidePanel.setPreferredSize(new Dimension(prefWidth, prefHeight));

        return mainSidePanel;
    }
}