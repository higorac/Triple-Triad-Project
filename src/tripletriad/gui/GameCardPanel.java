package tripletriad.gui;

import tripletriad.model.Carta;
import tripletriad.model.Jogador; // Necessário para comparar dono
import tripletriad.controller.Jogo; // Necessário para pegar J1 e J2
import tripletriad.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class GameCardPanel extends JPanel {

    private Carta carta;
    private Image cardBackgroundImage;
    private Image monsterImage;
    private boolean isFaceDown;

    private static final int MONSTER_IMAGE_WIDTH = 60;
    private static final int MONSTER_IMAGE_HEIGHT = 60;
    private static final int RANK_FONT_SIZE = 16;
    private static final int NAME_FONT_SIZE = 15;
    private static final Color RANK_COLOR = Color.WHITE;
    private static final Color NAME_COLOR = Color.WHITE;

    private TripleTriadGUI guiInstance;
    private boolean isBoardSlot;
    private int boardRow = -1, boardCol = -1;
    private int handIndex = -1;

    public GameCardPanel(Carta carta, Dimension preferredSize, boolean isFaceDown,
                         TripleTriadGUI gui, boolean isBoardSlot, int id1, int id2) {
        this.carta = carta;
        this.isFaceDown = isFaceDown;
        this.guiInstance = gui;
        this.isBoardSlot = isBoardSlot;

        if (isBoardSlot) {
            this.boardRow = id1;
            this.boardCol = id2;
        } else {
            this.handIndex = id1;
        }

        setPreferredSize(preferredSize);
        setOpaque(false);
        loadImages(); // Carrega imagens baseadas na carta inicial (pode ser null)

        if (this.guiInstance != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (GameCardPanel.this.isBoardSlot) {
                        guiInstance.boardSlotClicked(boardRow, boardCol);
                    } else {
                        if (!GameCardPanel.this.isFaceDown && GameCardPanel.this.carta != null) {
                            guiInstance.cardInHandClicked(GameCardPanel.this.carta, handIndex);
                        }
                    }
                }
            });
        }
    }

    public GameCardPanel(Carta carta, Dimension preferredSize, boolean isFaceDown) {
        this(carta, preferredSize, isFaceDown, null, false, -1, -1);
    }

    // MÉTODO MOVIDO PARA FORA E CORRIGIDO
    public void definirCarta(Carta novaCarta) {
        this.carta = novaCarta;
        loadImages(); // Sempre chama loadImages; o cache cuidará da eficiência.
        repaint();
    }

    // MÉTODO MOVIDO PARA FORA E CORRIGIDO
    // O nome do parâmetro 'jogadorDestaGui' está correto aqui se você o usa com esse nome dentro do método.
    // A classe TripleTriadGUI passa 'this.jogador' para este parâmetro.
    public void definirDestaqueSelecao(boolean selecionado, Jogador jogadorDestaGui, Jogo jogo) {
        if (selecionado && this.carta != null && jogadorDestaGui != null && jogo != null && jogo.getJogador1() != null && jogo.getJogador2() != null) {
            if (jogadorDestaGui == jogo.getJogador1()) {
                setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
            } else if (jogadorDestaGui == jogo.getJogador2()) {
                setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            } else {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3)); // Fallback
            }
        } else {
            setBorder(null); // Remove borda se não selecionado
        }
        repaint(); // Pede repaint para mostrar/remover a borda
    }


    private String formatCardId(int id) {
        return String.format("%03d", id);
    }

    private void loadImages() {
        // Carrega a imagem de fundo da carta usando o cache
        this.cardBackgroundImage = ImageCache.getCardBackgroundImage();
        if (this.cardBackgroundImage == null) {
            System.err.println("Falha ao carregar imagem de fundo da carta do cache.");
            // Você pode definir uma cor de fundo sólida como fallback aqui se desejar
        }

        // Reseta a imagem do monstro antes de tentar carregar uma nova
        this.monsterImage = null;
        if (this.carta != null && !this.isFaceDown) {
            String monsterImagePath = "/resources/images/card_art/monsters/" + formatCardId(this.carta.getId()) + ".png";
            this.monsterImage = ImageCache.getImage(monsterImagePath);
            if (this.monsterImage == null) {
                System.err.println("Falha ao carregar imagem do monstro '" + monsterImagePath + "' do cache para ID " + this.carta.getId());
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = getWidth();
        int height = getHeight();
        int nomeAltura = 20;
        int cartaAlturaReal = height - nomeAltura;

        if (this.isFaceDown) {
            if (cardBackgroundImage != null) {
                g2d.drawImage(cardBackgroundImage, 0, 0, width, cartaAlturaReal, this);
            } else {
                g2d.setColor(new Color(0, 0, 150));
                g2d.fillRect(0, 0, width, cartaAlturaReal);
                g2d.setColor(Color.WHITE);
                g2d.drawRect(0, 0, width - 1, cartaAlturaReal - 1);
                FontMetrics fm = g2d.getFontMetrics();
                String backText = "TT";
                g2d.drawString(backText, (width - fm.stringWidth(backText)) / 2, (cartaAlturaReal - fm.getHeight()) / 2 + fm.getAscent());
            }
        } else if (this.carta == null) {
            g2d.setColor(new Color(200, 200, 200, 100));
            g2d.fillRect(0, 0, width, cartaAlturaReal);
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String emptyText = "Vazio";
            g2d.drawString(emptyText, (width - fm.stringWidth(emptyText)) / 2, (cartaAlturaReal - fm.getHeight()) / 2 + fm.getAscent());
        } else {
            if (cardBackgroundImage != null) {
                g2d.drawImage(cardBackgroundImage, 0, 0, width, cartaAlturaReal, this);
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(0, 0, width, cartaAlturaReal);
            }

            if (isBoardSlot && this.carta.getDono() != null && guiInstance != null) {
                Jogo jogoAtual = guiInstance.getJogo();
                if (jogoAtual != null && jogoAtual.getJogador1() != null && jogoAtual.getJogador2() != null) { // Checagem adicional
                    Jogador dono = this.carta.getDono();
                    Color ownerTint = null;
                    if (dono.equals(jogoAtual.getJogador1())) { // Use .equals() para comparar objetos
                        ownerTint = new Color(0, 100, 255, 70);
                    } else if (dono.equals(jogoAtual.getJogador2())) {
                        ownerTint = new Color(255, 50, 50, 70);
                    }
                    if (ownerTint != null) {
                        g2d.setColor(ownerTint);
                        g2d.fillRect(1, 1, width - 2, cartaAlturaReal - 2);
                    }
                }
            }

            if (monsterImage != null) {
                int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
                int monsterY = (cartaAlturaReal - MONSTER_IMAGE_HEIGHT) / 2;
                g2d.drawImage(monsterImage, monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT, this);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
                int monsterY = (cartaAlturaReal - MONSTER_IMAGE_HEIGHT) / 2;
                g2d.fillRect(monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT);
                g2d.setColor(Color.BLACK);
                g2d.drawString("?", monsterX + MONSTER_IMAGE_WIDTH / 2 - 3, monsterY + MONSTER_IMAGE_HEIGHT / 2 + 5);
            }

            g2d.setFont(new Font("Arial", Font.BOLD, RANK_FONT_SIZE));
            g2d.setColor(RANK_COLOR);
            // ... (desenho dos ranks, como antes) ...
            String rankTopo = carta.verificandoPontosCartas(carta.getTopo());
            String rankBaixo = carta.verificandoPontosCartas(carta.getBaixo());
            String rankEsq = carta.verificandoPontosCartas(carta.getEsquerda());
            String rankDir = carta.verificandoPontosCartas(carta.getDireita());
            FontMetrics fmRank = g2d.getFontMetrics();
            int margin = 5;
            g2d.drawString(rankTopo, width / 2 - fmRank.stringWidth(rankTopo) / 2, margin + fmRank.getAscent());
            g2d.drawString(rankBaixo, width / 2 - fmRank.stringWidth(rankBaixo) / 2, cartaAlturaReal - margin);
            g2d.drawString(rankEsq, margin, cartaAlturaReal / 2 + fmRank.getAscent() / 2);
            g2d.drawString(rankDir, width - margin - fmRank.stringWidth(rankDir), cartaAlturaReal / 2 + fmRank.getAscent() / 2);
        }

        if (this.carta != null && !this.isFaceDown) {
            g2d.setFont(new Font("Arial", Font.BOLD, NAME_FONT_SIZE));
            g2d.setColor(NAME_COLOR);
            FontMetrics fmName = g2d.getFontMetrics();
            String nomeCarta = carta.getNome();
            int nomeWidth = fmName.stringWidth(nomeCarta);
            g2d.drawString(nomeCarta, (width - nomeWidth) / 2, height - fmName.getDescent() - 2);
        }
        g2d.dispose();
    }
}