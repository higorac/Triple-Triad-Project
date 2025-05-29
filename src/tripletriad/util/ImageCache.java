package tripletriad.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    // O cache para armazenar as imagens. A chave pode ser o caminho do recurso da imagem.
    private static Map<String, Image> cache = new HashMap<>();

    // Imagem de fundo padrão, carregada uma vez
    private static Image defaultCardBackground = null;

    // Método para obter uma imagem do cache ou carregá-la se não existir
    public static Image getImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return null;
        }

        // Verifica se a imagem já está no cache
        if (cache.containsKey(resourcePath)) {
            return cache.get(resourcePath);
        }

        // Se não estiver no cache, carrega a imagem
        try {
            URL imageUrl = ImageCache.class.getResource(resourcePath);
            if (imageUrl != null) {
                Image image = ImageIO.read(imageUrl);
                cache.put(resourcePath, image); // Adiciona ao cache
                return image;
            } else {
                System.err.println("Recurso de imagem não encontrado no cache: " + resourcePath);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem do recurso '" + resourcePath + "': " + e.getMessage());
            return null;
        }
    }

    // Método específico para obter a imagem de fundo da carta, garantindo que seja carregada apenas uma vez
    public static Image getCardBackgroundImage() {
        if (defaultCardBackground == null) {
            defaultCardBackground = getImage("/resources/images/card_art/card_bg.png");
        }
        return defaultCardBackground;
    }
}
