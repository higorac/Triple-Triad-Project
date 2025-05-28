package tripletriad.gui;

import tripletriad.controller.Jogo;
import tripletriad.model.Jogador;

import javax.swing.*;
import java.awt.*;

public class TripleTriadGUI extends JFrame {

    private static final int CARD_WIDTH = 90;
    private static final int CARD_HEIGHT = 120;

    private Jogador jogador;
    private Jogo jogo;

    public TripleTriadGUI(Jogador jogador, Jogo jogo) {
        this.jogador = jogador;
        this.jogo = jogo;

        setTitle("Triple Triad - " + jogador.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Crie o BackgroundPanel com o caminho para sua imagem
        // Lembre-se que o caminho é relativo à pasta 'resources'
        BackgroundPanel backgroundPanel = new BackgroundPanel("/resources/images/back.png"); // <-- USE O NOME DA SUA IMAGEM
        backgroundPanel.setLayout(new BorderLayout(10, 10)); // Define o layout para o painel de fundo

        // 2. Defina o backgroundPanel como o contentPane do JFrame
        setContentPane(backgroundPanel);

        // --- Mão do Jogador Principal (Embaixo) ---
        JPanel playerHandContent = createHandContentPanel(5);
        // Para que o fundo apareça através do JScrollPane e seu conteúdo,
        // os painéis internos podem precisar ser transparentes.
        playerHandContent.setOpaque(false); // Torna o painel de conteúdo da mão transparente
        JScrollPane playerScrollPane = new JScrollPane(playerHandContent);
        playerScrollPane.setOpaque(false); // Torna o JScrollPane transparente
        playerScrollPane.getViewport().setOpaque(false); // Torna o viewport do JScrollPane transparente
        playerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        playerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        playerScrollPane.setBorder(BorderFactory.createTitledBorder("Cartas de " + jogador.getNome()));

        // --- Mão do Oponente (Em Cima) ---
        JPanel opponentHandContent = createHandContentPanel(5);
        opponentHandContent.setOpaque(false); // Transparência
        JScrollPane opponentScrollPane = new JScrollPane(opponentHandContent);
        opponentScrollPane.setOpaque(false); // Transparência
        opponentScrollPane.getViewport().setOpaque(false); // Transparência
        opponentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        opponentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        opponentScrollPane.setBorder(BorderFactory.createTitledBorder("Cartas do Oponente"));


        // --- Painel do Tabuleiro ---
        JPanel gameBoardPanel = createGameBoardPanel();
        gameBoardPanel.setOpaque(false); // Transparência para o painel do tabuleiro em si

        // --- Painéis Laterais ---
        JPanel leftPanel = createSideTrianglePanel("Lado Esquerdo", true);
        leftPanel.setOpaque(false); // Transparência
        JPanel rightPanel = createSideTrianglePanel("Lado Direito", false);
        rightPanel.setOpaque(false); // Transparência


        // 3. Adicione os componentes ao backgroundPanel (que agora é o contentPane)
        backgroundPanel.add(opponentScrollPane, BorderLayout.NORTH);
        backgroundPanel.add(playerScrollPane, BorderLayout.SOUTH);
        backgroundPanel.add(gameBoardPanel, BorderLayout.CENTER); // Alterado de createGameBoardPanel() para a variável
        backgroundPanel.add(leftPanel, BorderLayout.WEST);     // Alterado de createSideTrianglePanel(...) para a variável
        backgroundPanel.add(rightPanel, BorderLayout.EAST);    // Alterado de createSideTrianglePanel(...) para a variável


        setPreferredSize(new Dimension(1024, 768));
        pack();
        setLocationRelativeTo(null);
        // setVisible(true); // É melhor chamar setVisible no método main após criar a instância
    }

    private JPanel createCardPlaceholder(String label) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // Se quiser que os placeholders sejam transparentes para ver o fundo através deles:
        // cardPanel.setOpaque(false);
        // Se quiser que eles tenham uma cor de fundo semi-transparente:
        cardPanel.setBackground(new Color(211, 211, 211, 150)); // Light gray com alpha (semi-transparente)
        // Ou mantenha opaco com uma cor sólida:
        // cardPanel.setBackground(Color.LIGHT_GRAY);

        JLabel cardLabel = new JLabel(label, SwingConstants.CENTER);
        cardPanel.add(cardLabel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createHandContentPanel(int cardCount) {
        JPanel handContentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handContentPanel.setOpaque(false); // Mãozinhas transparentes
        for (int i = 0; i < cardCount; i++) {
            handContentPanel.add(createCardPlaceholder("Carta"));
        }
        int preferredWidth = (CARD_WIDTH + 10) * cardCount + 10;
        int preferredHeight = CARD_HEIGHT + 20; // Altura um pouco maior para o título do JScrollPane
        handContentPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handContentPanel;
    }

    private JPanel createGameBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setOpaque(false); // Painel do grid transparente
        boardPanel.setBorder(BorderFactory.createTitledBorder("Tabuleiro"));
        for (int i = 0; i < 9; i++) {
            JPanel placeholder = createCardPlaceholder("Slot");
            // placeholder.setOpaque(false); // Se quiser que os slots individuais sejam transparentes
            boardPanel.add(placeholder);
        }
        return boardPanel;
    }

    private JPanel createSideTrianglePanel(String title, boolean isLeft) {
        JPanel sidePanel = new JPanel(new BorderLayout(10, 10));
        sidePanel.setOpaque(false); // Painel lateral principal transparente

        JPanel peakPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        peakPanel.setOpaque(false);
        peakPanel.add(createCardPlaceholder("Carta"));

        JPanel basePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        basePanel.setOpaque(false);
        basePanel.add(createCardPlaceholder("Carta"));
        basePanel.add(createCardPlaceholder("Carta"));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(peakPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(basePanel);
        contentPanel.add(Box.createVerticalGlue());

        sidePanel.add(contentPanel, BorderLayout.CENTER);
        sidePanel.setBorder(BorderFactory.createTitledBorder(title));

        int preferredWidth = CARD_WIDTH * 2 + 20 + (2 * 10); // Ajuste conforme necessário
        int preferredHeight = CARD_HEIGHT * 2 + 10 + (2 * 10); // Ajuste conforme necessário
        sidePanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        return sidePanel;
    }
}