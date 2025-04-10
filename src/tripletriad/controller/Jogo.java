package tripletriad.controller;

import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;

import java.util.*;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;
    private int turno;
    private Map<Jogador, List<Carta>> cartasVisiveisOponente;

    public Jogo(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.tabuleiro = new Tabuleiro();
        this.turno = 0;
        this.cartasVisiveisOponente = new HashMap<>();
    }

    public void jogarCarta(int linha, int coluna, Carta carta, int indiceCarta) {
        if (!tabuleiro.colocarCarta(linha, coluna, carta)) {
            System.out.println("Posição ocupada. Tente outra.");
            Jogador jogadorAtual = getJogadorAtual();
            jogadorAtual.getCartasNaMao().add(indiceCarta, carta);
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

    public void iniciar(Scanner scanner) {
        // Inicializa as 3 cartas visíveis do oponente antes do jogo começar
        cartasVisiveisOponente.put(jogador1, new ArrayList<>(jogador2.getCartasNaMao().subList(0, Math.min(3, jogador2.getCartasNaMao().size()))));
        cartasVisiveisOponente.put(jogador2, new ArrayList<>(jogador1.getCartasNaMao().subList(0, Math.min(3, jogador1.getCartasNaMao().size()))));

        while (!jogoFinalizado()) {
            mostrarTabuleiro();
            mostrarPlacar();

            Jogador atual = getJogadorAtual();
            Jogador oponente = (atual == jogador1) ? jogador2 : jogador1;
            System.out.println("\nVez de " + atual.getNome());

            List<Carta> cartasJogador = atual.getCartasNaMao();
            List<Carta> cartasOponenteVisiveis = cartasVisiveisOponente.get(atual);

            // Cartas do jogador (com índice)
            for (int i = 0; i < cartasJogador.size(); i++) {
                Carta carta = cartasJogador.get(i);
                String titulo = String.format("[%d] %s - %s", i, carta.getNome(), carta.getTipo());
                System.out.printf("%-25s", titulo);
            }
            System.out.println();

            String[][] visuaisJogador = new String[cartasJogador.size()][];
            for (int i = 0; i < cartasJogador.size(); i++) {
                visuaisJogador[i] = cartasJogador.get(i).toStringVisual();
            }

            for (int linha = 0; linha < 5; linha++) {
                for (int i = 0; i < cartasJogador.size(); i++) {
                    System.out.printf("%-25s", visuaisJogador[i][linha]);
                }
                System.out.println();
            }

            // Separador visual
            System.out.println("--- Cartas visíveis do oponente ---");

            for (Carta carta : cartasOponenteVisiveis) {
                String titulo = String.format("    %s - %s", carta.getNome(), carta.getTipo());
                System.out.printf("%-25s", titulo);
            }
            System.out.println();

            String[][] visuaisOponente = new String[cartasOponenteVisiveis.size()][];
            for (int i = 0; i < cartasOponenteVisiveis.size(); i++) {
                visuaisOponente[i] = cartasOponenteVisiveis.get(i).toStringVisual();
            }

            for (int linha = 0; linha < 5; linha++) {
                for (int i = 0; i < cartasOponenteVisiveis.size(); i++) {
                    System.out.printf("%-25s", visuaisOponente[i][linha]);
                }
                System.out.println();
            }

            int indiceCarta = -1;
            while (indiceCarta < 0 || indiceCarta >= cartasJogador.size()) {
                System.out.print("Escolha o índice da carta que deseja jogar: ");
                try {
                    indiceCarta = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número válido.");
                }
            }

            Carta cartaEscolhida = cartasJogador.remove(indiceCarta);
            cartaEscolhida.setDono(atual);

            // Remove a carta do oponente se for visível para o jogador atual
            cartasVisiveisOponente.get(oponente).remove(cartaEscolhida);

            int linha = -1, coluna = -1;
            boolean jogadaValida = false;

            while (!jogadaValida) {
                try {
                    System.out.print("Escolha a linha (0 a 2): ");
                    linha = Integer.parseInt(scanner.nextLine());

                    System.out.print("Escolha a coluna (0 a 2): ");
                    coluna = Integer.parseInt(scanner.nextLine());

                    jogarCarta(linha, coluna, cartaEscolhida, indiceCarta);
                    jogadaValida = true;
                } catch (Exception e) {
                    System.out.println("Erro ao tentar jogar a carta. Tente novamente.");
                }
            }

            System.out.println("--------------------------------------------\n");
        }

        mostrarTabuleiro();
        mostrarPlacar();
        mostrarVencedor();
    }
}
