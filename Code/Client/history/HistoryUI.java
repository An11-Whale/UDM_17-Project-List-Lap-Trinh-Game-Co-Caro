package Code.Client.history;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryUI extends javax.swing.JFrame {

    private String myUsername;
    private Runnable backCallback;
    private List<MatchRecord> matchHistory;

    public HistoryUI(String myUsername) {
        this.myUsername = myUsername;
        loadHistory();
        initComponents();
        setupDarkTheme();
        refreshHistory();
    }

    private void loadHistory() {
        matchHistory = new ArrayList<>();
        matchHistory.add(new MatchRecord("Paper Man", true, "13/04/2026 11:30", "3:45", 42));
        matchHistory.add(new MatchRecord("Dark Knight", false, "13/04/2026 10:15", "5:00", 68));
        matchHistory.add(new MatchRecord("StarGamer", true, "12/04/2026 20:30", "2:18", 31));
        matchHistory.add(new MatchRecord("ProX99", false, "12/04/2026 18:45", "4:52", 55));
        matchHistory.add(new MatchRecord("MinhHoang", true, "11/04/2026 15:20", "1:55", 28));
        matchHistory.add(new MatchRecord("CatoGirl", true, "11/04/2026 14:00", "3:10", 38));
        matchHistory.add(new MatchRecord("AnhKhoa", false, "10/04/2026 21:30", "4:30", 50));
        matchHistory.add(new MatchRecord("ThuanVN", true, "10/04/2026 19:00", "2:45", 35));
    }

    private void setupDarkTheme() {
        Color bg = new Color(18, 22, 36);
        Color panelBg = new Color(28, 33, 52);
        getContentPane().setBackground(bg);
        headerPanel.setBackground(panelBg);
        statsPanel.setBackground(panelBg);
        scrollPane.getViewport().setBackground(bg);
        historyListPanel.setBackground(bg);
        lblTitle.setForeground(new Color(220, 225, 240));
        btnBack.setBackground(new Color(45, 52, 78));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        lblTotal.setForeground(new Color(80, 160, 255));
        lblWins.setForeground(new Color(50, 200, 120));
        lblLosses.setForeground(new Color(255, 90, 90));
        lblRate.setForeground(new Color(255, 200, 50));
        lblTotalLabel.setForeground(new Color(120, 130, 160));
        lblWinsLabel.setForeground(new Color(120, 130, 160));
        lblLossesLabel.setForeground(new Color(120, 130, 160));
        lblRateLabel.setForeground(new Color(120, 130, 160));
    }

    private void refreshHistory() {
        historyListPanel.removeAll();
        int wins = 0, losses = 0;
        for (MatchRecord m : matchHistory) {
            if (m.isWin)
                wins++;
            else
                losses++;
            historyListPanel.add(createMatchCard(m));
            historyListPanel.add(Box.createVerticalStrut(6));
        }
        int total = matchHistory.size();
        double rate = total > 0 ? (wins * 100.0 / total) : 0;
        lblTotal.setText(String.valueOf(total));
        lblWins.setText(String.valueOf(wins));
        lblLosses.setText(String.valueOf(losses));
        lblRate.setText(String.format("%.0f%%", rate));
        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    private JPanel createMatchCard(MatchRecord match) {
        Color rc = match.isWin ? new Color(50, 200, 120) : new Color(255, 90, 90);
        String rt = match.isWin ? "THẮNG" : "THUA";

        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(32, 38, 58));
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel vs = new JLabel("vs " + match.opponent);
        vs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vs.setForeground(new Color(220, 225, 240));
        info.add(vs);
        JLabel details = new JLabel(match.date + " | " + match.duration + " | " + match.totalMoves + " nước");
        details.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        details.setForeground(new Color(120, 130, 160));
        info.add(details);
        card.add(info, BorderLayout.CENTER);

        // Result
        JLabel result = new JLabel(rt, SwingConstants.CENTER);
        result.setFont(new Font("Segoe UI", Font.BOLD, 13));
        result.setForeground(rc);
        result.setPreferredSize(new Dimension(60, 30));
        card.add(result, BorderLayout.EAST);

        return card;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        statsPanel = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblWins = new javax.swing.JLabel();
        lblLosses = new javax.swing.JLabel();
        lblRate = new javax.swing.JLabel();
        lblTotalLabel = new javax.swing.JLabel();
        lblWinsLabel = new javax.swing.JLabel();
        lblLossesLabel = new javax.swing.JLabel();
        lblRateLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        historyListPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caro - Lịch sử");
        setMinimumSize(new java.awt.Dimension(520, 600));
        setPreferredSize(new java.awt.Dimension(520, 600));
        setResizable(false);

        headerPanel.setPreferredSize(new java.awt.Dimension(520, 55));
        headerPanel.setLayout(null);

        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBack.setText("Quay lại");
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        headerPanel.add(btnBack);
        btnBack.setBounds(10, 12, 90, 30);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Lịch sử trản đấu");
        headerPanel.add(lblTitle);
        lblTitle.setBounds(120, 12, 280, 30);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        statsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 30));
        statsPanel.setPreferredSize(new java.awt.Dimension(520, 60));
        statsPanel.setLayout(new java.awt.GridLayout(2, 4, 10, 2));

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("0");
        statsPanel.add(lblTotal);

        lblWins.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblWins.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWins.setText("0");
        statsPanel.add(lblWins);

        lblLosses.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblLosses.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLosses.setText("0");
        statsPanel.add(lblLosses);

        lblRate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblRate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRate.setText("0%");
        statsPanel.add(lblRate);

        lblTotalLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblTotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalLabel.setText("Tổng");
        statsPanel.add(lblTotalLabel);

        lblWinsLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWinsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWinsLabel.setText("Thắng");
        statsPanel.add(lblWinsLabel);

        lblLossesLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblLossesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLossesLabel.setText("Thua");
        statsPanel.add(lblLossesLabel);

        lblRateLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRateLabel.setText("Tỉ lệ");
        statsPanel.add(lblRateLabel);

        getContentPane().add(statsPanel, java.awt.BorderLayout.SOUTH);

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        historyListPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        historyListPanel.setLayout(new javax.swing.BoxLayout(historyListPanel, javax.swing.BoxLayout.Y_AXIS));
        scrollPane.setViewportView(historyListPanel);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed
        dispose();
        if (backCallback != null)
            backCallback.run();
    }// GEN-LAST:event_btnBackActionPerformed

    public void setBackCallback(Runnable cb) {
        this.backCallback = cb;
    }

    public void updateHistory(List<MatchRecord> h) {
        this.matchHistory = h;
        refreshHistory();
    }

    public static class MatchRecord {
        public String opponent, date, duration;
        public boolean isWin;
        public int totalMoves;

        public MatchRecord(String o, boolean w, String d, String dur, int m) {
            opponent = o;
            isWin = w;
            date = d;
            duration = dur;
            totalMoves = m;
        }
    }

    // Test
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new HistoryUI("TestUser").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel historyListPanel;
    private javax.swing.JLabel lblLosses;
    private javax.swing.JLabel lblLossesLabel;
    private javax.swing.JLabel lblRate;
    private javax.swing.JLabel lblRateLabel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalLabel;
    private javax.swing.JLabel lblWins;
    private javax.swing.JLabel lblWinsLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel statsPanel;
    // End of variables declaration//GEN-END:variables
}
