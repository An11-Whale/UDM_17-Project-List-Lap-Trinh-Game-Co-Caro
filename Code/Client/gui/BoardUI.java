package Code.Client.gui;

import Code.Client.Game.GameManager;
import Code.Client.Network.ClientSocket;

public class BoardUI extends javax.swing.JFrame implements GameManager.GameListener, TimerUI.TimerListener {

    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;
    private static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 40;

    private javax.swing.JButton[][] boardButtons;
    private TimerUI timerPanel;
    private GameManager gameManager;
    private int currentPlayer = PLAYER_X;
    private int myPlayerId = PLAYER_X;
    private Runnable lobbyCallback;
    private ClientSocket client;
    private String myUsername = "Player";
    private String opponentName = "Opponent";
    private javax.swing.JLabel lblTurnIndicator;

    public BoardUI() {
        initComponents();
        setupDarkTheme();
        createBoard();
        setupTimer();
    }

    public BoardUI(String username, ClientSocket client, int myPlayerId, String opponentName) {
        this.client = client;
        this.myPlayerId = myPlayerId;
        this.myUsername = username;
        this.opponentName = opponentName;
        initComponents();
        setupDarkTheme();
        createBoard();
        setupTimer();

        // Dat ten player
        if (myPlayerId == PLAYER_X) {
            setPlayerNames(username, opponentName);
        } else {
            setPlayerNames(opponentName, username);
        }

        if (this.client != null) {
            GameManager gm = new GameManager(client.getSocketHandler(), myPlayerId);
            setGameManager(gm);
            client.getSocketHandler().setGameManager(gm);

            client.setListener(new ClientSocket.ClientListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onLogin(boolean success, String message) {
                }

                @Override
                public void onGameStart(int myId, String opName) {
                }

                @Override
                public void onMove(int row, int col, int player) {
                }

                @Override
                public void onMessage(String msg) {
                }

                @Override
                public void onPlayersList(String[] players) {
                }

                @Override
                public void onChallengeFrom(String fromUser) {
                }

                @Override
                public void onChallengeAccepted() {
                }

                @Override
                public void onChallengeDeclined(String byUser) {
                }

                @Override
                public void onChallengeCancelled(String byUser) {
                }

                @Override
                public void onHistoryData(String data) {
                }

                @Override
                public void onOpponentSurrendered() {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        if (timerPanel != null)
                            timerPanel.stopTimer();
                        if (gameManager != null)
                            gameManager.forceGameOver();
                        javax.swing.JOptionPane.showMessageDialog(BoardUI.this,
                                "Đối thủ đã đầu hàng!\nBạn thắng!", "Kết thúc",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        returnToLobby();
                    });
                }

                @Override
                public void onDisconnected() {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        if (timerPanel != null)
                            timerPanel.stopTimer();
                        javax.swing.JOptionPane.showMessageDialog(BoardUI.this,
                                "Mất kết nối với Server!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                        dispose();
                        new LoginUI().setVisible(true);
                    });
                }
            });

            // Player 1 di truoc -> start timer
            timerPanel.resetTurn(1);
        }
    }

    private void setupTimer() {
        timerPanel = new TimerUI(60);
        timerPanel.setTimerListener(this);

        // Chen timerPanel vao giua headerPanel va boardContainer
        getContentPane().add(timerPanel, java.awt.BorderLayout.NORTH);

        // Chuyen headerPanel sang trong timerPanel phia tren
        getContentPane().remove(headerPanel);

        // Tao wrapper panel chua header + timer + turn indicator
        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.setBackground(new java.awt.Color(18, 22, 36));
        topPanel.add(headerPanel);
        topPanel.add(timerPanel);

        // Them turn indicator
        javax.swing.JPanel turnRow = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4));
        turnRow.setBackground(new java.awt.Color(18, 22, 36));
        turnRow.add(lblTurnIndicator);
        topPanel.add(turnRow);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        // Khoi tao trang thai ban dau - Player 1 di truoc
        updateTurnIndicator(PLAYER_X);

        pack();
        setLocationRelativeTo(null);
    }

    private void setupDarkTheme() {
        java.awt.Color bg = new java.awt.Color(18, 22, 36);
        java.awt.Color panelBg = new java.awt.Color(28, 33, 52);
        getContentPane().setBackground(bg);
        headerPanel.setBackground(panelBg);
        boardContainer.setBackground(bg);
        lblPlayer1.setForeground(new java.awt.Color(80, 200, 255));
        lblVs.setForeground(new java.awt.Color(120, 130, 160));
        lblPlayer2.setForeground(new java.awt.Color(255, 100, 100));
        btnBack.setBackground(new java.awt.Color(45, 52, 78));
        btnBack.setForeground(java.awt.Color.WHITE);
        btnBack.setFocusPainted(false);
        btnSurrender.setBackground(new java.awt.Color(180, 50, 50));
        btnSurrender.setForeground(java.awt.Color.WHITE);
        btnSurrender.setFocusPainted(false);

        // Tạo label chỉ báo lượt chơi
        lblTurnIndicator = new javax.swing.JLabel("Lượt của bạn");
        lblTurnIndicator.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblTurnIndicator.setForeground(new java.awt.Color(50, 200, 80));
        lblTurnIndicator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTurnIndicator.setOpaque(true);
        lblTurnIndicator.setBackground(new java.awt.Color(30, 60, 40));
        lblTurnIndicator.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 200, 80), 1),
                javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12)));
    }

    private void updateTurnIndicator(int playerId) {
        boolean isMyTurn = (playerId == myPlayerId);

        if (isMyTurn) {
            lblTurnIndicator.setText("Lượt của bạn");
            lblTurnIndicator.setForeground(new java.awt.Color(50, 255, 100));
            lblTurnIndicator.setBackground(new java.awt.Color(30, 60, 40));
            lblTurnIndicator.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 200, 80), 1),
                    javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        } else {
            lblTurnIndicator.setText("Đợi đối thủ...");
            lblTurnIndicator.setForeground(new java.awt.Color(255, 200, 50));
            lblTurnIndicator.setBackground(new java.awt.Color(60, 50, 30));
            lblTurnIndicator.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 200, 50), 1),
                    javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        }

        // Highlight tên người chơi đang đánh
        if (playerId == PLAYER_X) {
            lblPlayer1.setForeground(new java.awt.Color(80, 220, 255));
            lblPlayer1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
            lblPlayer2.setForeground(new java.awt.Color(120, 60, 60));
            lblPlayer2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        } else {
            lblPlayer2.setForeground(new java.awt.Color(255, 120, 120));
            lblPlayer2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
            lblPlayer1.setForeground(new java.awt.Color(50, 100, 120));
            lblPlayer1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        }
    }

    private void createBoard() {
        boardPanel.setLayout(new java.awt.GridLayout(BOARD_SIZE, BOARD_SIZE, 1, 1));
        boardPanel.setBackground(new java.awt.Color(50, 70, 110));
        boardButtons = new javax.swing.JButton[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int r = row, c = col;
                javax.swing.JButton btn = new javax.swing.JButton();
                btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
                btn.setMargin(new java.awt.Insets(0, 0, 0, 0));
                btn.setBackground(new java.awt.Color(35, 40, 58));
                btn.setForeground(java.awt.Color.WHITE);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new java.awt.Dimension(CELL_SIZE, CELL_SIZE));
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.addActionListener(e -> handleCellClick(r, c));
                boardButtons[row][col] = btn;
                boardPanel.add(btn);
            }
        }
        pack();
        setLocationRelativeTo(null);
    }

    private void handleCellClick(int row, int col) {
        if (!boardButtons[row][col].getText().isEmpty())
            return;
        if (gameManager != null) {
            gameManager.makeMove(row, col);
        } else {
            updateBoardUI(row, col, currentPlayer);
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
    }

    public void updateBoardUI(int row, int col, int playerId) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JButton btn = boardButtons[row][col];
            if (playerId == PLAYER_X) {
                btn.setText("X");
                btn.setForeground(new java.awt.Color(80, 200, 255));
            } else {
                btn.setText("O");
                btn.setForeground(new java.awt.Color(255, 100, 100));
            }
        });
    }

    public void resetBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boardButtons[row][col].setText("");
                boardButtons[row][col].setBackground(new java.awt.Color(35, 40, 58));
            }
        }
        currentPlayer = PLAYER_X;
        if (timerPanel != null)
            timerPanel.resetTimer();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        lblPlayer1 = new javax.swing.JLabel();
        lblVs = new javax.swing.JLabel();
        btnSurrender = new javax.swing.JButton();
        lblPlayer2 = new javax.swing.JLabel();
        boardContainer = new javax.swing.JPanel();
        boardPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caro Game");
        setMinimumSize(new java.awt.Dimension(700, 780));
        setResizable(false);

        headerPanel.setPreferredSize(new java.awt.Dimension(700, 50));
        headerPanel.setLayout(null);

        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBack.setText("Lobby");
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        headerPanel.add(btnBack);
        btnBack.setBounds(10, 10, 80, 30);

        lblPlayer1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblPlayer1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPlayer1.setText("Player 1");
        headerPanel.add(lblPlayer1);
        lblPlayer1.setBounds(150, 12, 150, 22);

        lblVs.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVs.setText("VS");
        headerPanel.add(lblVs);
        lblVs.setBounds(310, 12, 40, 22);

        btnSurrender.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSurrender.setText("Đầu hàng");
        btnSurrender.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSurrender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSurrenderActionPerformed(evt);
            }
        });
        headerPanel.add(btnSurrender);
        btnSurrender.setBounds(570, 10, 100, 30);

        lblPlayer2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblPlayer2.setText("Player 2");
        headerPanel.add(lblPlayer2);
        lblPlayer2.setBounds(360, 10, 140, 22);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        boardContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        boardPanel.setPreferredSize(new java.awt.Dimension(614, 614));
        boardContainer.add(boardPanel);

        getContentPane().add(boardContainer, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void returnToLobby() {
        if (timerPanel != null)
            timerPanel.stopTimer();
        dispose();
        if (lobbyCallback != null)
            lobbyCallback.run();
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed
        if (gameManager != null && gameManager.isGameOver()) {
            returnToLobby();
            return;
        }

        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Thoát trận đấu?", "Xác nhận", javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            if (timerPanel != null)
                timerPanel.stopTimer();
            if (gameManager != null)
                gameManager.forceGameOver();

            // Gui SURRENDER len server
            if (client != null) {
                client.getSocketHandler().send("SURRENDER");
            }
            String winner = (myPlayerId == PLAYER_X) ? lblPlayer2.getText() : lblPlayer1.getText();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Bạn đã đầu hàng!\n" + winner + " thắng!",
                    "Kết thúc", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            returnToLobby();
        }
    }// GEN-LAST:event_btnBackActionPerformed

    private void btnSurrenderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSurrenderActionPerformed
        if (gameManager != null && gameManager.isGameOver()) {
            return;
        }

        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn đầu hàng?\nBạn sẽ thua trận này!",
                "Đầu hàng", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            if (timerPanel != null)
                timerPanel.stopTimer();
            if (gameManager != null)
                gameManager.forceGameOver();

            // Gui SURRENDER len server
            if (client != null) {
                client.getSocketHandler().send("SURRENDER");
            }
            String winner = (myPlayerId == PLAYER_X) ? lblPlayer2.getText() : lblPlayer1.getText();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Bạn đã đầu hàng!\n" + winner + " thắng!",
                    "Kết thúc", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            returnToLobby();
        }
    }// GEN-LAST:event_btnSurrenderActionPerformed

    // GameManager.GameListener
    @Override
    public void onWin(int playerId) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (timerPanel != null)
                timerPanel.stopTimer();
            String winner = (playerId == PLAYER_X) ? lblPlayer1.getText() : lblPlayer2.getText();
            javax.swing.JOptionPane.showMessageDialog(this, "Bạn đã thắng!", "Kết thúc",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // Gui ket qua len server
            if (client != null) {
                String winnerName = (playerId == myPlayerId) ? myUsername : opponentName;
                String loserName = (playerId == myPlayerId) ? opponentName : myUsername;
                client.sendGameResult(winnerName, loserName, "normal");
            }
            returnToLobby();
        });
    }

    @Override
    public void onLose() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (timerPanel != null)
                timerPanel.stopTimer();
            javax.swing.JOptionPane.showMessageDialog(this, "Bạn đã thua!", "Kết thúc",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            returnToLobby();
        });
    }

    @Override
    public void onMove(int row, int col, int playerId) {
        updateBoardUI(row, col, playerId);
    }

    @Override
    public void onDraw(int playerId) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (timerPanel != null)
                timerPanel.stopTimer();
            javax.swing.JOptionPane.showMessageDialog(this, "Hòa!", "Kết thúc",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                if (client != null) {
                String winnerName = (playerId == myPlayerId) ? myUsername : opponentName;
                String loserName = (playerId == myPlayerId) ? opponentName : myUsername;
                client.sendGameResult(winnerName, loserName, "draw");
            }
            returnToLobby();
        });
    }

    @Override
    public void onReset() {
        resetBoard();
    }

    @Override
    public void onTurnChanged(int playerId) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (timerPanel != null) {
                timerPanel.resetTurn(playerId);
            }
            updateTurnIndicator(playerId);
        });
    }

    // TimerUI.TimerListener
    @Override
    public void onTimeOut(int playerId) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (gameManager != null && gameManager.isGameOver())
                return;
            if (gameManager != null)
                gameManager.forceGameOver();

            String loser = (playerId == PLAYER_X) ? lblPlayer1.getText() : lblPlayer2.getText();
            javax.swing.JOptionPane.showMessageDialog(this, loser + " hết thời gian!", "Hết giờ",
                    javax.swing.JOptionPane.WARNING_MESSAGE);

            // Gui ket qua len server (nguoi het gio = thua)
            if (client != null) {
                boolean iMeTimedOut = (playerId == myPlayerId);
                String winnerName = iMeTimedOut ? opponentName : myUsername;
                String loserName = iMeTimedOut ? myUsername : opponentName;
                client.sendGameResult(winnerName, loserName, "timeout");
            }
            returnToLobby();
        });
    }

    // Setter
    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
        this.gameManager.setListener(this);
    }

    public void setPlayerNames(String name1, String name2) {
        lblPlayer1.setText(name1);
        lblPlayer2.setText(name2);
        if (timerPanel != null) {
            timerPanel.setPlayerNames(name1, name2);
        }
    }

    public void setLobbyCallback(Runnable cb) {
        this.lobbyCallback = cb;
    }

    public void setMyPlayerId(int id) {
        this.myPlayerId = id;
    }

    public javax.swing.JButton[][] getBoardButtons() {
        return boardButtons;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            BoardUI board = new BoardUI();
            board.setPlayerNames("Ninh", "Paper Man");
            board.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boardContainer;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSurrender;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblPlayer1;
    private javax.swing.JLabel lblPlayer2;
    private javax.swing.JLabel lblVs;
    // End of variables declaration//GEN-END:variables
}