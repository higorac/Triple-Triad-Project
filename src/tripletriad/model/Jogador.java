package tripletriad.model;

import java.util.ArrayList;
import java.util.List;

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

    public void aumentarPontuacao(int pontos) {
        this.pontuacao += pontos;
    }

    public void diminuirPontuacao(int pontos) {
        this.pontuacao -= pontos;
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

    public boolean temCartas() {
        return !cartasNaMao.isEmpty();
    }

    public void mostrarCartas() {
        for (int i = 0; i < cartasNaMao.size(); i++) {
            System.out.println("Carta " + (i + 1) + ":");
            String[] visual = cartasNaMao.get(i).toStringVisual();
            for (String linha : visual) {
                System.out.println(linha);
            }
            System.out.println();
        }
    }
}
