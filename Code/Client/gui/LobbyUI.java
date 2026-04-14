package Code.Client.gui;

import Code.Client.challenge.ChallengeUI;
import Code.Client.history.HistoryUI;
import Code.Client.gui.LoginUI;
import Code.Client.Network.ClientSocket;

public class LobbyUI extends javax.swing.JFrame {

    private String username;
    private ClientSocket client;

    public LobbyUI(String username, ClientSocket client) {
        this.username = username;
        this.client = client;
        initComponents();
        setupDarkTheme();
        lblUsername.setText(username);
        
        if (this.client != null) {
            this.client.setListener(new ClientSocket.ClientListener() {
                @Override public void onConnected() {}
                @Override public void onLogin(boolean success, String message) {}
                @Override public void onGameStart(int myId) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        dispose();
                        BoardUI board = new BoardUI(username, LobbyUI.this.client, myId);
                        board.setVisible(true);
                    });
                }
                @Override public void onMove(int row, int col, int player) {}
                @Override public void onMessage(String msg) {}
                @Override public void onDisconnected() {}
            });
        }
    }

    private void setupDarkTheme() {
        getContentPane().setBackground(new java.awt.Color(18, 22, 36));
        headerPanel.setBackground(new java.awt.Color(28, 33, 52));
        contentPanel.setBackground(new java.awt.Color(18, 22, 36));
        lblUsername.setForeground(new java.awt.Color(220, 225, 240));
        lblStatus.setForeground(new java.awt.Color(50, 200, 120));
        lblWelcome.setForeground(new java.awt.Color(220, 225, 240));
        lblDesc.setForeground(new java.awt.Color(120, 130, 160));
        btnChallenge.setBackground(new java.awt.Color(80, 160, 255));
        btnChallenge.setForeground(java.awt.Color.WHITE);
        btnChallenge.setFocusPainted(false);
        btnHistory.setBackground(new java.awt.Color(255, 160, 50));
        btnHistory.setForeground(java.awt.Color.WHITE);
        btnHistory.setFocusPainted(false);
        btnLogout.setBackground(new java.awt.Color(255, 90, 90));
        btnLogout.setForeground(java.awt.Color.WHITE);
        btnLogout.setFocusPainted(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblDesc = new javax.swing.JLabel();
        btnChallenge = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Caro Game - Lobby");
        setMinimumSize(new java.awt.Dimension(520, 500));
        setPreferredSize(new java.awt.Dimension(520, 500));
        setResizable(false);

        headerPanel.setPreferredSize(new java.awt.Dimension(520, 70));
        headerPanel.setLayout(null);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblUsername.setText("Player");
        headerPanel.add(lblUsername);
        lblUsername.setBounds(20, 12, 200, 25);

        lblStatus.setText("Online");
        headerPanel.add(lblStatus);
        lblStatus.setBounds(20, 38, 100, 20);

        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLogout.setText("Đăng xuất");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        headerPanel.add(btnLogout);
        btnLogout.setBounds(400, 20, 100, 32);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        contentPanel.setLayout(null);

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWelcome.setText("Chào mừng đến Caro Game!");
        contentPanel.add(lblWelcome);
        lblWelcome.setBounds(20, 30, 480, 35);

        lblDesc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDesc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDesc.setText("Chọn chức năng bên dưỚi");
        contentPanel.add(lblDesc);
        lblDesc.setBounds(20, 65, 480, 25);

        btnChallenge.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnChallenge.setText("Thách đấu");
        btnChallenge.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChallenge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChallengeActionPerformed(evt);
            }
        });
        contentPanel.add(btnChallenge);
        btnChallenge.setBounds(60, 130, 400, 80);

        btnHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHistory.setText("Lịch sử trản đấu");
        btnHistory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });
        contentPanel.add(btnHistory);
        btnHistory.setBounds(60, 240, 400, 80);

        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLogoutActionPerformed
        if (client != null) {
            client.disconnect();
        }
        dispose();
        new LoginUI().setVisible(true);
    }// GEN-LAST:event_btnLogoutActionPerformed

    private void btnChallengeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnChallengeActionPerformed
        dispose();
        ChallengeUI challenge = new ChallengeUI(username);
        challenge.setBackCallback(() -> new LobbyUI(username, client).setVisible(true));
        challenge.setVisible(true);
    }// GEN-LAST:event_btnChallengeActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHistoryActionPerformed
        dispose();
        HistoryUI history = new HistoryUI(username);
        history.setBackCallback(() -> new LobbyUI(username, client).setVisible(true));
        history.setVisible(true);
    }// GEN-LAST:event_btnHistoryActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new LobbyUI("TestUser", null).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChallenge;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblWelcome;
    // End of variables declaration//GEN-END:variables
}
