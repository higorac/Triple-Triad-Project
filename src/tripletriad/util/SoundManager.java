package tripletriad.util;

import javax.sound.sampled.LineListener;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;

public class SoundManager {

    private static SoundManager instance;
    private final Map<SoundEffect, Clip> soundClips = new HashMap<>();
    private Clip currentThemeClip = null;
    private LineListener themeTransitionListener = null;
    private final ExecutorService soundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true); // Configura a thread como daemon
            return t;
        }
    });

    private SoundManager() {
        // Pre-load all sounds
        for (SoundEffect effect : SoundEffect.values()) {
            loadSound(effect);
        }
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSound(SoundEffect effect) {
        // Submitted to executor to avoid blocking constructor if a sound takes time
        soundExecutor.submit(() -> {
            try {
                URL soundURL = SoundManager.class.getResource(effect.getFileName());
                if (soundURL == null) {
                    System.err.println("Sound file not found: " + effect.getFileName());
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                soundClips.put(effect, clip);
                // System.out.println("Loaded sound: " + effect.getFileName());
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error loading sound " + effect.getFileName() + ": " + e.getMessage());
            }
        });
    }

    public void playSound(SoundEffect effect) {
        soundExecutor.submit(() -> {
            Clip clip = soundClips.get(effect);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0); // Rewind to the beginning
                clip.start();
            } else {
                System.err.println("Sound not loaded or clip is null for: " + effect.getFileName() + ". Attempting to reload.");
                loadSound(effect); // Attempt to load it if it wasn't (e.g. due to earlier error)
                // You could try playing it again after a short delay if needed, but for now, just reload.
            }
        });
    }

    public void loopSound(SoundEffect effect, boolean loopContinuously) {
        soundExecutor.submit(() -> {
            Clip clipToLoop = soundClips.get(effect);
            if (clipToLoop != null) {
                if (currentThemeClip != null && currentThemeClip.isRunning()) {
                    currentThemeClip.stop();
                }
                clipToLoop.setFramePosition(0);
                currentThemeClip = clipToLoop;
                if (loopContinuously) {
                    currentThemeClip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    currentThemeClip.start();
                }
            } else {
                System.err.println("Theme sound not loaded or clip is null for: " + effect.getFileName());
                loadSound(effect);
            }
        });
    }

    public void stopSound(SoundEffect effect) {
        soundExecutor.submit(() -> {
            Clip clip = soundClips.get(effect);
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            if (currentThemeClip == clip && effect == SoundEffect.THEME_LOOP || effect == SoundEffect.THEME_START) {
                currentThemeClip = null; // Clear if it was the current theme
            }
        });
    }

    public void stopAllSounds() {
        soundExecutor.submit(() -> {
            for (Clip clip : soundClips.values()) {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
            }
            if (currentThemeClip != null && currentThemeClip.isRunning()) {
                currentThemeClip.stop();
            }
            currentThemeClip = null;
            // System.out.println("All sounds stopped.");
        });
    }

    public void playThemeSequence() {
        soundExecutor.submit(() -> {
            Clip themeStartClip = soundClips.get(SoundEffect.THEME_START);
            Clip themeLoopClip = soundClips.get(SoundEffect.THEME_LOOP);

            if (themeStartClip == null) {
                System.err.println("THEME_START sound not loaded. Attempting to play THEME_LOOP directly.");
                if (themeLoopClip != null) {
                    loopSound(SoundEffect.THEME_LOOP, true);
                } else {
                    System.err.println("THEME_LOOP sound also not loaded. No theme will play.");
                    loadSound(SoundEffect.THEME_LOOP); // Attempt to load
                }
                return;
            }

            if (currentThemeClip != null && currentThemeClip.isRunning()) {
                currentThemeClip.stop();
            }

            themeStartClip.setFramePosition(0);
            currentThemeClip = themeStartClip;

            // Remove o listener de transição anterior específico, se existir
            if (this.themeTransitionListener != null) {
                themeStartClip.removeLineListener(this.themeTransitionListener);
            }

            // Cria e define o novo listener de transição
            this.themeTransitionListener = event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    // Verifica se parou naturalmente e não foi interrompido manualmente ou substituído
                    // E se o evento é do clipe que esperamos (themeStartClip)
                    if (currentThemeClip == themeStartClip &&
                            themeStartClip.getFramePosition() >= themeStartClip.getFrameLength() - 20 && /* Tolerância */
                            event.getSource() == themeStartClip) {

                        // System.out.println("THEME_START finished, starting THEME_LOOP.");
                        if (themeLoopClip != null) {
                            // O themeTransitionListener será substituído na próxima chamada a playThemeSequence se necessário.
                            loopSound(SoundEffect.THEME_LOOP, true);
                        } else {
                            System.err.println("THEME_LOOP not loaded, cannot switch.");
                            loadSound(SoundEffect.THEME_LOOP); // Attempt to load
                        }
                    }
                }
            };

            themeStartClip.addLineListener(this.themeTransitionListener);
            themeStartClip.start();
            // System.out.println("Playing THEME_START.");
        });
    }

    public void shutdownExecutor() {
        soundExecutor.shutdown();
        try {
            if (!soundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                soundExecutor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            soundExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // System.out.println("SoundManager executor shut down.");
    }
}