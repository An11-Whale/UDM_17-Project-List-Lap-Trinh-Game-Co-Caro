package Code.GUI.GUI_History;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI_History extends JFrame {

    static class Match {
        String result;
        String player1, player2;

        public Match(String result, String player1, String player2) {
            this.result = result;
            this.player1 = player1;
            this.player2 = player2;
        }
    }

    private Match[] testMatch = new Match[] { 
        new Match("win", "Alice", "Bob") 
    };

    public GUI_History() {
        setTitle("Lịch Sử Ván Đấu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (Match m : testMatch) {
            listPanel.add(createMatchRow(m));
            listPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
    }

    public void setMatchHistory() {

    }

    private JPanel createMatchRow(Match match) {
        JPanel row = new JPanel();
        row.setLayout(new GridLayout(1, 4, 10, 0));
        row.setBackground(new Color(61, 57, 53));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(800, 60));

        JLabel iconLabel = new JLabel();
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        if (match.result.equals("win")) {
            iconLabel.setText("WIN");
            iconLabel.setForeground(new Color(129, 182, 76));
        } else if (match.result.equals("loss")) {
            iconLabel.setText("LOSS");
            iconLabel.setForeground(new Color(250, 65, 45));
        } else {
            iconLabel.setText("DRAW");
            iconLabel.setForeground(Color.GRAY);
        }
        row.add(iconLabel);

        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);

        JLabel whiteLbl = new JLabel("O " + match.player1);
        whiteLbl.setForeground(Color.WHITE);
        JLabel blackLbl = new JLabel("X " + match.player2);
        blackLbl.setForeground(Color.LIGHT_GRAY);

        playersPanel.add(whiteLbl);
        playersPanel.add(blackLbl);
        row.add(playersPanel);

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.setBackground(new Color(75, 72, 68));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.setBackground(new Color(61, 57, 53));
            }
        });

        return row;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            new GUI_History().setVisible(true);
        });
    }
}
