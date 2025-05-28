package tripletriad.gui; // Ou o pacote onde você quer colocar esta classe

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) { // imagePath will be "/resources/images/back.png"
        // Tenta carregar a imagem do classpath (deve estar na pasta resources)
        try {
            URL imageUrl = getClass().getResource(imagePath); // Use the provided imagePath directly
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
        super.paintComponent(g); // Importante para limpar o painel antes de desenhar
        if (backgroundImage != null) {
            // Desenha a imagem para cobrir todo o painel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}