package tripletriad.model;

/**
 * Representa o tabuleiro 3x3 do jogo Triple Triad.
 * Armazena as cartas que foram colocadas durante a partida.
 */

public class Tabuleiro {
    private Carta[][] matriz;

    public Tabuleiro() {
        matriz = new Carta[3][3];
    }

    public boolean colocarCarta(int linha, int coluna, Carta carta) {
        if (matriz[linha][coluna] == null) {
            matriz[linha][coluna] = carta;
            return true;
        }
        return false;
    }

    public Carta getCarta(int linha, int coluna) {
        return matriz[linha][coluna];
    }

}
