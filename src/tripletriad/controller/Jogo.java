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

/**
 * Controla a lógica e o estado de uma partida de Triple Triad.
 * Gerencia os jogadores, o tabuleiro, o turno atual e as regras do jogo,
 * como virar cartas e calcular pontuações.
 * Utiliza o padrão Observer para notificar as GUIs (TripleTriadGUI)
 * sobre mudanças no estado do jogo, permitindo que a interface do usuário seja atualizada.
 */

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;
    private Jogador jogadorAtual;
    private List<TripleTriadGUI> observers = new ArrayList<>();
    private List<Carta> reveladasJogador1ParaJogador2 = new ArrayList<>();
    private List<Carta> reveladasJogador2ParaJogador1 = new ArrayList<>();
    private static final int NUM_CARTAS_REVELADAS_OPEN = 3;

    public Jogo(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.tabuleiro = new Tabuleiro();
        this.jogadorAtual = jogador1; // Define o jogador1 como o primeiro a jogar

        if (this.jogador1 != null && !this.jogador1.getCartasNaMao().isEmpty()) {
            List<Carta> tempHand1 = new ArrayList<>(this.jogador1.getCartasNaMao());
            Collections.shuffle(tempHand1); // Embaralha para pegar aleatórias
            for (int i = 0; i < NUM_CARTAS_REVELADAS_OPEN && i < tempHand1.size(); i++) {
                reveladasJogador1ParaJogador2.add(tempHand1.get(i));
            }
        }
        if (this.jogador2 != null && !this.jogador2.getCartasNaMao().isEmpty()) {
            List<Carta> tempHand2 = new ArrayList<>(this.jogador2.getCartasNaMao());
            Collections.shuffle(tempHand2); // Embaralha para pegar aleatórias
            for (int i = 0; i < NUM_CARTAS_REVELADAS_OPEN && i < tempHand2.size(); i++) {
                reveladasJogador2ParaJogador1.add(tempHand2.get(i));
            }
        }
    }

    //Metodos Observers que modificam o estado do jogo em tempo real enquanto é jogado

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

    //Metódo que valida todas as jogados dos jogares e vê se são possiveis
    //Se o metodo for valido, ele define um dono para carta e aplica a regra de batalha

    public boolean tentarJogarCarta(int linha, int coluna, Carta carta, Jogador jogadorQueJogou) {
        if (jogadorQueJogou != jogadorAtual || tabuleiro.getCarta(linha, coluna) != null || carta == null) {
            return false;
        }

        boolean sucessoPosicionamento = tabuleiro.colocarCarta(linha, coluna, carta);
        if (!sucessoPosicionamento) {
            return false;
        }

        carta.setDono(jogadorQueJogou);
        jogadorQueJogou.removerCarta(carta);

        //Troca de jogador após jogada e verifica se o jogo foi finalizado

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

    //Metodo que pega e compara as cartas para atualizar a pontuação

    private void virarCartasAdjacentes(int linha, int coluna, Carta cartaJogada, Jogador jogadorQueJogou) {
        int[] dr = {-1, 1, 0, 0}; // Deslocamento nas linhas: Cima, Baixo, Mesmo, Mesmo
        int[] dc = {0, 0, -1, 1}; // Deslocamento nas colunas: Mesmo, Mesmo, Esquerda, Direita

        for (int i = 0; i < 4; i++) {
            int nl = linha + dr[i]; // Nova linha (adjacente)
            int nc = coluna + dc[i]; // Nova coluna (adjacente)

            if (nl >= 0 && nl < 3 && nc >= 0 && nc < 3) { // Verifica se a posição adjacente está dentro do tabuleiro
                Carta cartaAdjacente = tabuleiro.getCarta(nl, nc);
                if (cartaAdjacente != null && cartaAdjacente.getDono() != jogadorQueJogou) {
                    boolean virou = false;

                    if (dr[i] == -1 && cartaJogada.getTopo() > cartaAdjacente.getBaixo()) virou = true;       // Checa para CIMA
                    else if (dr[i] == 1 && cartaJogada.getBaixo() > cartaAdjacente.getTopo()) virou = true;    // Checa para BAIXO
                    else if (dc[i] == -1 && cartaJogada.getEsquerda() > cartaAdjacente.getDireita()) virou = true; // Checa para ESQUERDA
                    else if (dc[i] == 1 && cartaJogada.getDireita() > cartaAdjacente.getEsquerda()) virou = true;  // Checa para DIREITA

                    if (virou) {
                        cartaAdjacente.setDono(jogadorQueJogou);
                    }
                }
            }
        }
        atualizarPlacarComBaseNoTabuleiro(); // Atualiza o placar após todas as possíveis viradas da jogada
    }

    //Calcula a pontuação de cada jogador somando as cartas na mão com as do tabuleiro

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
        int pontuacaoJ1 = jogador1.getCartasNaMao().size() + cartasJ1NoTabuleiro;
        int pontuacaoJ2 = jogador2.getCartasNaMao().size() + cartasJ2NoTabuleiro;

        jogador1.setPontuacao(pontuacaoJ1);
        jogador2.setPontuacao(pontuacaoJ2);
    }

    //Confere se o tabuleiro está cheio, se sim, o jogo finaliza e ele traz o efeito sonoro de vitória caso um jogador ganhe

    public boolean jogoFinalizado() {
        if (tabuleiro == null) return true;
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

        atualizarPlacarComBaseNoTabuleiro(); // Garante a atualização final
    }

    public SoundEffect determineEndGameSound() {
        if (!jogoFinalizado() || jogador1 == null || jogador2 == null) return null;

        int p1Score = jogador1.getPontuacao();
        int p2Score = jogador2.getPontuacao();

        if (p1Score > p2Score) {
            return SoundEffect.WIN;
        } else if (p2Score > p1Score) {
            return SoundEffect.WIN;
        } else {
            return SoundEffect.CARD_PLACED;
        }
    }

    public List<Carta> getReveladasDoOponenteParaJogador(Jogador jogadorQueEstaVendo) {
        if (jogadorQueEstaVendo == null || jogador1 == null || jogador2 == null) return Collections.emptyList();

        if (jogadorQueEstaVendo.equals(jogador1)) {
            List<Carta> visiveis = new ArrayList<>();
            for(Carta c : reveladasJogador2ParaJogador1) {
                if (jogador2.getCartasNaMao().contains(c)) {
                    visiveis.add(c);
                }
            }
            return visiveis;
        } else if (jogadorQueEstaVendo.equals(jogador2)) {
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