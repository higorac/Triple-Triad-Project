package tripletriad.gui; // Ou o pacote onde você quer colocar esta classe

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        // Tenta carregar a imagem do classpath (deve estar na pasta resources)
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                System.err.println("Recurso de imagem não encontrado: " + imagePath);
                // Define uma cor de fundo como fallback se a imagem não for encontrada
                setBackground(Color.DARK_GRAY); // Exemplo de cor de fallback
            } else {
                backgroundImage = ImageIO.read(imageUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a imagem de fundo: " + imagePath);
            setBackground(Color.DARK_GRAY); // Exemplo de cor de fallback em caso de erro
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