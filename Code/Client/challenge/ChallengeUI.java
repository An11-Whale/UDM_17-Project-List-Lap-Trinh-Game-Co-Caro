package Code.Client.challenge;

import Code.Client.gui.BoardUI;
import Code.Client.Network.ClientSocket;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeUI extends javax.swing.JFrame {

    private String myUsername;
    private Runnable backCallback;
    private List<PlayerInfo> onlinePlayers;
    private ClientSocket client;
    private JDialog waitingDialog;
    private String challengeTarget;
    private JDialog incomingChallengeDialog;

    public ChallengeUI(String myUsername, ClientSocket client) {
        this.myUsername = myUsername;
        this.client = client;
        onlinePlayers = new ArrayList<>();

        initComponents();
        setupDarkTheme();
        setupClientListener();

        if (client != null) {
            client.getPlayers();
        } else {
            // Fallback for UI testing
            loadFakePlayers();
            refreshList();
        }
    }

    private void setupClientListener() {
        if (client != null) {
            client.setListener(new ClientSocket.ClientListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onLogin(boolean success, String message) {
                }

                @Override
                public void onGameStart(int myId, String opponentName) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        closeWaitingDialog();
                        dispose();
                        BoardUI board = new BoardUI(myUsername, client, myId, opponentName);
                        board.setLobbyCallback(backCallback);
                        board.setVisible(true);
                    });
                }

                @Override
                public void onMove(int row, int col, int player) {
                }

                @Override
                public void onMessage(String msg) {
                    if (msg.startsWith("CHALLENGE_ERROR")) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            closeWaitingDialog();
                            JOptionPane.showMessageDialog(ChallengeUI.this,
                                    "Lỗi thách đấu: " + msg.substring(15), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }

                @Override
                public void onDisconnected() {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        closeWaitingDialog();
                        JOptionPane.showMessageDialog(ChallengeUI.this,
                                "Mất kết nối với Server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        dispose();
                        Code.Client.gui.LoginUI login = new Code.Client.gui.LoginUI();
                        login.setVisible(true);
                    });
                }

                @Override
                public void onPlayersList(String[] players) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        onlinePlayers.clear();
                        for (String p : players) {
                            if (!p.isEmpty()) {
                                // Default fake stats for now, server only sends usernames
                                onlinePlayers.add(new PlayerInfo(p, "Online", 0, 0, 1000));
                            }
                        }
                        refreshList();
                    });
                }

                @Override
                public void onChallengeFrom(String fromUser) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Dong dialog cu neu co
                        closeIncomingChallengeDialog();

                        incomingChallengeDialog = new JDialog(ChallengeUI.this, "Lời mời thách đấu", false);
                        incomingChallengeDialog.setLayout(new java.awt.BorderLayout(10, 10));
                        incomingChallengeDialog.setSize(350, 150);
                        incomingChallengeDialog.setLocationRelativeTo(ChallengeUI.this);

                        JLabel lblMsg = new JLabel(fromUser + " muốn thách đấu với bạn. Chấp nhận?", SwingConstants.CENTER);
                        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        incomingChallengeDialog.add(lblMsg, java.awt.BorderLayout.CENTER);

                        JPanel btnPanel = new JPanel();
                        JButton btnAccept = new JButton("Chấp nhận");
                        JButton btnDecline = new JButton("Từ chối");

                        btnAccept.addActionListener(ev -> {
                            client.acceptChallenge(fromUser);
                            closeIncomingChallengeDialog();
                        });
                        btnDecline.addActionListener(ev -> {
                            client.declineChallenge(fromUser);
                            closeIncomingChallengeDialog();
                        });

                        btnPanel.add(btnAccept);
                        btnPanel.add(btnDecline);
                        incomingChallengeDialog.add(btnPanel, java.awt.BorderLayout.SOUTH);

                        incomingChallengeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        incomingChallengeDialog.setVisible(true);
                    });
                }

                @Override
                public void onChallengeAccepted() {
                    // Wait for START message to transition, handleGameStart will trigger
                }

                @Override
                public void onChallengeDeclined(String byUser) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        closeWaitingDialog();
                        JOptionPane.showMessageDialog(ChallengeUI.this,
                                byUser + " đã từ chối lời thách đấu.", "Từ chối", JOptionPane.INFORMATION_MESSAGE);
                    });
                }

                @Override
                public void onChallengeCancelled(String byUser) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        closeIncomingChallengeDialog();
                        JOptionPane.showMessageDialog(ChallengeUI.this,
                                byUser + " đã hủy lời mời thách đấu.", "Đã hủy", JOptionPane.INFORMATION_MESSAGE);
                    });
                }

                @Override
                public void onHistoryData(String data) {
                }

                @Override
                public void onOpponentSurrendered() {
                }
            });
        }
    }

    private void loadFakePlayers() {
        onlinePlayers.add(new PlayerInfo("Paper Man", "Online", 15, 8, 1850));
        onlinePlayers.add(new PlayerInfo("Dark Knight", "Online", 22, 12, 2100));
        onlinePlayers.add(new PlayerInfo("StarGamer", "Online", 10, 5, 1600));
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
                player.status);
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
                if (client != null) {
                    client.challenge(player.name);
                    showWaitingDialog(player.name);
                } else {
                    int r = JOptionPane.showConfirmDialog(this, "Thách đấu " + player.name + "?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        dispose();
                        BoardUI board = new BoardUI();
                        board.setPlayerNames(myUsername, player.name);
                        board.setLobbyCallback(backCallback);
                        board.setVisible(true);
                    }
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

    private void showWaitingDialog(String targetUser) {
        challengeTarget = targetUser;
        waitingDialog = new JDialog(this, "Đang chờ", true);
        waitingDialog.setLayout(new BorderLayout());
        waitingDialog.setSize(300, 150);
        waitingDialog.setLocationRelativeTo(this);

        JLabel lblWait = new JLabel("Đang đợi " + targetUser + " chấp nhận...", SwingConstants.CENTER);
        waitingDialog.add(lblWait, BorderLayout.CENTER);

        JButton btnCancel = new JButton("Hủy lời mời");
        btnCancel.addActionListener(e -> {
            if (client != null && challengeTarget != null) {
                client.cancelChallenge(challengeTarget);
            }
            challengeTarget = null;
            closeWaitingDialog();
        });
        JPanel bottom = new JPanel();
        bottom.add(btnCancel);
        waitingDialog.add(bottom, BorderLayout.SOUTH);

        waitingDialog.setVisible(true);
    }

    private void closeWaitingDialog() {
        if (waitingDialog != null) {
            waitingDialog.dispose();
            waitingDialog = null;
        }
    }

    private void closeIncomingChallengeDialog() {
        if (incomingChallengeDialog != null) {
            incomingChallengeDialog.dispose();
            incomingChallengeDialog = null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        if (client != null) {
            client.getPlayers();
        } else {
            loadFakePlayers();
            refreshList();
        }
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
        java.awt.EventQueue.invokeLater(() -> new ChallengeUI("TestUser", null).setVisible(true));
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
