package tripletriad.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia um cache de imagens para otimizar o desempenho, evitando
 * carregamentos repetidos de imagens do disco.
 * Utiliza um HashMap para armazenar as instâncias de Image já carregadas,
 * usando o caminho do recurso da imagem como chave.
 * Esta classe segue um padrão de utilidade estática.
 */

public class ImageCache {
    private static Map<String, Image> cache = new HashMap<>();

    // Imagem de fundo padrão
    private static Image defaultCardBackground = null;

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

    public static Image getCardBackgroundImage() {
        if (defaultCardBackground == null) {
            defaultCardBackground = getImage("/resources/images/card_art/card_bg.png");
        }
        return defaultCardBackground;
    }
}
