package tripletriad.gui;

import tripletriad.model.Carta;
import tripletriad.model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class GameCardPanel extends JPanel {

    private Carta carta;
    private Image cardBackgroundImage;
    private Image monsterImage;

    private static final int MONSTER_IMAGE_WIDTH = 60; // Ajuste conforme necessário
    private static final int MONSTER_IMAGE_HEIGHT = 60; // Ajuste conforme necessário
    private static final int RANK_FONT_SIZE = 16;
    private static final int NAME_FONT_SIZE = 10;
    private static final Color RANK_COLOR = Color.WHITE;
    private static final Color NAME_COLOR = Color.CYAN;


    public GameCardPanel(Carta carta, Dimension preferredSize) {
        this.carta = carta;
        setPreferredSize(preferredSize);
        setOpaque(false); // O painel em si é transparente, o desenho cuidará do fundo

        loadImages();
    }

    private void loadImages() {
        // Carregar imagem de fundo da carta
        try {
            URL bgUrl = getClass().getResource("/images/card_art/card_bg.png");
            if (bgUrl != null) {
                cardBackgroundImage = ImageIO.read(bgUrl);
            } else {
                System.err.println("Imagem de fundo da carta não encontrada: /images/card_art/card_bg.png");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem de fundo da carta: " + e.getMessage());
        }

        // Carregar imagem do monstro, se a carta não for nula
        if (this.carta != null) {
            try {
                String monsterImagePath = "/images/card_art/monsters/" + this.carta.getId() + ".png";
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
        Graphics2D g2d = (Graphics2D) g.create(); // Usar Graphics2D para melhor controle (antialiasing)

        // Habilitar antialiasing para texto e formas
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);


        int width = getWidth();
        int height = getHeight();

        // 1. Desenhar cor de fundo baseada no dono (se a carta existir)
        if (carta != null && carta.getDono() != null) {
            Jogador dono = carta.getDono();
            // Precisamos de uma referência ao jogador da GUI atual para comparar.
            // Isso é um pouco complexo de passar diretamente para cá de forma limpa.
            // Por agora, vamos apenas usar uma cor padrão se tiver dono, ou outra se não tiver.
            // Ou, podemos basear a cor no 'tipo' de jogador (jogador1 vs jogador2).
            // Uma solução melhor seria ter uma propriedade no GameCardPanel como 'isPlayerOwned'.
            // Por simplicidade, vamos omitir a cor de fundo baseada no dono por enquanto
            // e focar no card_bg.png.
        } else if (carta == null) {
            // Slot vazio - poderia desenhar uma borda ou um placeholder
            g2d.setColor(new Color(200, 200, 200, 100)); // Cinza semi-transparente para slot vazio
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(0,0, width-1, height-1);
            g2d.drawString("Vazio", width / 2 - 15, height / 2);
            g2d.dispose();
            return; // Não desenha mais nada para slot vazio
        }


        // 2. Desenhar imagem de fundo da carta
        if (cardBackgroundImage != null) {
            g2d.drawImage(cardBackgroundImage, 0, 0, width, height, this);
        } else {
            // Fallback se o fundo da carta não carregar
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, width, height);
        }

        // Se não houver carta, não desenha monstro nem ranks
        if (carta == null) {
            g2d.dispose();
            return;
        }

        // 3. Desenhar imagem do monstro (centralizada)
        if (monsterImage != null) {
            int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
            int monsterY = (height - MONSTER_IMAGE_HEIGHT) / 2 - (NAME_FONT_SIZE + 5) ; // Um pouco acima para dar espaço ao nome
            g2d.drawImage(monsterImage, monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT, this);
        } else {
            // Fallback se a imagem do monstro não carregar - desenhar um placeholder ou nome
            g2d.setColor(Color.LIGHT_GRAY);
            int monsterX = (width - MONSTER_IMAGE_WIDTH) / 2;
            int monsterY = (height - MONSTER_IMAGE_HEIGHT) / 2 - (NAME_FONT_SIZE + 5);
            g2d.fillRect(monsterX, monsterY, MONSTER_IMAGE_WIDTH, MONSTER_IMAGE_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString("?", monsterX + MONSTER_IMAGE_WIDTH/2 -3, monsterY + MONSTER_IMAGE_HEIGHT/2 + 5);
        }

        // 4. Desenhar Ranks e Nome
        g2d.setFont(new Font("Arial", Font.BOLD, RANK_FONT_SIZE));
        g2d.setColor(RANK_COLOR);

        String rankTopo = carta.verificandoPontosCartas(carta.getTopo());
        String rankBaixo = carta.verificandoPontosCartas(carta.getBaixo());
        String rankEsq = carta.verificandoPontosCartas(carta.getEsquerda());
        String rankDir = carta.verificandoPontosCartas(carta.getDireita());

        FontMetrics fmRank = g2d.getFontMetrics();
        int charWidthRank = fmRank.stringWidth("A"); // Largura de um caractere de rank

        // Posições dos ranks (ajuste fino pode ser necessário)
        int margin = 5;
        g2d.drawString(rankTopo, width / 2 - fmRank.stringWidth(rankTopo) / 2, margin + fmRank.getAscent());
        g2d.drawString(rankBaixo, width / 2 - fmRank.stringWidth(rankBaixo) / 2, height - margin - fmRank.getDescent() + fmRank.getAscent()/2); // Pequeno ajuste para baixo
        g2d.drawString(rankEsq, margin, height / 2 + fmRank.getAscent() / 2);
        g2d.drawString(rankDir, width - margin - fmRank.stringWidth(rankDir), height / 2 + fmRank.getAscent() / 2);

        // Nome da carta
        g2d.setFont(new Font("Arial", Font.BOLD, NAME_FONT_SIZE));
        g2d.setColor(NAME_COLOR);
        FontMetrics fmName = g2d.getFontMetrics();
        String nomeCarta = carta.getNome();
        int nomeWidth = fmName.stringWidth(nomeCarta);
        // Centralizar o nome na parte inferior, acima do rank de baixo
        g2d.drawString(nomeCarta, width / 2 - nomeWidth / 2, height - margin - fmRank.getHeight() - fmName.getDescent());


        g2d.dispose();
    }
}