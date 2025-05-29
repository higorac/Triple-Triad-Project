package tripletriad.util;

public enum SoundEffect {
    CARD_PLACED("card-placed.wav"),
    ERROR("error.wav"),
    SELECTION("selection.wav"),
    THEME_LOOP("theme-loop.wav"),
    THEME_START("theme-start.wav"),
    WIN("win.wav");

    private final String fileName;

    SoundEffect(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return "/resources/sounds/" + fileName; // Caminho relativo à raiz do classpath
    }
}