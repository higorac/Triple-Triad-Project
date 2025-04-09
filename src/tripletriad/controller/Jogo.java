package tripletriad.controller;

import tripletriad.model.Jogador;
import tripletriad.model.Tabuleiro;

import java.util.Scanner;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Tabuleiro tabuleiro;

    private Scanner Scanner;

    public Jogo(Jogador j1, Jogador j2){
        this.jogador1 = j1;
        this.jogador2 = j2;
        this.tabuleiro = new Tabuleiro();
        this.Scanner = new Scanner(System.in);
    }
}
