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
    private static final int CARD_HEIGHT = 140; // Altura total incluindo nome abaixo da carta
    private static final int DEFAULT_HGAP_HANDS = 10; // Espaçamento horizontal para as mãos
    private static final int BOARD_GRID_GAP = 5;     // Espaçamento (hgap e vgap) para o grid do tabuleiro

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
        String opponentName = (oponente != null) ? oponente.getNome() : "Oponente"; //

        // --- Mão do Jogador Principal (Embaixo) ---
        JPanel playerHandPanel = createPlayerHandDisplayPanel(jogador.getCartasNaMao()); //
        JScrollPane playerScrollPane = new JScrollPane(playerHandPanel);
        configureScrollPane(playerScrollPane, "Cartas de " + jogador.getNome()); //

        // --- Mão Oculta do Oponente (Em Cima) ---
        JPanel opponentHiddenHandPanel = createFaceDownCardsPanel(5); //
        JScrollPane opponentHiddenScrollPane = new JScrollPane(opponentHiddenHandPanel);
        configureScrollPane(opponentHiddenScrollPane, "Mão do Oponente (Oculta)"); //

        // --- Painel do Tabuleiro (Centro) ---
        JPanel gameBoardDisplayPanel = createGameBoardDisplayPanel(); //

        // --- Painel Lateral Esquerdo (3 Cartas Visíveis do Oponente) ---
        List<Carta> opponentVisibleCards = new ArrayList<>(); //
        if (oponente != null) {
            List<Carta> oponentFullHand = oponente.getCartasNaMao(); //
            for (int i = 0; i < oponentFullHand.size() && i < 3; i++) { //
                opponentVisibleCards.add(oponentFullHand.get(i)); //
            }
        }
        JPanel leftOpponentCardsPanel = createSideTrianglePanel("Cartas do Oponente", opponentVisibleCards, false); //

        // --- Painel Lateral Direito (3 Cartas Reserva do Oponente, Ocultas) ---
        JPanel rightOpponentReservePanel = createSideTrianglePanel("Cartas de " + opponentName, null, true); //

        backgroundPanel.add(opponentHiddenScrollPane, BorderLayout.NORTH); //
        backgroundPanel.add(playerScrollPane, BorderLayout.SOUTH); //
        backgroundPanel.add(gameBoardDisplayPanel, BorderLayout.CENTER); //
        backgroundPanel.add(leftOpponentCardsPanel, BorderLayout.WEST); //
        backgroundPanel.add(rightOpponentReservePanel, BorderLayout.EAST); //

        setPreferredSize(new Dimension(860, 840));  //
        pack(); //
        setResizable(false); //
        setLocationRelativeTo(null); //
    }

    private void configureScrollPane(JScrollPane scrollPane, String title) {
        scrollPane.setOpaque(false); //
        scrollPane.getViewport().setOpaque(false); //
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); //
        scrollPane.setBorder(BorderFactory.createTitledBorder(title)); //
    }

    private JPanel createPlayerHandDisplayPanel(List<Carta> cartasNaMao) {
        JPanel handDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 10)); //
        handDisplayPanel.setOpaque(false); //
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT); //
        int numCartasParaCalculo = 5; //

        if (cartasNaMao != null && !cartasNaMao.isEmpty()) { //
            for (Carta carta : cartasNaMao) { //
                handDisplayPanel.add(new GameCardPanel(carta, cardDimension, false)); //
            }
            for (int i = cartasNaMao.size(); i < numCartasParaCalculo; i++) { //
                handDisplayPanel.add(new GameCardPanel(null, cardDimension, false)); //
            }
        } else {
            for (int i = 0; i < numCartasParaCalculo; i++) { //
                handDisplayPanel.add(new GameCardPanel(null, cardDimension, false)); //
            }
        }

        int preferredWidth = (CARD_WIDTH * numCartasParaCalculo) + (DEFAULT_HGAP_HANDS * (numCartasParaCalculo + 1)); //
        if (numCartasParaCalculo == 0) preferredWidth = DEFAULT_HGAP_HANDS * 2; //

        int preferredHeight = CARD_HEIGHT + 30; //
        handDisplayPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight)); //
        return handDisplayPanel;
    }

    private JPanel createFaceDownCardsPanel(int numCards) {
        JPanel faceDownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 10)); //
        faceDownPanel.setOpaque(false); //
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT); //
        for (int i = 0; i < numCards; i++) { //
            faceDownPanel.add(new GameCardPanel(null, cardDimension, true)); //
        }

        int preferredWidth = (CARD_WIDTH * numCards) + (DEFAULT_HGAP_HANDS * (numCards + 1)); //
        if (numCards == 0) preferredWidth = DEFAULT_HGAP_HANDS * 2; //

        int preferredHeight = CARD_HEIGHT + 30; //
        faceDownPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight)); //
        return faceDownPanel;
    }

    private JPanel createGameBoardDisplayPanel() {
        // GridLayout agora usa BOARD_GRID_GAP para hgap e vgap
        JPanel boardDisplayPanel = new JPanel(new GridLayout(3, 3, BOARD_GRID_GAP, BOARD_GRID_GAP));  //
        boardDisplayPanel.setOpaque(false); //

        // O EmptyBorder ao redor do grid pode ser mantido ou ajustado se necessário
        int boardOuterPadding = 5;
        boardDisplayPanel.setBorder(BorderFactory.createEmptyBorder(boardOuterPadding, boardOuterPadding, boardOuterPadding, boardOuterPadding));  //

        Dimension cardSlotDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT); //

        Tabuleiro tabuleiroModel = jogo.getTabuleiro(); //
        if (tabuleiroModel != null) { //
            for (int i = 0; i < 3; i++) { //
                for (int j = 0; j < 3; j++) { //
                    Carta cartaNoSlot = tabuleiroModel.getCarta(i, j); //
                    boardDisplayPanel.add(new GameCardPanel(cartaNoSlot, cardSlotDimension, false)); //
                }
            }
        } else {
            for (int i = 0; i < 9; i++) { //
                boardDisplayPanel.add(new GameCardPanel(null, cardSlotDimension, false)); //
            }
        }

        // Calcula o tamanho preferido do painel do tabuleiro:
        // 3 cartas na largura/altura + 2 vãos entre elas + o padding da EmptyBorder
        int numGaps = 2; // Para 3 colunas/linhas, há 2 vãos entre elas
        int totalOuterPaddingHorizontal = boardOuterPadding * 2; //
        int totalOuterPaddingVertical = boardOuterPadding * 2;   //

        int boardPrefWidth = (CARD_WIDTH * 3) + (BOARD_GRID_GAP * numGaps) + totalOuterPaddingHorizontal;  //
        int boardPrefHeight = (CARD_HEIGHT * 3) + (BOARD_GRID_GAP * numGaps) + totalOuterPaddingVertical;  //
        boardDisplayPanel.setPreferredSize(new Dimension(boardPrefWidth, boardPrefHeight)); //
        return boardDisplayPanel;
    }

    private JPanel createSideTrianglePanel(String title, List<Carta> cards, boolean showFaceDown) {
        JPanel mainSidePanel = new JPanel(new BorderLayout(5, 5)); //
        mainSidePanel.setOpaque(false); //
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(title)); //

        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT); //

        JPanel triangleContainer = new JPanel(); //
        triangleContainer.setLayout(new BoxLayout(triangleContainer, BoxLayout.Y_AXIS)); //
        triangleContainer.setOpaque(false); //

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));  //
        topRow.setOpaque(false); //
        Carta card1 = (cards != null && !cards.isEmpty()) ? cards.get(0) : null; //
        topRow.add(new GameCardPanel(card1, cardDimension, showFaceDown)); //

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 0));  // Usando DEFAULT_HGAP_HANDS para consistência
        bottomRow.setOpaque(false); //
        Carta card2 = (cards != null && cards.size() >= 2) ? cards.get(1) : null; //
        Carta card3 = (cards != null && cards.size() >= 3) ? cards.get(2) : null; //
        bottomRow.add(new GameCardPanel(card2, cardDimension, showFaceDown)); //
        bottomRow.add(new GameCardPanel(card3, cardDimension, showFaceDown)); //

        triangleContainer.add(Box.createVerticalGlue()); //
        triangleContainer.add(topRow); //
        triangleContainer.add(Box.createRigidArea(new Dimension(0, 5)));  //
        triangleContainer.add(bottomRow); //
        triangleContainer.add(Box.createVerticalGlue()); //

        mainSidePanel.add(triangleContainer, BorderLayout.CENTER); //

        int internalWidth = (CARD_WIDTH * 2) + DEFAULT_HGAP_HANDS; // 2 cartas + hgap entre elas
        int prefWidth = internalWidth + 30; //

        int prefHeight = (CARD_HEIGHT * 2) + 5 + 40;  //
        mainSidePanel.setPreferredSize(new Dimension(prefWidth, prefHeight)); //

        return mainSidePanel;
    }
}