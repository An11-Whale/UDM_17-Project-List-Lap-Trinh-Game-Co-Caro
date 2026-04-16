package Code.Client.gui;

import javax.swing.*;
import java.awt.*;

public class TimerUI extends JPanel {

    public static final int DEFAULT_TURN_TIME = 60;

    // MÀU SẮC
    private static final Color COLOR_BG = new Color(25, 30, 45);
    private static final Color COLOR_TIMER_ACTIVE = new Color(50, 200, 80);
    private static final Color COLOR_TIMER_INACTIVE = new Color(40, 45, 65);
    private static final Color COLOR_TIMER_WARNING = new Color(255, 80, 80);
    private static final Color COLOR_TEXT_WHITE = new Color(220, 225, 240);
    private static final Color COLOR_HINT = new Color(120, 130, 160);
    private static final Color COLOR_GOLD = new Color(255, 200, 50);
    private static final Color COLOR_CYAN = new Color(80, 200, 255);

    // BIẾN
    private int remainingTime;
    private int turnTimeLimit;
    private int currentPlayer;
    private Timer turnTimer;
    private JLabel lblTimerP1;
    private JLabel lblTimerP2;
    private JLabel lblNameP1;
    private JLabel lblNameP2;
    private String nameP1 = "Player 1";
    private String nameP2 = "Player 2";

    // LISTENER
    public interface TimerListener {
        void onTimeOut(int playerId);
    }

    private TimerListener listener;

    // Named inner class thay cho anonymous class (avatar)
    static class AvatarPanel extends JPanel {
        private final Color borderColor;
        private final String symbol;

        AvatarPanel(Color borderColor, String symbol) {
            this.borderColor = borderColor;
            this.symbol = symbol;
            setOpaque(false);
            setPreferredSize(new Dimension(50, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
            g2.setColor(new Color(45, 50, 70));
            g2.fillOval(x + 3, y + 3, size - 6, size - 6);
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(borderColor);
            g2.drawOval(x + 2, y + 2, size - 4, size - 4);
            g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(symbol, x + (size - fm.stringWidth(symbol)) / 2,
                    y + (size - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // Named inner class thay cho anonymous class (score circle)
    static class ScoreCircleLabel extends JLabel {
        private final Color circleColor;

        ScoreCircleLabel(Color color) {
            this.circleColor = color;
            setPreferredSize(new Dimension(18, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(circleColor);
            g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
            g2.dispose();
        }
    }

    public TimerUI() {
        this(DEFAULT_TURN_TIME);
    }

    public TimerUI(int turnTimeLimit) {
        this.turnTimeLimit = turnTimeLimit;
        this.remainingTime = turnTimeLimit;
        this.currentPlayer = 1;
        initUI();
        setupTimer();
    }

    private void initUI() {
        setBackground(COLOR_BG);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Player 1 (trái)
        JPanel panelP1 = createPlayerPanel(true);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(panelP1, gbc);

        // Score (giữa)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(new ScoreCircleLabel(COLOR_GOLD));
        centerPanel.add(new ScoreCircleLabel(new Color(100, 110, 130)));
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 15, 0, 15);
        add(centerPanel, gbc);

        // Player 2 (phải)
        JPanel panelP2 = createPlayerPanel(false);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(panelP2, gbc);
    }

    private JPanel createPlayerPanel(boolean isPlayer1) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Tên + Avatar
        JPanel nameRow = new JPanel(new FlowLayout(isPlayer1 ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 0));
        nameRow.setOpaque(false);

        JLabel lblName = new JLabel(isPlayer1 ? nameP1 : nameP2);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblName.setForeground(COLOR_TEXT_WHITE);

        JPanel avatar = new AvatarPanel(isPlayer1 ? COLOR_GOLD : COLOR_CYAN, isPlayer1 ? "X" : "O");

        if (isPlayer1) {
            nameRow.add(lblName);
            nameRow.add(avatar);
            lblNameP1 = lblName;
        } else {
            nameRow.add(avatar);
            nameRow.add(lblName);
            lblNameP2 = lblName;
        }
        panel.add(nameRow);

        // Timer
        JPanel timerRow = new JPanel(new FlowLayout(isPlayer1 ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        timerRow.setOpaque(false);

        JLabel lblTimer = new JLabel(formatTime(turnTimeLimit));
        lblTimer.setFont(new Font("Consolas", Font.BOLD, 14));
        lblTimer.setOpaque(true);
        lblTimer.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimer.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        if (isPlayer1) {
            lblTimer.setBackground(COLOR_TIMER_ACTIVE);
            lblTimer.setForeground(Color.BLACK);
            lblTimerP1 = lblTimer;
        } else {
            lblTimer.setBackground(COLOR_TIMER_INACTIVE);
            lblTimer.setForeground(COLOR_HINT);
            lblTimerP2 = lblTimer;
        }
        timerRow.add(lblTimer);
        panel.add(timerRow);
        return panel;
    }

    private void setupTimer() {
        turnTimer = new Timer(1000, e -> {
            remainingTime--;
            updateTimerDisplay();
            if (remainingTime <= 0) {
                turnTimer.stop();
                if (listener != null)
                    listener.onTimeOut(currentPlayer);
            }
        });
    }

    private void updateTimerDisplay() {
        JLabel activeLabel = (currentPlayer == 1) ? lblTimerP1 : lblTimerP2;
        activeLabel.setText(formatTime(remainingTime));
        if (remainingTime <= 10) {
            activeLabel.setBackground(COLOR_TIMER_WARNING);
            activeLabel.setForeground(Color.WHITE);
        }
    }

    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    // PUBLIC METHODS
    public void resetTurn(int playerId) {
        turnTimer.stop();
        currentPlayer = playerId;
        remainingTime = turnTimeLimit;
        if (playerId == 1) {
            lblTimerP1.setText(formatTime(turnTimeLimit));
            lblTimerP1.setBackground(COLOR_TIMER_ACTIVE);
            lblTimerP1.setForeground(Color.BLACK);
            lblTimerP2.setBackground(COLOR_TIMER_INACTIVE);
            lblTimerP2.setForeground(COLOR_HINT);
        } else {
            lblTimerP2.setText(formatTime(turnTimeLimit));
            lblTimerP2.setBackground(COLOR_TIMER_ACTIVE);
            lblTimerP2.setForeground(Color.BLACK);
            lblTimerP1.setBackground(COLOR_TIMER_INACTIVE);
            lblTimerP1.setForeground(COLOR_HINT);
        }
        turnTimer.start();
    }

    public void stopTimer() {
        if (turnTimer != null && turnTimer.isRunning())
            turnTimer.stop();
    }

    public void setTimerListener(TimerListener listener) {
        this.listener = listener;
    }

    public void setPlayerNames(String name1, String name2) {
        this.nameP1 = name1;
        this.nameP2 = name2;
        if (lblNameP1 != null)
            lblNameP1.setText(name1);
        if (lblNameP2 != null)
            lblNameP2.setText(name2);
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setTurnTimeLimit(int seconds) {
        this.turnTimeLimit = seconds;
    }

    public void resetTimer() {
        stopTimer();
        remainingTime = turnTimeLimit;
        currentPlayer = 1;
        if (lblTimerP1 != null) {
            lblTimerP1.setText(formatTime(turnTimeLimit));
            lblTimerP1.setBackground(COLOR_TIMER_ACTIVE);
            lblTimerP1.setForeground(Color.BLACK);
        }
        if (lblTimerP2 != null) {
            lblTimerP2.setText(formatTime(turnTimeLimit));
            lblTimerP2.setBackground(COLOR_TIMER_INACTIVE);
            lblTimerP2.setForeground(COLOR_HINT);
        }
    }
}
