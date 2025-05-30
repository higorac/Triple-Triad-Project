package tripletriad.gui;

import tripletriad.app.Main;
import tripletriad.controller.Jogo;
import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;
import tripletriad.util.SoundManager;
import tripletriad.util.SoundEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A janela principal (JFrame) da interface gráfica do usuário para uma instância do jogo Triple Triad.
 * No design atual, cada jogador tem sua própria instância desta GUI, permitindo uma visualização
 * individualizada do jogo.
 *
 * Esta classe é responsável por:
 * - Exibir o estado atual do jogo (tabuleiro, mãos dos jogadores, placar, turno).
 * - Capturar as interações do usuário (cliques em cartas na mão e em slots do tabuleiro).
 * - Comunicar essas interações ao controlador do jogo (`Jogo`).
 * - Atualizar sua exibição quando notificada de mudanças no estado do jogo pelo controlador (padrão Observer).
 * - Lidar com o fluxo de fim de jogo e solicitação de reinício.
 */

public class TripleTriadGUI extends JFrame {

    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140;
    private static final int DEFAULT_HGAP_HANDS = 10;
    private static final int BOARD_GRID_GAP = 5;

    private Jogador jogador;
    private Jogo jogo;
    private SoundManager soundManager;

    private Carta selectedCardFromHand = null;
    private boolean alreadyShownWinner = false;

    private BackgroundPanel backgroundPanel;
    private JScrollPane playerScrollPane;
    private JScrollPane opponentHiddenScrollPane;
    private JPanel gameBoardDisplayPanel;
    private JPanel leftOpponentCardsPanel;
    private JPanel rightOpponentReservePanel;
    private JPanel northPanelContainer;
    private JLabel lblPlayer1Score;
    private JLabel lblPlayer2Score;
    private JLabel lblTurnIndicator;

    private Runnable onRestartRequestCallback;


    public TripleTriadGUI(Jogador jogador, Jogo jogo, Runnable onRestartRequest) {
        this.jogador = jogador;
        this.jogo = jogo;
        this.soundManager = SoundManager.getInstance();
        this.onRestartRequestCallback = onRestartRequest;

        if (this.jogo != null) {
            this.jogo.addObserver(this);
        } else {
            System.err.println("CRÍTICO: Instância de Jogo é nula na criação de TripleTriadGUI para " +
                    (this.jogador != null ? this.jogador.getNome() : "Jogador Desconhecido"));
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        TripleTriadGUI.this,
                        "Tem certeza que deseja sair do Triple Triad?",
                        "Confirmar Saída",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (TripleTriadGUI.this.jogo != null) {
                    TripleTriadGUI.this.jogo.removeObserver(TripleTriadGUI.this);
                }
            }
        });

        this.backgroundPanel = new BackgroundPanel("/resources/images/back.png");
        this.backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(this.backgroundPanel);

        initializePanels();
        refreshStatusDisplay();

        setPreferredSize(new Dimension(880, 860));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void exibirMensagemAguardandoOponente(boolean aguardando, String nomeOponente) {
        SwingUtilities.invokeLater(() -> {
            if (lblTurnIndicator != null) {
                if (aguardando) {
                    lblTurnIndicator.setText("Você quer jogar novamente. Aguardando " + nomeOponente + "...");
                    lblTurnIndicator.setForeground(Color.ORANGE);
                }
            }
        });
    }

    private void initializePanels() {
        if (jogo == null) {
            System.err.println("Não é possível inicializar painéis: instância de Jogo é nula.");
            lblPlayer1Score = new JLabel("P1: -");
            lblPlayer2Score = new JLabel("P2: -");
            lblTurnIndicator = new JLabel("Erro no jogo");
            return;
        }
        Jogador oponente = (jogo.getJogador1() == jogador) ? jogo.getJogador2() : jogo.getJogador1();
        String opponentName = (oponente != null) ? oponente.getNome() : "Oponente";

        JPanel statusDisplayPanel = new JPanel(new BorderLayout(20, 0));
        statusDisplayPanel.setOpaque(false);
        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        lblPlayer1Score = new JLabel();
        lblPlayer1Score.setForeground(Color.WHITE);
        lblPlayer1Score.setFont(new Font("Arial", Font.BOLD, 14));
        lblPlayer2Score = new JLabel();
        lblPlayer2Score.setForeground(Color.WHITE);
        lblPlayer2Score.setFont(new Font("Arial", Font.BOLD, 14));
        scorePanel.add(lblPlayer1Score);
        scorePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        scorePanel.add(lblPlayer2Score);
        statusDisplayPanel.add(scorePanel, BorderLayout.WEST);
        lblTurnIndicator = new JLabel(" ");
        lblTurnIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        lblTurnIndicator.setFont(new Font("Arial", Font.BOLD, 16));
        statusDisplayPanel.add(lblTurnIndicator, BorderLayout.CENTER);

        this.northPanelContainer = new JPanel(new BorderLayout(0, 5));
        this.northPanelContainer.setOpaque(false);
        this.northPanelContainer.add(statusDisplayPanel, BorderLayout.NORTH);
        int opponentHandSize = (oponente != null && oponente.getCartasNaMao() != null) ? 5 : 0;
        JPanel opponentHiddenActualPanel = createFaceDownCardsPanel(opponentHandSize);
        this.opponentHiddenScrollPane = new JScrollPane(opponentHiddenActualPanel);
        configureScrollPane(this.opponentHiddenScrollPane, "Mão do Oponente (Oculta)");
        this.northPanelContainer.add(this.opponentHiddenScrollPane, BorderLayout.CENTER);

        List<Carta> maoAtualJogador = (this.jogador != null && this.jogador.getCartasNaMao() != null) ? this.jogador.getCartasNaMao() : Collections.emptyList();
        JPanel playerHandActualPanel = createPlayerHandDisplayPanel(maoAtualJogador);
        this.playerScrollPane = new JScrollPane(playerHandActualPanel);
        configureScrollPane(this.playerScrollPane, "Cartas de " + (this.jogador != null ? this.jogador.getNome(): "Jogador"));

        this.gameBoardDisplayPanel = createGameBoardDisplayPanel();

        List<Carta> opponentVisibleCardsList = new ArrayList<>();
        if (oponente != null && this.jogador != null) {
            List<Carta> initiallyRevealed = jogo.getReveladasDoOponenteParaJogador(this.jogador);
            List<Carta> opponentCurrentHand = oponente.getCartasNaMao();
            if (initiallyRevealed != null && opponentCurrentHand != null) {
                for (Carta revealedCard : initiallyRevealed) {
                    if (opponentCurrentHand.contains(revealedCard)) {
                        opponentVisibleCardsList.add(revealedCard);
                    }
                }
            }
        }
        this.leftOpponentCardsPanel = createSideTrianglePanel("Cartas do Oponente", opponentVisibleCardsList, false);
        this.rightOpponentReservePanel = createSideTrianglePanel("Reserva de " + opponentName, null, true);

        this.backgroundPanel.add(this.northPanelContainer, BorderLayout.NORTH);
        this.backgroundPanel.add(this.playerScrollPane, BorderLayout.SOUTH);
        this.backgroundPanel.add(this.gameBoardDisplayPanel, BorderLayout.CENTER);
        this.backgroundPanel.add(this.leftOpponentCardsPanel, BorderLayout.WEST);
        this.backgroundPanel.add(this.rightOpponentReservePanel, BorderLayout.EAST);
    }

    private void configureScrollPane(JScrollPane scrollPane, String title) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
    }

    private JPanel createPlayerHandDisplayPanel(List<Carta> cartasNaMao) {
        JPanel handDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 10));
        handDisplayPanel.setOpaque(false);
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        int numCartasParaExibir = 5;

        if (cartasNaMao != null) {
            for (int i = 0; i < cartasNaMao.size(); i++) {
                Carta carta = cartasNaMao.get(i);
                GameCardPanel cardPanel = new GameCardPanel(carta, cardDimension, false, this, false, i, -1);
                if (carta != null && selectedCardFromHand != null && carta.equals(selectedCardFromHand)) {
                    if (this.jogador == jogo.getJogador1()) {
                        cardPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
                    } else if (this.jogador == jogo.getJogador2()) {
                        cardPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    } else {
                        cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                    }
                } else {
                    cardPanel.setBorder(null);
                }
                handDisplayPanel.add(cardPanel);
            }
        }
        int slotsPreenchidos = (cartasNaMao != null) ? cartasNaMao.size() : 0;
        for (int i = slotsPreenchidos; i < numCartasParaExibir; i++) {
            handDisplayPanel.add(new GameCardPanel(null, cardDimension, false, null, false, -1, -1));
        }
        int preferredWidth = (CARD_WIDTH * numCartasParaExibir) + (DEFAULT_HGAP_HANDS * (numCartasParaExibir + 1));
        if (numCartasParaExibir == 0) preferredWidth = DEFAULT_HGAP_HANDS * 2;
        int preferredHeight = CARD_HEIGHT + 30;
        handDisplayPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handDisplayPanel;
    }

    private JPanel createFaceDownCardsPanel(int numCards) {
        JPanel faceDownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 10));
        faceDownPanel.setOpaque(false);
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        for (int i = 0; i < numCards; i++) {
            faceDownPanel.add(new GameCardPanel(null, cardDimension, true, null, false, -1, -1));
        }
        int preferredWidth = (CARD_WIDTH * numCards) + (DEFAULT_HGAP_HANDS * (numCards + 1));
        if (numCards == 0) preferredWidth = DEFAULT_HGAP_HANDS * 2;
        int preferredHeight = CARD_HEIGHT + 30;
        faceDownPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return faceDownPanel;
    }


    private JPanel createGameBoardDisplayPanel() {
        JPanel boardDisplayPanel = new JPanel(new GridLayout(3, 3, BOARD_GRID_GAP, BOARD_GRID_GAP));
        boardDisplayPanel.setOpaque(false);
        int boardOuterPadding = 5;
        boardDisplayPanel.setBorder(BorderFactory.createEmptyBorder(boardOuterPadding, boardOuterPadding, boardOuterPadding, boardOuterPadding));
        Dimension cardSlotDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        Tabuleiro tabuleiroModel = (jogo != null) ? jogo.getTabuleiro() : null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Carta cartaNoSlot = (tabuleiroModel != null) ? tabuleiroModel.getCarta(i, j) : null;
                boardDisplayPanel.add(new GameCardPanel(cartaNoSlot, cardSlotDimension, false, this, true, i, j));
            }
        }
        int numGaps = 2;
        int totalOuterPaddingHorizontal = boardOuterPadding * 2;
        int totalOuterPaddingVertical = boardOuterPadding * 2;
        int boardPrefWidth = (CARD_WIDTH * 3) + (BOARD_GRID_GAP * numGaps) + totalOuterPaddingHorizontal;
        int boardPrefHeight = (CARD_HEIGHT * 3) + (BOARD_GRID_GAP * numGaps) + totalOuterPaddingVertical;
        boardDisplayPanel.setPreferredSize(new Dimension(boardPrefWidth, boardPrefHeight));
        return boardDisplayPanel;
    }

    private JPanel createSideTrianglePanel(String title, List<Carta> cards, boolean showFaceDown) {
        JPanel mainSidePanel = new JPanel(new BorderLayout(5, 5));
        mainSidePanel.setOpaque(false);
        mainSidePanel.setBorder(BorderFactory.createTitledBorder(title));
        Dimension cardDimension = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        JPanel triangleContainer = new JPanel();
        triangleContainer.setLayout(new BoxLayout(triangleContainer, BoxLayout.Y_AXIS));
        triangleContainer.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topRow.setOpaque(false);
        Carta card1 = (cards != null && !cards.isEmpty()) ? cards.get(0) : null;
        topRow.add(new GameCardPanel(card1, cardDimension, showFaceDown, null, false, -1, -1));

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, DEFAULT_HGAP_HANDS, 0));
        bottomRow.setOpaque(false);
        Carta card2 = (cards != null && cards.size() >= 2) ? cards.get(1) : null;
        Carta card3 = (cards != null && cards.size() >= 3) ? cards.get(2) : null;
        bottomRow.add(new GameCardPanel(card2, cardDimension, showFaceDown, null, false, -1, -1));
        bottomRow.add(new GameCardPanel(card3, cardDimension, showFaceDown, null, false, -1, -1));

        triangleContainer.add(Box.createVerticalGlue());
        triangleContainer.add(topRow);
        triangleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        triangleContainer.add(bottomRow);
        triangleContainer.add(Box.createVerticalGlue());
        mainSidePanel.add(triangleContainer, BorderLayout.CENTER);
        int internalWidth = (CARD_WIDTH * 2) + DEFAULT_HGAP_HANDS;
        int prefWidth = internalWidth + 30;
        int prefHeight = (CARD_HEIGHT * 2) + 5 + 40;
        mainSidePanel.setPreferredSize(new Dimension(prefWidth, prefHeight));
        return mainSidePanel;
    }


    public void cardInHandClicked(Carta card, int handIndex) {
        if (jogo == null || this.jogador == null || jogo.getJogadorAtual() == null) return;
        if (jogo.getJogadorAtual() == this.jogador && !jogo.jogoFinalizado()) {
            if (this.selectedCardFromHand != null && this.selectedCardFromHand.equals(card)) {
                this.selectedCardFromHand = null;
            } else {
                this.selectedCardFromHand = card;
                soundManager.playSound(SoundEffect.SELECTION);
            }
            updateGUI();
        }
    }

    public void boardSlotClicked(int row, int col) {
        if (jogo == null || this.jogador == null || jogo.getJogadorAtual() == null) return;
        if (jogo.getJogadorAtual() == this.jogador && this.selectedCardFromHand != null && !jogo.jogoFinalizado()) {
            boolean sucesso = jogo.tentarJogarCarta(row, col, selectedCardFromHand, this.jogador);
            this.selectedCardFromHand = null;

            if (!sucesso) {
                soundManager.playSound(SoundEffect.ERROR);
                JOptionPane.showMessageDialog(this, "Não é possível jogar a carta nesta posição.", "Jogada Inválida", JOptionPane.WARNING_MESSAGE);
                updateGUI();
            }
        } else if (jogo.getJogadorAtual() != this.jogador && !jogo.jogoFinalizado()) {
            soundManager.playSound(SoundEffect.ERROR);
            JOptionPane.showMessageDialog(this, "Não é a sua vez!", "Aguarde", JOptionPane.INFORMATION_MESSAGE);
        } else if (this.selectedCardFromHand == null && !jogo.jogoFinalizado()) {
            if (jogo.getTabuleiro() != null && !tabuleiroVazio(jogo.getTabuleiro())) {
                soundManager.playSound(SoundEffect.ERROR);
                JOptionPane.showMessageDialog(this, "Selecione uma carta da sua mão primeiro.", "Nenhuma Carta Selecionada", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private boolean tabuleiroVazio(Tabuleiro tab){
        if (tab == null) return true;
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                if(tab.getCarta(i,j) != null) return false;
            }
        }
        return true;
    }

    public void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            if (backgroundPanel == null || jogo == null || jogador == null) return;

            refreshStatusDisplay();

            if (playerScrollPane != null) backgroundPanel.remove(playerScrollPane);
            if (gameBoardDisplayPanel != null) backgroundPanel.remove(gameBoardDisplayPanel);
            if (leftOpponentCardsPanel != null) backgroundPanel.remove(leftOpponentCardsPanel);

            List<Carta> maoAtualJogador = (this.jogador != null && this.jogador.getCartasNaMao() != null) ? this.jogador.getCartasNaMao() : Collections.emptyList();
            JPanel newPlayerHandActualPanel = createPlayerHandDisplayPanel(maoAtualJogador);
            this.playerScrollPane = new JScrollPane(newPlayerHandActualPanel);
            configureScrollPane(this.playerScrollPane, "Cartas de " + (this.jogador != null ? this.jogador.getNome():"Jogador"));
            this.backgroundPanel.add(this.playerScrollPane, BorderLayout.SOUTH);

            this.gameBoardDisplayPanel = createGameBoardDisplayPanel();
            this.backgroundPanel.add(this.gameBoardDisplayPanel, BorderLayout.CENTER);

            Jogador oponente = (jogo.getJogador1() == this.jogador) ? jogo.getJogador2() : jogo.getJogador1();
            List<Carta> opponentVisibleCardsList = new ArrayList<>();
            if (oponente != null && this.jogador != null) {
                List<Carta> initiallyRevealed = jogo.getReveladasDoOponenteParaJogador(this.jogador);
                List<Carta> opponentCurrentHand = oponente.getCartasNaMao();
                if (initiallyRevealed != null && opponentCurrentHand != null) {
                    for (Carta revealedCard : initiallyRevealed) {
                        if (opponentCurrentHand.contains(revealedCard)) {
                            opponentVisibleCardsList.add(revealedCard);
                        }
                    }
                }
            }
            this.leftOpponentCardsPanel = createSideTrianglePanel("Cartas do Oponente", opponentVisibleCardsList, false);
            this.backgroundPanel.add(this.leftOpponentCardsPanel, BorderLayout.WEST);

            backgroundPanel.revalidate();
            backgroundPanel.repaint();

            if (jogo.jogoFinalizado() && !alreadyShownWinner) {
                alreadyShownWinner = true;
                mostrarVencedorGUI();
            }
        });
    }

    private void refreshStatusDisplay() {
        if (jogo == null || jogador == null || lblPlayer1Score == null || lblPlayer2Score == null || lblTurnIndicator == null) {
            return;
        }
        Jogador j1 = jogo.getJogador1();
        Jogador j2 = jogo.getJogador2();

        lblPlayer1Score.setText((j1 != null ? j1.getNome() : "P1") + ": " + (j1 != null ? j1.getPontuacao() : 0));
        lblPlayer2Score.setText((j2 != null ? j2.getNome() : "P2") + ": " + (j2 != null ? j2.getPontuacao() : 0));

        if (jogo.jogoFinalizado()) {
            boolean p1Espera = Main.isJogador1QuerReiniciar();
            boolean p2Espera = Main.isJogador2QuerReiniciar();

            boolean umJogadorEstaEsperando = (p1Espera && !p2Espera) || (!p1Espera && p2Espera);

            if (!umJogadorEstaEsperando) {
                lblTurnIndicator.setText("Fim de Jogo!");
                lblTurnIndicator.setForeground(Color.WHITE);
            }

            setTitle("Triple Triad - " + (this.jogador != null ? this.jogador.getNome() : "") + " (Fim de Jogo)");
        } else {
            Jogador currentPlayer = jogo.getJogadorAtual();
            if (currentPlayer != null) {
                lblTurnIndicator.setText("Vez de: " + currentPlayer.getNome());
                if (currentPlayer == jogo.getJogador1()) {
                    lblTurnIndicator.setForeground(Color.WHITE);
                } else {
                    lblTurnIndicator.setForeground(Color.WHITE);
                }
                String turnIndicatorInTitle = (currentPlayer == this.jogador) ? " (Sua Vez)" : " (Vez do Oponente)";
                setTitle("Triple Triad - " + (this.jogador != null ? this.jogador.getNome() : "") + turnIndicatorInTitle);
            } else {
                lblTurnIndicator.setText("Aguardando jogador...");
                lblTurnIndicator.setForeground(Color.YELLOW);
                setTitle("Triple Triad - " + (this.jogador != null ? this.jogador.getNome() : ""));
            }
        }
    }


    private void mostrarVencedorGUI() {
        if (jogo == null || jogo.getJogador1() == null || jogo.getJogador2() == null) {
            soundManager.playSound(SoundEffect.ERROR);
            JOptionPane.showMessageDialog(this, "Erro ao determinar vencedor: dados do jogo incompletos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SoundEffect endSound = jogo.determineEndGameSound();
        if (endSound != null) {
            soundManager.playSound(endSound);
        }

        int p1Score = jogo.getJogador1().getPontuacao();
        int p2Score = jogo.getJogador2().getPontuacao();
        String msgFinal;

        if (p1Score > p2Score) {
            msgFinal = "Vitória de " + jogo.getJogador1().getNome() + "!";
        } else if (p2Score > p1Score) {
            msgFinal = "Vitória de " + jogo.getJogador2().getNome() + "!";
        } else {
            msgFinal = "Empate!";
        }
        String placar = jogo.getJogador1().getNome() + ": " + p1Score + " pontos\n" +
                jogo.getJogador2().getNome() + ": " + p2Score + " pontos\n\n";

        Object[] options = {"Jogar Novamente", "Fechar Jogo"};
        int choice = JOptionPane.showOptionDialog(this,
                placar + msgFinal,
                "Fim de Jogo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            if (onRestartRequestCallback != null) {
                onRestartRequestCallback.run();
            }
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public Jogo getJogo() {
        return this.jogo;
    }
}