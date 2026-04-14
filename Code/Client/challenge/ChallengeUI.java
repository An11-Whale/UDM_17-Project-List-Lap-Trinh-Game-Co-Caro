package Code.Client.challenge;

import Code.Client.gui.BoardUI;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeUI extends javax.swing.JFrame {

    private String myUsername;
    private Runnable backCallback;
    private List<PlayerInfo> onlinePlayers;

    public ChallengeUI(String myUsername) {
        this.myUsername = myUsername;
        loadPlayers();
        initComponents();
        setupDarkTheme();
        refreshList();
    }

    private void loadPlayers() {
        onlinePlayers = new ArrayList<>();
        onlinePlayers.add(new PlayerInfo("Paper Man", "Online", 15, 8, 1850));
        onlinePlayers.add(new PlayerInfo("Dark Knight", "Online", 22, 12, 2100));
        onlinePlayers.add(new PlayerInfo("StarGamer", "Online", 10, 5, 1600));
        onlinePlayers.add(new PlayerInfo("ProX99", "Đang chơi", 30, 20, 2350));
        onlinePlayers.add(new PlayerInfo("MinhHoang", "Online", 8, 3, 1450));
        onlinePlayers.add(new PlayerInfo("CatoGirl", "Online", 18, 9, 1780));
        onlinePlayers.add(new PlayerInfo("ThuanVN", "Đang chơi", 25, 15, 2050));
        onlinePlayers.add(new PlayerInfo("AnhKhoa", "Online", 12, 6, 1700));
    }

    private void setupDarkTheme() {
        Color bg = new Color(18, 22, 36);
        Color panelBg = new Color(28, 33, 52);
        getContentPane().setBackground(bg);
        headerPanel.setBackground(panelBg);
        scrollPane.getViewport().setBackground(bg);
        playerListPanel.setBackground(bg);
        footerPanel.setBackground(panelBg);
        lblTitle.setForeground(new Color(220, 225, 240));
        lblOnline.setForeground(new Color(50, 200, 120));
        btnBack.setBackground(new Color(45, 52, 78));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnRefresh.setBackground(new Color(45, 52, 78));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
    }

    private void refreshList() {
        playerListPanel.removeAll();
        for (PlayerInfo p : onlinePlayers) {
            JPanel card = createPlayerCard(p);
            playerListPanel.add(card);
            playerListPanel.add(Box.createVerticalStrut(6));
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
        long count = onlinePlayers.stream().filter(pl -> "Online".equals(pl.status)).count();
        lblOnline.setText(count + " online");
    }

    private JPanel createPlayerCard(PlayerInfo player) {
        boolean busy = "Đang chơi".equals(player.status);
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(32, 38, 58));
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(player.name);
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(new Color(220, 225, 240));
        info.add(name);
        JLabel stats = new JLabel(
                player.status + " | ELO: " + player.elo + " | W:" + player.wins + " L:" + player.losses);
        stats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stats.setForeground(busy ? new Color(255, 160, 50) : new Color(50, 200, 120));
        info.add(stats);
        card.add(info, BorderLayout.CENTER);

        // Button
        if (!busy) {
            JButton btn = new JButton("Thách đấu");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBackground(new Color(80, 160, 255));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(90, 32));
            btn.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(this, "Thách đấu " + player.name + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    dispose();
                    BoardUI board = new BoardUI();
                    board.setPlayerNames(myUsername, player.name);
                    board.setLobbyCallback(backCallback);
                    board.setVisible(true);
                }
            });
            card.add(btn, BorderLayout.EAST);
        } else {
            JLabel lblBusy = new JLabel("Đang bận");
            lblBusy.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblBusy.setForeground(new Color(120, 130, 160));
            card.add(lblBusy, BorderLayout.EAST);
        }
        return card;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblOnline = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        playerListPanel = new javax.swing.JPanel();
        footerPanel = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caro - Thách đấu");
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
        lblTitle.setText("Danh sách người chơi");
        headerPanel.add(lblTitle);
        lblTitle.setBounds(120, 12, 280, 30);

        lblOnline.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblOnline.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOnline.setText("0 online");
        headerPanel.add(lblOnline);
        lblOnline.setBounds(420, 10, 90, 30);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        playerListPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        playerListPanel.setLayout(new javax.swing.BoxLayout(playerListPanel, javax.swing.BoxLayout.Y_AXIS));
        scrollPane.setViewportView(playerListPanel);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        footerPanel.setPreferredSize(new java.awt.Dimension(520, 45));

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRefresh.setText("Làm mới");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        footerPanel.add(btnRefresh);

        getContentPane().add(footerPanel, java.awt.BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed
        dispose();
        if (backCallback != null)
            backCallback.run();
    }// GEN-LAST:event_btnBackActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRefreshActionPerformed
        loadPlayers();
        refreshList();
    }// GEN-LAST:event_btnRefreshActionPerformed

    public void setBackCallback(Runnable cb) {
        this.backCallback = cb;
    }

    public void updatePlayerList(List<PlayerInfo> players) {
        this.onlinePlayers = players;
        refreshList();
    }

    public static class PlayerInfo {
        public String name, status;
        public int wins, losses, elo;

        public PlayerInfo(String n, String s, int w, int l, int e) {
            name = n;
            status = s;
            wins = w;
            losses = l;
            elo = e;
        }
    }

    // Test
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ChallengeUI("TestUser").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblOnline;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel playerListPanel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}

