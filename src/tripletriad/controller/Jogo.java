package tripletriad.controller;

import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;
    private int turno;

    public Jogo(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.tabuleiro = new Tabuleiro();
        this.turno = 0;

        // Pontuação inicial
        jogador1.setPontuacao(5);
        jogador2.setPontuacao(5);
    }

    public void jogarCarta(int linha, int coluna, Carta carta) {
        if (!tabuleiro.colocarCarta(linha, coluna, carta)) {
            System.out.println("Posição ocupada. Tente outra.");
            return;
        }

        // Checa cartas adjacentes e aplica captura
        capturarCartasAdjacentes(linha, coluna, carta);

        turno++;
        System.out.println("Carta jogada!");
    }

    private void capturarCartasAdjacentes(int linha, int coluna, Carta carta) {
        // CIMA
        if (linha > 0) verificarCaptura(linha, coluna, linha - 1, coluna, carta, carta.getTopo(), "baixo");
        // BAIXO
        if (linha < 2) verificarCaptura(linha, coluna, linha + 1, coluna, carta, carta.getBaixo(), "topo");
        // ESQUERDA
        if (coluna > 0) verificarCaptura(linha, coluna, linha, coluna - 1, carta, carta.getEsquerda(), "direita");
        // DIREITA
        if (coluna < 2) verificarCaptura(linha, coluna, linha, coluna + 1, carta, carta.getDireita(), "esquerda");
    }

    private void verificarCaptura(int linhaOrigem, int colunaOrigem, int linhaAlvo, int colunaAlvo, Carta cartaJogada, int valorAtaque, String ladoDefesa) {
        Carta cartaAlvo = tabuleiro.getCarta(linhaAlvo, colunaAlvo);

        if (cartaAlvo != null && cartaAlvo.getDono() != cartaJogada.getDono()) {
            int valorDefesa = switch (ladoDefesa) {
                case "topo" -> cartaAlvo.getTopo();
                case "baixo" -> cartaAlvo.getBaixo();
                case "esquerda" -> cartaAlvo.getEsquerda();
                case "direita" -> cartaAlvo.getDireita();
                default -> -1;
            };

            if (valorAtaque > valorDefesa) {
                Jogador donoAnterior = cartaAlvo.getDono();
                cartaAlvo.setDono(cartaJogada.getDono());

                cartaJogada.getDono().aumentarPontuacao(1);
                donoAnterior.diminuirPontuacao(1);

                System.out.println("Carta capturada! " + cartaJogada.getDono().getNome() + " tomou uma carta de " + donoAnterior.getNome());
            }
        }
    }

    public Jogador getJogadorAtual() {
        return (turno % 2 == 0) ? jogador1 : jogador2;
    }

    public boolean jogoFinalizado() {
        return tabuleiro.estaCheio();
    }

    public void mostrarTabuleiro() {
        tabuleiro.exibirTabuleiro();
    }

    public void mostrarPlacar() {
        System.out.println(jogador1.getNome() + ": " + jogador1.getPontuacao() + " pontos");
        System.out.println(jogador2.getNome() + ": " + jogador2.getPontuacao() + " pontos");
    }

    public void mostrarVencedor() {
        int p1 = jogador1.getPontuacao();
        int p2 = jogador2.getPontuacao();

        if (p1 > p2) {
            System.out.println("Vitória de " + jogador1.getNome() + "!");
        } else if (p2 > p1) {
            System.out.println("Vitória de " + jogador2.getNome() + "!");
        } else {
            System.out.println("Empate!");
        }
    }
}
