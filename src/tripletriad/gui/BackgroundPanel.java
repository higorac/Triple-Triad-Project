package tripletriad.gui; // Ou o pacote onde você quer colocar esta classe

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

/**
 * Um JPanel customizado que desenha uma imagem de fundo.
 * A imagem é carregada a partir de um caminho de recurso especificado
 * e é redimensionada para preencher todo o painel.
 */

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("Recurso de imagem não encontrado: " + imagePath);
                setBackground(Color.DARK_GRAY);
            } else {
                backgroundImage = ImageIO.read(imageUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a imagem de fundo: " + imagePath);
            setBackground(Color.DARK_GRAY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}