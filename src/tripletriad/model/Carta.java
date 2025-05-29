package tripletriad.model;

public class Carta {
    private int id;
    private String nome;
    private int topo;
    private int direita;
    private int baixo;
    private int esquerda;
    private String tipo;
    private Jogador dono;

    public Carta(int id, String nome, int topo, int direita, int baixo, int esquerda, String tipo) {
        this.id = id;
        this.nome = nome;
        this.topo = topo;
        this.direita = direita;
        this.baixo = baixo;
        this.esquerda = esquerda;
        this.tipo = tipo;
    }

    public String verificandoPontosCartas(int ponto) {
        return ponto == 10 ? "A" : String.valueOf(ponto);
    }

    public String[] toStringVisual() {
        String linha1 = "+-----+";
        String linha2 = String.format("|  %s  |", verificandoPontosCartas(topo));
        String linha3 = String.format("|%s   %s|", verificandoPontosCartas(esquerda), verificandoPontosCartas(direita));
        String linha4 = String.format("|  %s  |", verificandoPontosCartas(baixo));
        String linha5 = "+-----+";

        return new String[]{linha1, linha2, linha3, linha4, linha5};
    }

    public int getId() {return id;}
    public int getTopo() { return topo; }
    public int getBaixo() { return baixo; }
    public int getEsquerda() { return esquerda; }
    public int getDireita() { return direita; }

    public Jogador getDono() { return dono; }
    public void setDono(Jogador dono) { this.dono = dono; }

    public String getNome() { return nome; }

    public String getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carta carta = (Carta) o;
        return id == carta.id; // Comparar pelo ID
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id); // Usar o hash do ID
    }
}
