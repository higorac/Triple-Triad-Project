package tripletriad.gui;

import tripletriad.model.Carta;
// import tripletriad.model.Jogador; // Removido se não usado diretamente aqui

import javax.swing.*;
import java.awt.*;
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

    public GameCardPanel(Carta carta, Dimension preferredSize, boolean isFaceDown) {
        this.carta = carta;
        this.isFaceDown = isFaceDown;
        setPreferredSize(preferredSize);
        setOpaque(false);
        loadImages();
    }

    public GameCardPanel(Carta carta, Dimension preferredSize) {
        this(carta, preferredSize, false);
    }

    private String formatCardId(int id) {
        return String.format("%03d", id);
    }

    private void loadImages() {
        try {
            URL bgUrl = getClass().getResource("/resources/images/card_art/card_bg.png");
            if (bgUrl != null) {
                cardBackgroundImage = ImageIO.read(bgUrl);
            } else {
                System.err.println("Imagem de fundo da carta não encontrada: /resources/images/card_art/card_bg.png");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem de fundo da carta: " + e.getMessage());
        }

        // Só carrega imagem do monstro se a carta existir E estiver virada para cima
        if (this.carta != null && !this.isFaceDown) {
            try {
                String monsterImagePath = "/resources/images/card_art/monsters/" + formatCardId(this.carta.getId()) + ".png";
                URL monsterUrl = getClass().getResource(monsterImagePath);
                if (monsterUrl != null) {
                    monsterImage = ImageIO.read(monsterUrl);
                } else {
                    System.err.println("Imagem do monstro não encontrada: " + monsterImagePath);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar imagem do monstro para ID " + this.carta.getId() + ": " + e.getMessage());
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
        int nomeAltura = 20; // Espaço reservado para o nome da carta
        int cartaAltura = height - nomeAltura;

        if (this.isFaceDown) {
            if (cardBackgroundImage != null) {
                g2d.drawImage(cardBackgroundImage, 0, 0, width, cartaAltura, this);
            } else {
                g2d.setColor(new Color(0, 0, 150));
                g2d.fillRect(0, 0, width, cartaAltura);
                g2d.setColor(Color.WHITE);
                g2d.drawRect(0, 0, width - 1, cartaAltura - 1);
                FontMetrics fm = g2d.getFontMetrics();
                String backText = "TT";
                g2d.drawString(backText, (width - fm.stringWidth(backText)) / 2, (cartaAltura - fm.getHeight()) / 2 + fm.getAscent());
            }

            g2d.dispose();
            return;
        }

        if (this.carta == null) {
            g2d.setColor(new Color(200, 200, 200, 100));
            g2d.fillRect(0, 0, width, cartaAltura);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(0, 0, width - 1, cartaAltura - 1);
            g2d.drawString("Vazio", width / 2 - 15, cartaAltura / 2);
            g2d.dispose();
            return;
        }

        if (cardBackgroundImage != null) {
            g2d.drawImage(cardBackgroundImage, 0, 0, width, cartaAltura, this);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, width, cartaAltura);
        }

        if (monsterImage != null) {
            int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
            int monsterY = (cartaAltura - MONSTER_IMAGE_HEIGHT) / 2;
            g2d.drawImage(monsterImage, monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT, this);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
            int monsterY = (cartaAltura - MONSTER_IMAGE_HEIGHT) / 2;
            g2d.fillRect(monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString("?", monsterX + MONSTER_IMAGE_WIDTH / 2 - 3, monsterY + MONSTER_IMAGE_HEIGHT / 2 + 5);
        }


        g2d.setFont(new Font("Arial", Font.BOLD, RANK_FONT_SIZE));
        g2d.setColor(RANK_COLOR);

        String rankTopo = carta.verificandoPontosCartas(carta.getTopo());
        String rankBaixo = carta.verificandoPontosCartas(carta.getBaixo());
        String rankEsq = carta.verificandoPontosCartas(carta.getEsquerda());
        String rankDir = carta.verificandoPontosCartas(carta.getDireita());

        FontMetrics fmRank = g2d.getFontMetrics();
        int margin = 5;
        g2d.drawString(rankTopo, width / 2 - fmRank.stringWidth(rankTopo) / 2, margin + fmRank.getAscent());
        g2d.drawString(rankBaixo, width / 2 - fmRank.stringWidth(rankBaixo) / 2, cartaAltura - margin - fmRank.getDescent() + fmRank.getAscent() / 2);
        g2d.drawString(rankEsq, margin, cartaAltura / 2 + fmRank.getAscent() / 2);
        g2d.drawString(rankDir, width - margin - fmRank.stringWidth(rankDir), cartaAltura / 2 + fmRank.getAscent() / 2);

        // Nome da carta FORA da carta
        g2d.setFont(new Font("Arial", Font.BOLD, NAME_FONT_SIZE));
        g2d.setColor(NAME_COLOR);
        FontMetrics fmName = g2d.getFontMetrics();
        String nomeCarta = carta.getNome();
        int nomeWidth = fmName.stringWidth(nomeCarta);
        int nomeX = width / 2 - nomeWidth / 2;
        int nomeY = height - 5;
        g2d.drawString(nomeCarta, nomeX, nomeY);

        g2d.dispose();
    }

}