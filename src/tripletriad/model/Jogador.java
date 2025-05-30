package tripletriad.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um jogador no jogo Triple Triad.
 * Cada jogador tem um nome, uma pontuação e uma lista de cartas na mão.
 */

public class Jogador {
    private String nome;
    private int pontuacao;
    private List<Carta> cartasNaMao;

    public Jogador(String nome) {
        this.nome = nome;
        this.pontuacao = 5; // pontuação inicial
        this.cartasNaMao = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void adicionarCarta(Carta carta) {
        cartasNaMao.add(carta);
    }

    public void removerCarta(Carta carta) {
        cartasNaMao.remove(carta);
    }

    public List<Carta> getCartasNaMao() {
        return cartasNaMao;
    }
}
