// Arquivo: src/tripletriad/model/Carta.java
package tripletriad.model;

/**
 * Representa uma carta individual no jogo Triple Triad.
 * Contém informações sobre seus ranks, nome, ID, tipo (elemento) e
 * o jogador que atualmente a possui (dono).
 */

public class Carta {
    private int id; // Identificador único da carta.
    private String nome; // Nome da carta.
    private int topo; // Valor do rank superior.
    private int direita; // Valor do rank direito.
    private int baixo; // Valor do rank inferior.
    private int esquerda; // Valor do rank esquerdo.
    private String tipo; // Elemento da carta (ex: FIRE, ICE, NEUTRAL).
    private Jogador dono; // Jogador que atualmente controla esta carta.

    public Carta(int id, String nome, int topo, int direita, int baixo, int esquerda, String tipo) { //
        this.id = id; //
        this.nome = nome; //
        this.topo = topo; //
        this.direita = direita; //
        this.baixo = baixo; //
        this.esquerda = esquerda; //
        this.tipo = tipo; //
    }

    /**
     * Formata o valor de um rank para exibição.
     * Se o ponto for 10, retorna "A"; caso contrário, retorna o número como String.
     */
    public String verificandoPontosCartas(int ponto) { //
        return ponto == 10 ? "A" : String.valueOf(ponto); //
    }

    /**
     * Gera uma representação visual da carta em formato de array de Strings,
     * útil para exibição em console ou debug.
     */
    public String[] toStringVisual() { //
        String linha1 = "+-----+"; //
        String linha2 = String.format("|  %s  |", verificandoPontosCartas(topo)); //
        String linha3 = String.format("|%s   %s|", verificandoPontosCartas(esquerda), verificandoPontosCartas(direita)); //
        String linha4 = String.format("|  %s  |", verificandoPontosCartas(baixo)); //
        String linha5 = "+-----+"; //

        return new String[]{linha1, linha2, linha3, linha4, linha5}; //
    }

    // Getters
    public int getId() {return id;} //
    public int getTopo() { return topo; } //
    public int getBaixo() { return baixo; } //
    public int getEsquerda() { return esquerda; } //
    public int getDireita() { return direita; } //
    public String getNome() { return nome; } //
    public String getTipo() { return tipo; } //

    // Getter e Setter para o dono da carta
    public Jogador getDono() { return dono; } //
    public void setDono(Jogador dono) { this.dono = dono; } //

    /**
     * Compara esta carta com outro objeto para igualdade.
     * Duas cartas são consideradas iguais se seus IDs forem iguais.
     */
    @Override
    public boolean equals(Object o) { //
        if (this == o) return true; //
        if (o == null || getClass() != o.getClass()) return false; //
        Carta carta = (Carta) o; //
        return id == carta.id; // Compara pelo ID //
    }

    /**
     * Retorna um valor de hash code para esta carta.
     * O hash code é baseado no ID da carta.
     */
    @Override
    public int hashCode() { //
        return Integer.hashCode(id); // Usar o hash do ID //
    }
}