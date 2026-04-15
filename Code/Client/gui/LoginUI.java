package Code.Client.gui;

import Code.Client.Network.ClientSocket;

public class LoginUI extends javax.swing.JFrame {

    public LoginUI() {
        initComponents();
        setupDarkTheme();
    }

    private void setupDarkTheme() {
        getContentPane().setBackground(new java.awt.Color(18, 22, 36));
        mainPanel.setBackground(new java.awt.Color(28, 33, 52));
        lblTitle.setForeground(new java.awt.Color(220, 225, 240));
        lblSubtitle.setForeground(new java.awt.Color(120, 130, 160));
        lblUsername.setForeground(new java.awt.Color(220, 225, 240));
        lblPassword.setForeground(new java.awt.Color(220, 225, 240));
        lblError.setForeground(new java.awt.Color(255, 90, 90));
        txtUsername.setBackground(new java.awt.Color(38, 44, 68));
        txtUsername.setForeground(new java.awt.Color(220, 225, 240));
        txtUsername.setCaretColor(new java.awt.Color(220, 225, 240));
        txtPassword.setBackground(new java.awt.Color(38, 44, 68));
        txtPassword.setForeground(new java.awt.Color(220, 225, 240));
        txtPassword.setCaretColor(new java.awt.Color(220, 225, 240));
        btnLogin.setBackground(new java.awt.Color(80, 160, 255));
        btnLogin.setForeground(java.awt.Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnRegister.setBackground(new java.awt.Color(50, 180, 120));
        btnRegister.setForeground(java.awt.Color.WHITE);
        btnRegister.setFocusPainted(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblError = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Caro Game - Đăng nhập");
        setMinimumSize(new java.awt.Dimension(470, 550));
        setResizable(false);
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                formComponentAdded(evt);
            }
        });
        getContentPane().setLayout(null);

        mainPanel.setLayout(null);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("CARO GAME");
        mainPanel.add(lblTitle);
        lblTitle.setBounds(20, 30, 340, 40);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitle.setText("Đăng nhập để chơi");
        mainPanel.add(lblSubtitle);
        lblSubtitle.setBounds(20, 70, 340, 25);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblUsername.setText("Tài khoản");
        mainPanel.add(lblUsername);
        lblUsername.setBounds(30, 120, 320, 20);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mainPanel.add(txtUsername);
        txtUsername.setBounds(30, 145, 320, 38);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPassword.setText("Mật khẩu");
        mainPanel.add(lblPassword);
        lblPassword.setBounds(30, 200, 320, 20);

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mainPanel.add(txtPassword);
        txtPassword.setBounds(30, 225, 320, 38);

        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText(" ");
        mainPanel.add(lblError);
        lblError.setBounds(30, 275, 320, 20);

        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogin.setText("ĐĂNG NHẬP");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        mainPanel.add(btnLogin);
        btnLogin.setBounds(30, 310, 320, 42);

        btnRegister.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRegister.setText("ĐĂNG KÝ");
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });
        mainPanel.add(btnRegister);
        btnRegister.setBounds(30, 365, 320, 42);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(40, 40, 390, 440);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentAdded(java.awt.event.ContainerEvent evt) {// GEN-FIRST:event_formComponentAdded
        // TODO add your handling code here:
    }// GEN-LAST:event_formComponentAdded

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLoginActionPerformed
        String user = txtUsername.getText().trim();
        String pass = String.valueOf(txtPassword.getPassword());
        if (user.isEmpty()) {
            lblError.setText("Vui lòng nhập tài khoản!");
            return;
        }
        if (pass.isEmpty()) {
            lblError.setText("Vui lòng nhập mật khẩu!");
            return;
        }
        ClientSocket client = new ClientSocket();
        if (!client.connect("127.0.0.1", 12345)) {
            lblError.setText("Lỗi kết nối Server!");
            return;
        }
        client.setListener(new ClientSocket.ClientListener() {
            @Override public void onConnected() {}
            @Override public void onLogin(boolean success, String message) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (success) {
                        dispose();
                        new LobbyUI(user, client).setVisible(true);
                    } else {
                        lblError.setForeground(new java.awt.Color(255, 90, 90));
                        lblError.setText("Lỗi: " + message);
                    }
                });
            }
            @Override public void onGameStart(int myId, String opponentName) {}
            @Override public void onMove(int row, int col, int player) {}
            @Override public void onMessage(String msg) {}
            @Override public void onDisconnected() {}
            @Override public void onPlayersList(String[] players) {}
            @Override public void onChallengeFrom(String fromUser) {}
            @Override public void onChallengeAccepted() {}
            @Override public void onChallengeDeclined(String byUser) {}
            @Override public void onHistoryData(String data) {}
            @Override public void onOpponentSurrendered() {}
        });
        lblError.setForeground(new java.awt.Color(220, 225, 240));
        lblError.setText("Đang đăng nhập...");
        client.login(user, pass);
    }// GEN-LAST:event_btnLoginActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRegisterActionPerformed
        String user = txtUsername.getText().trim();
        String pass = String.valueOf(txtPassword.getPassword());
        if (user.isEmpty()) {
            lblError.setText("Vui lòng nhập tài khoản!");
            return;
        }
        if (pass.isEmpty()) {
            lblError.setText("Vui lòng nhập mật khẩu!");
            return;
        }
        ClientSocket client = new ClientSocket();
        if (!client.connect("127.0.0.1", 12345)) {
            lblError.setText("Lỗi kết nối Server!");
            return;
        }
        client.setListener(new ClientSocket.ClientListener() {
            @Override public void onConnected() {}
            @Override public void onLogin(boolean success, String message) {}
            @Override public void onGameStart(int myId, String opponentName) {}
            @Override public void onMove(int row, int col, int player) {}
            @Override public void onMessage(String msg) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (msg.startsWith("REGISTER_SUCCESS")) {
                        lblError.setForeground(new java.awt.Color(50, 200, 120));
                        lblError.setText("Đăng ký thành công!");
                        client.disconnect();
                    } else if (msg.startsWith("REGISTER_ERROR")) {
                        lblError.setForeground(new java.awt.Color(255, 90, 90));
                        lblError.setText("Lỗi: " + msg.replace("REGISTER_ERROR", "").trim());
                        client.disconnect();
                    }
                });
            }
            @Override public void onDisconnected() {}
            @Override public void onPlayersList(String[] players) {}
            @Override public void onChallengeFrom(String fromUser) {}
            @Override public void onChallengeAccepted() {}
            @Override public void onChallengeDeclined(String byUser) {}
            @Override public void onHistoryData(String data) {}
            @Override public void onOpponentSurrendered() {}
        });
        lblError.setForeground(new java.awt.Color(220, 225, 240));
        lblError.setText("Đang xử lý...");
        client.register(user, pass);
    }// GEN-LAST:event_btnRegisterActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new LoginUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
