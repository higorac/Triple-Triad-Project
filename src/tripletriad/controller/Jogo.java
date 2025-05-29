package tripletriad.controller;

import tripletriad.model.Carta;
import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;
import tripletriad.gui.TripleTriadGUI; // Importar a GUI

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;
    private int turno;
    private List<TripleTriadGUI> observers = new ArrayList<>();

    // Armazena as cartas do oponente que são inicialmente reveladas para cada jogador "viewer"
    private Map<Jogador, List<Carta>> reveladasDoOponenteParaJogadorViewer;

    public Jogo(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.tabuleiro = new Tabuleiro();
        this.turno = 0;
        this.reveladasDoOponenteParaJogadorViewer = new HashMap<>();

        // Preenche o mapa com as cartas inicialmente reveladas
        // Chamado DEPOIS que CartaLoader.distribuirCartas já populou as mãos (na classe Main)
        if (this.jogador1 != null && this.jogador1.getCartasNaMao() != null &&
                this.jogador2 != null && this.jogador2.getCartasNaMao() != null) {

            List<Carta> p1Hand = this.jogador1.getCartasNaMao();
            List<Carta> p2Hand = this.jogador2.getCartasNaMao();

            // Cartas de P1 visíveis para P2
            if (!p1Hand.isEmpty()) {
                this.reveladasDoOponenteParaJogadorViewer.put(this.jogador2, // Jogador 2 (viewer)
                        new ArrayList<>(p1Hand.subList(0, Math.min(3, p1Hand.size()))));
            } else {
                this.reveladasDoOponenteParaJogadorViewer.put(this.jogador2, Collections.emptyList());
            }

            // Cartas de P2 visíveis para P1
            if (!p2Hand.isEmpty()) {
                this.reveladasDoOponenteParaJogadorViewer.put(this.jogador1, // Jogador 1 (viewer)
                        new ArrayList<>(p2Hand.subList(0, Math.min(3, p2Hand.size()))));
            } else {
                this.reveladasDoOponenteParaJogadorViewer.put(this.jogador1, Collections.emptyList());
            }
            System.out.println("Jogo init: P1 ("+jogador1.getNome()+") vê de P2: " + this.reveladasDoOponenteParaJogadorViewer.get(this.jogador1));
            System.out.println("Jogo init: P2 ("+jogador2.getNome()+") vê de P1: " + this.reveladasDoOponenteParaJogadorViewer.get(this.jogador2));

        } else {
            System.err.println("Erro no construtor de Jogo: Mãos dos jogadores não inicializadas corretamente antes de configurar cartas reveladas.");
            // Inicializa com listas vazias para evitar NullPointerExceptions posteriores
            this.reveladasDoOponenteParaJogadorViewer.put(this.jogador1, Collections.emptyList());
            this.reveladasDoOponenteParaJogadorViewer.put(this.jogador2, Collections.emptyList());
        }
    }

    // Getter para as cartas reveladas
    public List<Carta> getReveladasDoOponenteParaJogador(Jogador jogadorViewer) {
        return reveladasDoOponenteParaJogadorViewer.getOrDefault(jogadorViewer, Collections.emptyList());
    }

    // Métodos do Observer
    public void addObserver(TripleTriadGUI observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(TripleTriadGUI observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (TripleTriadGUI observer : new ArrayList<>(observers)) { // Itera sobre uma cópia
            observer.updateGUI();
        }
    }

    // Getters
    public Jogador getJogador1() {
        return jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Jogador getJogadorAtual() {
        if (jogador1 == null || jogador2 == null) return null; // Segurança
        return (turno % 2 == 0) ? jogador1 : jogador2;
    }

    public boolean jogoFinalizado() {
        return tabuleiro != null && tabuleiro.estaCheio();
    }

    public boolean tentarJogarCarta(int linha, int coluna, Carta cartaEscolhida, Jogador jogadorQueJogou) {
        if (jogadorQueJogou == null || cartaEscolhida == null) {
            System.err.println("Erro crítico: Jogador ou Carta escolhida é nulo em tentarJogarCarta.");
            return false;
        }

        if (this.getJogadorAtual() != jogadorQueJogou) {
            System.err.println("Atenção: Tentativa de jogada fora de turno por " + jogadorQueJogou.getNome() + ". Vez de: " + (this.getJogadorAtual() != null ? this.getJogadorAtual().getNome() : "Ninguém"));
            return false;
        }

        List<Carta> maoDoJogador = jogadorQueJogou.getCartasNaMao();
        if (maoDoJogador == null) {
            System.err.println("Erro crítico: Mão do jogador " + jogadorQueJogou.getNome() + " é nula.");
            return false;
        }

        int indiceNaMao = maoDoJogador.indexOf(cartaEscolhida); // Requer Carta.equals()
        if (indiceNaMao == -1) {
            System.err.println("Erro: Carta " + cartaEscolhida.getNome() + " não encontrada na mão de " + jogadorQueJogou.getNome() +
                    ". Verifique se Carta.equals() está implementado corretamente.");
            return false;
        }

        if (this.tabuleiro == null) {
            System.err.println("Erro crítico: Tabuleiro do jogo é nulo.");
            return false;
        }

        if (!this.tabuleiro.colocarCarta(linha, coluna, cartaEscolhida)) {
            System.out.println("Posição [" + linha + "," + coluna + "] ocupada ou inválida. Carta não jogada.");
            return false;
        }

        maoDoJogador.remove(indiceNaMao);
        cartaEscolhida.setDono(jogadorQueJogou);

        capturarCartasAdjacentes(linha, coluna, cartaEscolhida);

        this.turno++;
        System.out.println("Carta " + cartaEscolhida.getNome() + " jogada por " + jogadorQueJogou.getNome() + " em [" + linha + "," + coluna + "]");
        notifyObservers();
        return true;
    }

    private void capturarCartasAdjacentes(int linha, int coluna, Carta cartaJogada) {
        List<int[]> capturasNormais = new ArrayList<>();
        Map<Integer, List<int[]>> capturasPorSoma = new HashMap<>();

        verificarAdjacente(linha - 1, coluna, cartaJogada, cartaJogada.getTopo(), "baixo", capturasNormais, capturasPorSoma);
        verificarAdjacente(linha + 1, coluna, cartaJogada, cartaJogada.getBaixo(), "topo", capturasNormais, capturasPorSoma);
        verificarAdjacente(linha, coluna - 1, cartaJogada, cartaJogada.getEsquerda(), "direita", capturasNormais, capturasPorSoma);
        verificarAdjacente(linha, coluna + 1, cartaJogada, cartaJogada.getDireita(), "esquerda", capturasNormais, capturasPorSoma);

        boolean algumaRegraEspecialAtivada = false;
        // Lógica para "Same" (se implementada separadamente de "Plus")
        // ... (se tiver regra "Same" que não seja baseada em soma, adicione aqui)

        // Lógica para "Plus"
        for (Map.Entry<Integer, List<int[]>> entry : capturasPorSoma.entrySet()) {
            if (entry.getValue().size() >= 2) {
                algumaRegraEspecialAtivada = true;
                System.out.println("REGRA 'PLUS' ATIVADA (soma igual em múltiplas direções)");
                for (int[] pos : entry.getValue()) {
                    capturar(cartaJogada, pos[0], pos[1], true);
                }
                // Não há bônus de +2 pontos na regra padrão, apenas as capturas múltiplas.
                break;
            }
        }

        if (!algumaRegraEspecialAtivada) {
            for (int[] pos : capturasNormais) {
                capturar(cartaJogada, pos[0], pos[1], false);
            }
        }
    }

    private void verificarAdjacente(int la, int ca, Carta jogada, int valorAtaque, String ladoDefesaOposto,
                                    List<int[]> capturasNormais, Map<Integer, List<int[]>> candidatasParaPlus) {
        if (la < 0 || la > 2 || ca < 0 || ca > 2) return;

        Carta alvo = tabuleiro.getCarta(la, ca);
        if (alvo != null && alvo.getDono() != null && alvo.getDono() != jogada.getDono()) {
            int valorDefesaAlvo = -1;
            switch (ladoDefesaOposto) {
                case "topo": valorDefesaAlvo = alvo.getTopo(); break;
                case "baixo": valorDefesaAlvo = alvo.getBaixo(); break;
                case "esquerda": valorDefesaAlvo = alvo.getEsquerda(); break;
                case "direita": valorDefesaAlvo = alvo.getDireita(); break;
            }
            if (valorDefesaAlvo == -1) return;

            if (valorAtaque > valorDefesaAlvo) {
                capturasNormais.add(new int[]{la, ca});
            }
            // Para regra "Plus", adicionamos a carta e sua posição se a soma for relevante
            // A checagem se a soma é igual em MÚLTIPLAS direções é feita em capturarCartasAdjacentes
            int soma = valorAtaque + valorDefesaAlvo;
            candidatasParaPlus.computeIfAbsent(soma, k -> new ArrayList<>()).add(new int[]{la, ca});
        }
    }

    private void capturar(Carta cartaJogada, int linhaAlvo, int colunaAlvo, boolean porRegraEspecial) {
        Carta cartaAlvo = tabuleiro.getCarta(linhaAlvo, colunaAlvo);
        // Verifica se o alvo é válido e pertence ao oponente
        if (cartaAlvo == null || cartaAlvo.getDono() == null || cartaAlvo.getDono() == cartaJogada.getDono()) {
            return;
        }

        Jogador donoDaCartaJogada = cartaJogada.getDono();
        Jogador donoAnteriorDaCartaAlvo = cartaAlvo.getDono();

        System.out.println("--- CAPTURANDO CARTA ---");
        System.out.println("Atacante: " + donoDaCartaJogada.getNome() + " (Score antes: " + donoDaCartaJogada.getPontuacao() + ")");
        System.out.println("Defensor: " + donoAnteriorDaCartaAlvo.getNome() + " (Score antes: " + donoAnteriorDaCartaAlvo.getPontuacao() + ")");
        System.out.println(donoDaCartaJogada.getNome() + " está capturando " + cartaAlvo.getNome() + " de " + donoAnteriorDaCartaAlvo.getNome());

        cartaAlvo.setDono(donoDaCartaJogada);
        donoDaCartaJogada.aumentarPontuacao(1);
        donoAnteriorDaCartaAlvo.diminuirPontuacao(1); // Dono anterior sempre existe e é diferente

        System.out.println("Score DEPOIS: " + donoDaCartaJogada.getNome() + "=" + donoDaCartaJogada.getPontuacao() +
                ", " + donoAnteriorDaCartaAlvo.getNome() + "=" + donoAnteriorDaCartaAlvo.getPontuacao());
        String tipo = porRegraEspecial ? "Captura por Regra Especial! " : "Carta capturada! ";
        System.out.println(tipo);
    }

    // Métodos de console para depuração
    public void mostrarTabuleiroConsole() {
        if(tabuleiro != null) tabuleiro.exibirTabuleiro();
    }

    public void mostrarPlacarConsole() {
        if(jogador1 != null && jogador2 != null) {
            System.out.println(jogador1.getNome() + ": " + jogador1.getPontuacao() + " pontos");
            System.out.println(jogador2.getNome() + ": " + jogador2.getPontuacao() + " pontos");
        }
    }
}