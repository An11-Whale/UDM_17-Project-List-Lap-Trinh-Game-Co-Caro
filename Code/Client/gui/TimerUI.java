package Code.Client.Gui;
import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;

public class TimerUI extends JFrame {
    private JLabel lblTimer;
    private JButton btnSwitchTurn, btnStart;

    private int time = 10;
    private int currentPlayer = 1;

    private Timer timer;

    public TimerUI() {
        setTitle("Caro Timer");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //label hien thi
        lblTimer = new JLabel("Player 1: 10", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 20));

        //button de test start & doi luot
        btnStart = new JButton("Start");
        btnSwitchTurn = new JButton("Doi luot");

        //layout
        setLayout(new GridLayout(3, 1));
        add(lblTimer);
        add(btnStart);
        add(btnSwitchTurn);

        //timer chay moi 1s
        timer = new Timer(1000, e -> {
            time--;
            lblTimer.setText("Player " + currentPlayer + ": " + time);

            if (time == 0) {
                timer.stop();
                JOptionPane.showMessageDialog(TimerUI.this,
                        "Player " + currentPlayer + " het thoi gian! Thua!");
            }
        });

        //start game
        btnStart.addActionListener(e -> {
            resetTurn();
            timer.start();
        });

        //doi luot
        btnSwitchTurn.addActionListener(e -> {
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            resetTurn(); //reset time
        });
    }
    //reset timer
        private void resetTurn() {
            time = 10;
            lblTimer.setText("Player " + currentPlayer + ": " + time);
        }

    public static void main(String[] args) {
        new TimerUI().setVisible(true);
    }
}


