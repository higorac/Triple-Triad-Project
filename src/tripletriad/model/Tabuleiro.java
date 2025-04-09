package tripletriad.model;

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

    public void exibirTabuleiro() {
        for (int linha = 0; linha < 3; linha++) {
            // Linha por linha da carta (5 linhas visuais por carta)
            for (int linhaVisual = 0; linhaVisual < 5; linhaVisual++) {
                for (int coluna = 0; coluna < 3; coluna++) {
                    Carta carta = matriz[linha][coluna];
                    if (carta != null) {
                        System.out.print(carta.toStringVisual()[linhaVisual]);
                    } else {
                        // Espaço vazio com o mesmo tamanho
                        System.out.print("       ");
                    }
                }
                System.out.println();
            }
        }
    }

    public boolean estaCheio() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matriz[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public Carta getCarta(int linha, int coluna) {
        return matriz[linha][coluna];
    }

}
