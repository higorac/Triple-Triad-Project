package tripletriad.controller;

import tripletriad.gui.TripleTriadGUI;
import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;
import tripletriad.util.SoundEffect;
import tripletriad.util.SoundManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;
    private Jogador jogadorAtual;
    private List<TripleTriadGUI> observers = new ArrayList<>();
    private List<Carta> reveladasJogador1ParaJogador2 = new ArrayList<>(); // Cartas do J1 que J2 pode ver (Ex: Regra Open)
    private List<Carta> reveladasJogador2ParaJogador1 = new ArrayList<>(); // Cartas do J2 que J1 pode ver (Ex: Regra Open)
    private static final int NUM_CARTAS_REVELADAS_OPEN = 3;

    public Jogo(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.tabuleiro = new Tabuleiro();
        this.jogadorAtual = jogador1; // Define o jogador1 como o primeiro a jogar

        // Lógica de exemplo para a regra "Open": Revela algumas cartas do oponente no início.
        // Você pode ajustar o número de cartas ou remover se não usar essa regra.
        // Esta é apenas uma sugestão de implementação.
        if (this.jogador1 != null && !this.jogador1.getCartasNaMao().isEmpty()) {
            List<Carta> tempHand1 = new ArrayList<>(this.jogador1.getCartasNaMao());
            Collections.shuffle(tempHand1); // Embaralha para pegar aleatórias
            // Alterar o limite do loop para NUM_CARTAS_REVELADAS_OPEN (ou 3)
            for (int i = 0; i < NUM_CARTAS_REVELADAS_OPEN && i < tempHand1.size(); i++) {
                reveladasJogador1ParaJogador2.add(tempHand1.get(i));
            }
        }
        if (this.jogador2 != null && !this.jogador2.getCartasNaMao().isEmpty()) {
            List<Carta> tempHand2 = new ArrayList<>(this.jogador2.getCartasNaMao());
            Collections.shuffle(tempHand2); // Embaralha para pegar aleatórias
            // Alterar o limite do loop para NUM_CARTAS_REVELADAS_OPEN (ou 3)
            for (int i = 0; i < NUM_CARTAS_REVELADAS_OPEN && i < tempHand2.size(); i++) {
                reveladasJogador2ParaJogador1.add(tempHand2.get(i));
            }
        }
    }

    public void addObserver(TripleTriadGUI observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(TripleTriadGUI observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (TripleTriadGUI observer : observers) {
            if (observer != null) {
                observer.updateGUI();
            }
        }
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public Jogador getJogador1() {
        return jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public boolean tentarJogarCarta(int linha, int coluna, Carta carta, Jogador jogadorQueJogou) {
        if (jogadorQueJogou != jogadorAtual || tabuleiro.getCarta(linha, coluna) != null || carta == null) {
            return false; // Não é turno do jogador, slot ocupado ou carta nula
        }

        boolean sucessoPosicionamento = tabuleiro.colocarCarta(linha, coluna, carta);
        if (!sucessoPosicionamento) {
            return false; // Não deveria acontecer se a checagem anterior passou, mas por segurança
        }

        carta.setDono(jogadorQueJogou);
        jogadorQueJogou.removerCarta(carta);

        virarCartasAdjacentes(linha, coluna, carta, jogadorQueJogou);

        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
        SoundManager.getInstance().playSound(SoundEffect.CARD_PLACED);
        notifyObservers();

        if (jogoFinalizado()) {
            calcularPontuacaoFinal(); // Garante que a pontuação está correta e notifica
            notifyObservers(); // Notifica para exibir o estado final e mensagem de vencedor
        }
        return true;
    }

    private void virarCartasAdjacentes(int linha, int coluna, Carta cartaJogada, Jogador jogadorQueJogou) {
        int[] dr = {-1, 1, 0, 0}; // Deslocamento nas linhas: Cima, Baixo, Mesmo, Mesmo
        int[] dc = {0, 0, -1, 1}; // Deslocamento nas colunas: Mesmo, Mesmo, Esquerda, Direita

        // Direções para debug/log (opcional)
        // String[] direcoes = {"Cima", "Baixo", "Esquerda", "Direita"};

        for (int i = 0; i < 4; i++) {
            int nl = linha + dr[i]; // Nova linha (adjacente)
            int nc = coluna + dc[i]; // Nova coluna (adjacente)

            if (nl >= 0 && nl < 3 && nc >= 0 && nc < 3) { // Verifica se a posição adjacente está dentro do tabuleiro
                Carta cartaAdjacente = tabuleiro.getCarta(nl, nc);
                if (cartaAdjacente != null && cartaAdjacente.getDono() != jogadorQueJogou) {
                    boolean virou = false;
                    // Comparação dos ranks
                    // dr[i] == -1: cartaAdjacente está ACIMA da cartaJogada. Compara Baixo da adjacente com Topo da jogada.
                    // dr[i] == 1:  cartaAdjacente está ABAIXO da cartaJogada. Compara Topo da adjacente com Baixo da jogada.
                    // dc[i] == -1: cartaAdjacente está à ESQUERDA da cartaJogada. Compara Direita da adjacente com Esquerda da jogada.
                    // dc[i] == 1:  cartaAdjacente está à DIREITA da cartaJogada. Compara Esquerda da adjacente com Direita da jogada.

                    if (dr[i] == -1 && cartaJogada.getTopo() > cartaAdjacente.getBaixo()) virou = true;       // Checa para CIMA
                    else if (dr[i] == 1 && cartaJogada.getBaixo() > cartaAdjacente.getTopo()) virou = true;    // Checa para BAIXO
                    else if (dc[i] == -1 && cartaJogada.getEsquerda() > cartaAdjacente.getDireita()) virou = true; // Checa para ESQUERDA
                    else if (dc[i] == 1 && cartaJogada.getDireita() > cartaAdjacente.getEsquerda()) virou = true;  // Checa para DIREITA

                    if (virou) {
                        cartaAdjacente.setDono(jogadorQueJogou);
                        // System.out.println("Carta " + cartaAdjacente.getNome() + " virada por " + cartaJogada.getNome() + " na direção: " + direcoes[i]);
                    }
                }
            }
        }
        atualizarPlacarComBaseNoTabuleiro(); // Atualiza o placar após todas as possíveis viradas da jogada
    }

    private void atualizarPlacarComBaseNoTabuleiro() {
        if (jogador1 == null || jogador2 == null || tabuleiro == null) return;

        int cartasJ1NoTabuleiro = 0;
        int cartasJ2NoTabuleiro = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Carta c = tabuleiro.getCarta(i, j);
                if (c != null && c.getDono() != null) {
                    if (c.getDono().equals(jogador1)) {
                        cartasJ1NoTabuleiro++;
                    } else if (c.getDono().equals(jogador2)) {
                        cartasJ2NoTabuleiro++;
                    }
                }
            }
        }
        // A pontuação é o total de cartas que o jogador controla (mão + tabuleiro)
        // O placar do Triple Triad geralmente é 5 vs 5 no início, e muda conforme as cartas são viradas.
        // Uma forma de representar isso é:
        // Pontuação Jogador 1 = 5 (base) + (cartas que J1 virou de J2) - (cartas que J2 virou de J1)
        // No FFVIII, a pontuação é o número de cartas que você controla no final.
        // Se cada um começa com 5, e há 9 posições, o placar pode variar de 0-9 a 9-0, ou 1-8, etc.
        // A forma mais simples: a pontuação é o número de cartas na mão + número de cartas no tabuleiro pertencentes ao jogador.
        // Total de cartas em jogo é sempre 10 (5 para cada jogador inicialmente).
        // A pontuação exibida pode ser apenas as cartas no tabuleiro.

        // Vamos usar o padrão de pontuação onde cada jogador tem 5 "pontos" (representando suas 5 cartas iniciais).
        // Quando uma carta é virada, o placar muda.
        // Se J1 tem N1 cartas no tabuleiro e J2 tem N2 cartas no tabuleiro:
        // J1_score = 5 - (cartas iniciais de J1 que J2 pegou) + (cartas iniciais de J2 que J1 pegou)
        // Total de cartas no tabuleiro = T = N1 + N2
        // Cartas na mão de J1 = 5 - (cartas que J1 jogou)
        // Cartas na mão de J2 = 5 - (cartas que J2 jogou)

        // Pontuação final clássica: número de cartas que o jogador controla (mão + tabuleiro)
        // Mas o placar visível durante o jogo muitas vezes é apenas o número de cartas no tabuleiro.
        // Para o modelo, a pontuação do Jogador deve refletir o estado real de quantas cartas ele controla.
        int pontuacaoJ1 = jogador1.getCartasNaMao().size() + cartasJ1NoTabuleiro;
        int pontuacaoJ2 = jogador2.getCartasNaMao().size() + cartasJ2NoTabuleiro;

        jogador1.setPontuacao(pontuacaoJ1);
        jogador2.setPontuacao(pontuacaoJ2);
    }

    public boolean jogoFinalizado() {
        if (tabuleiro == null) return true; // Segurança
        // O jogo termina quando o tabuleiro está cheio (9 cartas)
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tabuleiro.getCarta(i, j) != null) {
                    count++;
                }
            }
        }
        return count == 9;
    }

    private void calcularPontuacaoFinal() {
        // A pontuação já deve ter sido atualizada pela última chamada a atualizarPlacarComBaseNoTabuleiro()
        // Esta função é mais um ponto de demarcação ou para lógicas finais complexas (Same, Plus, etc.)
        atualizarPlacarComBaseNoTabuleiro(); // Garante a atualização final
        // System.out.println("Jogo finalizado. Pontuação final: ");
        // System.out.println(jogador1.getNome() + ": " + jogador1.getPontuacao());
        // System.out.println(jogador2.getNome() + ": " + jogador2.getPontuacao());
    }

    public SoundEffect determineEndGameSound() {
        if (!jogoFinalizado() || jogador1 == null || jogador2 == null) return null;

        int p1Score = jogador1.getPontuacao();
        int p2Score = jogador2.getPontuacao();

        // Considera o jogador da GUI para determinar se é vitória ou derrota "para ele"
        // Isso é mais complexo se o som for tocado centralmente.
        // Por enquanto, um som de vitória genérico se alguém ganhou, ou um som neutro/empate.
        if (p1Score > p2Score) {
            return SoundEffect.WIN;
        } else if (p2Score > p1Score) {
            return SoundEffect.WIN; // Poderia ser um som de "LOSE" se tocado pela perspectiva do jogador perdedor
        } else {
            // Para empate, pode ser um som neutro ou o mesmo de colocar carta.
            // Se tiver um som específico para empate, use-o.
            return SoundEffect.CARD_PLACED; // Ou outro som apropriado para empate.
        }
    }

    public List<Carta> getReveladasDoOponenteParaJogador(Jogador jogadorQueEstaVendo) {
        if (jogadorQueEstaVendo == null || jogador1 == null || jogador2 == null) return Collections.emptyList();

        if (jogadorQueEstaVendo.equals(jogador1)) {
            // Retorna as cartas do Jogador 2 que o Jogador 1 pode ver
            // Garante que as cartas retornadas ainda estão na mão do oponente
            List<Carta> visiveis = new ArrayList<>();
            for(Carta c : reveladasJogador2ParaJogador1) {
                if (jogador2.getCartasNaMao().contains(c)) {
                    visiveis.add(c);
                }
            }
            return visiveis;
        } else if (jogadorQueEstaVendo.equals(jogador2)) {
            // Retorna as cartas do Jogador 1 que o Jogador 2 pode ver
            List<Carta> visiveis = new ArrayList<>();
            for(Carta c : reveladasJogador1ParaJogador2) {
                if (jogador1.getCartasNaMao().contains(c)) {
                    visiveis.add(c);
                }
            }
            return visiveis;
        }
        return Collections.emptyList();
    }
}