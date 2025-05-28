import javax.swing.*;
import java.awt.*;

public class TripleTriadGUI extends JFrame {

    // 1. REDUZIR O TAMANHO DAS CARTAS
    private static final int CARD_WIDTH = 90;  // Reduzido de 300
    private static final int CARD_HEIGHT = 120; // Reduzido de 300

    public TripleTriadGUI() {
        setTitle("Triple Triad Game Board (Responsive)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 2. ADAPTAR HANDPANEL PARA USAR JSCROLLPANE (OPCIONAL, MAS BOM PARA RESPONSIVIDADE)
        // Criar os painéis de mão primeiro, para depois envolvê-los em JScrollPane
        JPanel player2HandContent = createHandContentPanel(5);
        JScrollPane player2ScrollPane = new JScrollPane(player2HandContent);
        player2ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        player2ScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        player2ScrollPane.setBorder(BorderFactory.createTitledBorder("Player 2 Hand (Top)")); // Título no ScrollPane

        JPanel player1HandContent = createHandContentPanel(5);
        JScrollPane player1ScrollPane = new JScrollPane(player1HandContent);
        player1ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        player1ScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        player1ScrollPane.setBorder(BorderFactory.createTitledBorder("Player 1 Hand (Bottom)")); // Título no ScrollPane


        add(player2ScrollPane, BorderLayout.NORTH);
        add(createGameBoardPanel(), BorderLayout.CENTER);
        add(player1ScrollPane, BorderLayout.SOUTH);

        // Os painéis laterais também se beneficiarão da redução do tamanho da carta
        add(createSideTrianglePanel("Left Triangle", true), BorderLayout.WEST);
        add(createSideTrianglePanel("Right Triangle", false), BorderLayout.EAST);

        // Definir um tamanho preferencial inicial razoável para o Frame
        setPreferredSize(new Dimension(1024, 768)); // Ajuste conforme necessário
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createCardPlaceholder(String label) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT)); // Usa os novos tamanhos
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardPanel.setBackground(Color.LIGHT_GRAY);
        JLabel cardLabel = new JLabel(label, SwingConstants.CENTER);
        cardPanel.add(cardLabel, BorderLayout.CENTER);
        return cardPanel;
    }

    // Método modificado para criar o conteúdo da mão, que será colocado no JScrollPane
    private JPanel createHandContentPanel(int cardCount) {
        JPanel handContentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        // Não colocar borda de título aqui, será no JScrollPane
        for (int i = 0; i < cardCount; i++) {
            handContentPanel.add(createCardPlaceholder("Card"));
        }
        // Definir um tamanho preferencial para o painel de conteúdo para o JScrollPane funcionar bem
        // Se todas as cartas couberem, a barra de rolagem pode não aparecer.
        // A altura deve ser um pouco maior que a altura da carta + paddings.
        // A largura pode ser a soma das larguras das cartas + espaçamentos.
        int preferredWidth = (CARD_WIDTH + 10) * cardCount + 10;
        int preferredHeight = CARD_HEIGHT + 20;
        handContentPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        return handContentPanel;
    }


    private JPanel createGameBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBorder(BorderFactory.createTitledBorder("Game Board"));
        for (int i = 0; i < 9; i++) {
            boardPanel.add(createCardPlaceholder("Slot"));
        }
        // O tamanho preferido do board panel será calculado automaticamente pelo GridLayout
        // e pelos preferredSize dos card placeholders.
        return boardPanel;
    }

    private JPanel createSideTrianglePanel(String title, boolean isLeft) {
        JPanel sidePanel = new JPanel(new BorderLayout(10,10));

        JPanel peakPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        peakPanel.add(createCardPlaceholder("Card"));

        JPanel basePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        basePanel.add(createCardPlaceholder("Card"));
        basePanel.add(createCardPlaceholder("Card"));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(peakPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(basePanel);
        contentPanel.add(Box.createVerticalGlue());

        sidePanel.add(contentPanel, BorderLayout.CENTER);
        sidePanel.setBorder(BorderFactory.createTitledBorder(title));


        // O tamanho preferido do painel lateral também diminuirá devido às cartas menores.
        // A largura será CARD_WIDTH * 2 + espaçamentos (para a base)
        // A altura será CARD_HEIGHT * 2 + espaçamentos (para pico e base empilhados)
        int preferredWidth = CARD_WIDTH * 2 + 20 + (2*10); // 2 cards wide + flowlayout hgaps + borderlayout hgap
        int preferredHeight = CARD_HEIGHT * 2 + 10 + (2*10); // 2 cards high + rigid area + borderlayout vgap
        sidePanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        return sidePanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TripleTriadGUI());
    }
}