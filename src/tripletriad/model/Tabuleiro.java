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
        String horizontal = "+-------+-------+-------+";
        for (int linha = 0; linha < 3; linha++) {
            System.out.println(horizontal);
            for (int linhaVisual = 0; linhaVisual < 5; linhaVisual++) {
                for (int coluna = 0; coluna < 3; coluna++) {
                    Carta carta = matriz[linha][coluna];
                    String conteudo = (carta != null) ? carta.toStringVisual()[linhaVisual] : "       ";
                    System.out.print("|" + conteudo);
                }
                System.out.println("|");
            }
        }
        System.out.println(horizontal);
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
