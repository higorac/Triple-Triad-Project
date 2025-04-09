package tripletriad.model;

public class Carta {
    private int topo;
    private int direita;
    private int baixo;
    private int esquerda;
    private String tipo;
    private Jogador dono;

    public Carta(int topo, int direita, int baixo, int esquerda, String tipo, Jogador dono) {
        this.topo = topo;
        this.direita = direita;
        this.baixo = baixo;
        this.esquerda = esquerda;
        this.tipo = tipo;
        this.dono = dono;
    }

    public String[] toStringVisual() {
        String linha1 = "+-----+";
        String linha2 = String.format("|  %s  |", String.valueOf(tipo.charAt(0)));
        String linha3 = String.format("|%s %s|", String.valueOf(esquerda), String.valueOf(direita));
        String linha4 = String.format("|  %s  |", String.valueOf(baixo));
        String linha5 = "+-----+";

        return new String[]{linha1, linha2, linha3, linha4, linha5};
    }

    public int getTopo() { return topo; }
    public int getBaixo() { return baixo; }
    public int getEsquerda() { return esquerda; }
    public int getDireita() { return direita; }

    public Jogador getDono() { return dono; }
    public void setDono(Jogador dono) { this.dono = dono; }

    public String getTipo() {
        return tipo;
    }
}
