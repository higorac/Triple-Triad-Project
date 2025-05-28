package tripletriad.gui;

import javax.swing.*;
import java.awt.*;

public class TripleTriadGUI extends JFrame {
    public TripleTriadGUI() {
        setTitle("Triple Triad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centraliza a janela

        initUI();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Painel superior com nomes dos jogadores
        JPanel topo = new JPanel(new GridLayout(1, 2));
        topo.add(new JLabel("Jogador 1"));
        topo.add(new JLabel("Jogador 2"));
        add(topo, BorderLayout.NORTH);

        // Painel central com tabuleiro 3x3
        JPanel tabuleiro = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            JButton botao = new JButton(" ");
            tabuleiro.add(botao);
        }
        add(tabuleiro, BorderLayout.CENTER);

        // Painel inferior com mensagens e controle
        JPanel rodape = new JPanel(new BorderLayout());
        JLabel status = new JLabel("Bem-vindo ao Triple Triad!");
        rodape.add(status, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TripleTriadGUI());
    }
}
