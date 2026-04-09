package Code.GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GUIBoard extends JFrame {

    
    public static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 42;
    private static final int BOARD_PADDING = 30;
    private static final int INFO_PANEL_WIDTH = 230;

    //BANG MAU
    // Nen tong the
    private static final Color COLOR_BG = new Color(18, 18, 35);
    // Panel thong tin
    private static final Color COLOR_PANEL = new Color(24, 24, 50);
    private static final Color COLOR_PANEL_BORDER = new Color(55, 55, 90);
    private static final Color COLOR_CARD = new Color(32, 32, 62);
    private static final Color COLOR_CARD_ACTIVE = new Color(42, 42, 78);
    // Ban co
    private static final Color COLOR_BOARD = new Color(222, 196, 154);
    private static final Color COLOR_BOARD_EDGE = new Color(185, 158, 115);
    private static final Color COLOR_GRID = new Color(160, 135, 100);
    // Quan co
    private static final Color COLOR_X = new Color(66, 165, 245);
    private static final Color COLOR_X_DARK = new Color(33, 130, 210);
    private static final Color COLOR_O = new Color(239, 83, 80);
    private static final Color COLOR_O_DARK = new Color(200, 50, 47);
    // Hieu ung
    private static final Color COLOR_HOVER_X = new Color(66, 165, 245, 70);
    private static final Color COLOR_HOVER_O = new Color(239, 83, 80, 70);
    private static final Color COLOR_LAST_MOVE = new Color(255, 193, 7);
    private static final Color COLOR_WIN_BG = new Color(76, 175, 80, 100);
    // Text
    private static final Color COLOR_TEXT = new Color(224, 224, 230);
    private static final Color COLOR_TEXT_DIM = new Color(140, 140, 160);
    private static final Color COLOR_ACCENT = new Color(126, 87, 194);

    //TRANG THAI GAME
    private int[][] board;          // 0 = trong, 1 = X, 2 = O
    private boolean myTurn;         // true = luot minh
    private int myMark;             // 1 = X, 2 = O
    private boolean gameOver = false;
    private int hoverRow = -1, hoverCol = -1;
    private int lastRow = -1, lastCol = -1;
    private int[][] winCells = null; // 5 o thang
    private int moveCount = 0;
    private int pendingMoveRow = -1, pendingMoveCol = -1; // nuoc di cho xu ly sau reset
    private String playerXName = "Player X";
    private String playerOName = "Player O";

    //THANH PHAN UI
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel moveCountLabel;
    private JPanel xCard, oCard;
    private JLabel xTurnDot, oTurnDot;
    private JButton newGameBtn;

    //CALLBACK KET NOI MANG
    /**
     * Interface de gui nuoc di qua mang.
     * Khi nguoi choi click vao ban co, onMoveMade() se duoc goi.
     * Client.java co the set listener nay de gui "MOVE row col" den server.
     */
    public interface MoveListener {
        void onMoveMade(int row, int col);
    }
    private MoveListener moveListener;

    // Listener gui yeu cau van moi qua mang
    private Runnable newGameRequestListener;
    private boolean myNewGameReady = false;
    private boolean opponentNewGameReady = false;

    public GUIBoard(int myMark) {
        this.myMark = myMark;
        this.myTurn = (myMark == 1); // X luon di truoc
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        initUI();
    }

    //KHOI TAO GIAO DIEN
    private void initUI() {
        setTitle("Cờ Caro — Gomoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));

        // Panel chinh chua ban co
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Panel thong tin ben phai
        add(createInfoPanel(), BorderLayout.EAST);

        // Status bar phia duoi
        add(createStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        updateTurnDisplay();
    }

    /**
     * Tao panel thong tin ben phai (nguoi choi, luot di, nut moi).
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, COLOR_PANEL_BORDER));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Tieu de
        JLabel title = new JLabel("CỜ CARO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        panel.add(title);

        JLabel subtitle = new JLabel("Gomoku 15×15");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(COLOR_TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        panel.add(Box.createVerticalStrut(25));

        // Card Player X
        xCard = createPlayerCard("X", playerXName, COLOR_X, myMark == 1);
        panel.add(xCard);
        panel.add(Box.createVerticalStrut(10));

        // Card Player O
        oCard = createPlayerCard("O", playerOName, COLOR_O, myMark == 2);
        panel.add(oCard);

        panel.add(Box.createVerticalStrut(25));

        // So luot di
        JLabel moveTitle = new JLabel("LƯỢT ĐI");
        moveTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        moveTitle.setForeground(COLOR_TEXT_DIM);
        moveTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(moveTitle);
        panel.add(Box.createVerticalStrut(4));

        moveCountLabel = new JLabel("0");
        moveCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        moveCountLabel.setForeground(COLOR_TEXT);
        moveCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(moveCountLabel);

        panel.add(Box.createVerticalGlue());

        // Nut van moi
        newGameBtn = new JButton("VÁN MỚI");
        newGameBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newGameBtn.setForeground(Color.WHITE);
        newGameBtn.setBackground(COLOR_ACCENT);
        newGameBtn.setFocusPainted(false);
        newGameBtn.setBorderPainted(false);
        newGameBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameBtn.setMaximumSize(new Dimension(180, 40));
        newGameBtn.setPreferredSize(new Dimension(180, 40));
        newGameBtn.addActionListener(e -> {
            if (newGameRequestListener != null) {
                // Che do mang: can ca 2 dong y
                requestNewGame();
            } else {
                // Che do local: reset truc tiep
                resetBoard();
            }
        });
        // Hieu ung cho nut
        newGameBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newGameBtn.setBackground(new Color(149, 105, 210));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                newGameBtn.setBackground(COLOR_ACCENT);
            }
        });
        panel.add(newGameBtn);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    /**
     * Tao card hien thi thong tin nguoi choi.
     */
    private JPanel createPlayerCard(String mark, String name, Color color, boolean isMe) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.X_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setMaximumSize(new Dimension(200, 60));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Dot chi thi luot choi (se duoc bat/tat)
        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dot.setForeground(COLOR_TEXT_DIM);
        dot.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        card.add(dot);

        // Luu reference de update
        if (mark.equals("X")) xTurnDot = dot;
        else oTurnDot = dot;

        // Ten va ky hieu
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel markLabel = new JLabel(mark);
        markLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        markLabel.setForeground(color);
        textPanel.add(markLabel);

        String roleText = isMe ? "(Bạn)" : "(Đối thủ)";
        JLabel roleLabel = new JLabel(name + " " + roleText);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(COLOR_TEXT_DIM);
        textPanel.add(roleLabel);

        card.add(textPanel);
        return card;
    }

    /**
     * Tao thanh trang thai phia duoi.
     */
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setBackground(COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PANEL_BORDER));
        bar.setPreferredSize(new Dimension(0, 36));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(COLOR_TEXT);
        bar.add(statusLabel);

        return bar;
    }

    /**
     * Cap nhat hien thi luot choi, trang thai.
     */
    private void updateTurnDisplay() {
        if (gameOver) return;

        int currentMark = myTurn ? myMark : (3 - myMark); // 1 hoac 2
        boolean isXTurn = (currentMark == 1);

        // Cap nhat dot indicator
        if (xTurnDot != null) {
            xTurnDot.setForeground(isXTurn ? COLOR_X : COLOR_TEXT_DIM);
        }
        if (oTurnDot != null) {
            oTurnDot.setForeground(!isXTurn ? COLOR_O : COLOR_TEXT_DIM);
        }

        // Cap nhat card highlight
        if (xCard != null) {
            xCard.setBackground(isXTurn ? COLOR_CARD_ACTIVE : COLOR_CARD);
        }
        if (oCard != null) {
            oCard.setBackground(!isXTurn ? COLOR_CARD_ACTIVE : COLOR_CARD);
        }

        // Cap nhat so luot
        if (moveCountLabel != null) {
            moveCountLabel.setText(String.valueOf(moveCount));
        }

        // Cap nhat status bar
        if (statusLabel != null) {
            if (myTurn) {
                String sym = (myMark == 1) ? "X" : "O";
                statusLabel.setText("⏳ Lượt của bạn — Đánh " + sym);
                statusLabel.setForeground(myMark == 1 ? COLOR_X : COLOR_O);
            } else {
                statusLabel.setText("⌛ Chờ đối thủ đánh...");
                statusLabel.setForeground(COLOR_TEXT_DIM);
            }
        }
    }

    //PANEL BAN CO
    /**
     * Panel ve ban co va xu ly su kien chuot.
     */
    private class BoardPanel extends JPanel {

        private int boardPixelSize = BOARD_PADDING * 2 + CELL_SIZE * (BOARD_SIZE - 1);

        BoardPanel() {
            setPreferredSize(new Dimension(boardPixelSize + 1, boardPixelSize + 1));
            setBackground(COLOR_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Su kien click
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int[] cell = getCellFromMouse(e.getX(), e.getY());
                    if (cell != null) {
                        handleCellClick(cell[0], cell[1]);
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverRow = -1;
                    hoverCol = -1;
                    repaint();
                }
            });

            // Su kien hover
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int[] cell = getCellFromMouse(e.getX(), e.getY());
                    int newRow = cell != null ? cell[0] : -1;
                    int newCol = cell != null ? cell[1] : -1;
                    if (newRow != hoverRow || newCol != hoverCol) {
                        hoverRow = newRow;
                        hoverCol = newCol;
                        repaint();
                    }
                }
            });
        }

        /**
         * Chuyen toa do chuot thanh chi so o [row, col].
         * Tra ve null neu chuot nam ngoai ban co.
         */
        private int[] getCellFromMouse(int mx, int my) {
            int col = Math.round((float)(mx - BOARD_PADDING) / CELL_SIZE);
            int row = Math.round((float)(my - BOARD_PADDING) / CELL_SIZE);
            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                return null;
            }
            // Kiem tra khoang cach den giao diem (chi nhan neu gan o)
            int cx = BOARD_PADDING + col * CELL_SIZE;
            int cy = BOARD_PADDING + row * CELL_SIZE;
            double dist = Math.sqrt((mx - cx) * (mx - cx) + (my - cy) * (my - cy));
            if (dist > CELL_SIZE * 0.48) {
                return null;
            }
            return new int[]{row, col};
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawBoardBackground(g2);
            drawGridLines(g2);
            drawStarPoints(g2);
            drawCoordinates(g2);
            drawWinHighlight(g2);
            drawPieces(g2);
            drawLastMoveIndicator(g2);
            drawHoverEffect(g2);
        }

        /**
         * Ve nen ban co (go).
         */
        private void drawBoardBackground(Graphics2D g2) {
            int size = CELL_SIZE * (BOARD_SIZE - 1) + BOARD_PADDING * 2;

            // Bong do
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(4, 4, size, size, 12, 12);

            // Vien ngoai
            g2.setColor(COLOR_BOARD_EDGE);
            g2.fillRoundRect(0, 0, size, size, 10, 10);

            // Nen go
            int inset = 6;
            g2.setColor(COLOR_BOARD);
            g2.fillRoundRect(inset, inset, size - inset * 2, size - inset * 2, 6, 6);

            // Tao hieu ung van go nhe (gradient)
            GradientPaint woodGrain = new GradientPaint(
                0, 0, new Color(230, 205, 165, 40),
                size, size, new Color(200, 170, 130, 40)
            );
            g2.setPaint(woodGrain);
            g2.fillRoundRect(inset, inset, size - inset * 2, size - inset * 2, 6, 6);
        }

        /**
         * Ve duong ke luoi.
         */
        private void drawGridLines(Graphics2D g2) {
            g2.setColor(COLOR_GRID);
            g2.setStroke(new BasicStroke(1.0f));

            for (int i = 0; i < BOARD_SIZE; i++) {
                int pos = BOARD_PADDING + i * CELL_SIZE;
                int start = BOARD_PADDING;
                int end = BOARD_PADDING + (BOARD_SIZE - 1) * CELL_SIZE;
                // Duong ngang
                g2.drawLine(start, pos, end, pos);
                // Duong doc
                g2.drawLine(pos, start, pos, end);
            }
        }

        /**
         * Ve cac diem sao (star points) tren ban co.
         */
        private void drawStarPoints(Graphics2D g2) {
            g2.setColor(COLOR_GRID);
            int[][] stars = {{3, 3}, {3, 11}, {7, 7}, {11, 3}, {11, 11}};
            for (int[] sp : stars) {
                int cx = BOARD_PADDING + sp[1] * CELL_SIZE;
                int cy = BOARD_PADDING + sp[0] * CELL_SIZE;
                g2.fillOval(cx - 4, cy - 4, 8, 8);
            }
        }

        /**
         * Ve nhan toa do (A-O, 1-15).
         */
        private void drawCoordinates(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(130, 110, 85));
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < BOARD_SIZE; i++) {
                int pos = BOARD_PADDING + i * CELL_SIZE;

                // So hang (1-15) ben trai
                String rowLabel = String.valueOf(i + 1);
                int rw = fm.stringWidth(rowLabel);
                g2.drawString(rowLabel, BOARD_PADDING - rw - 10, pos + fm.getAscent() / 2);

                // Chu cot (A-O) phia tren
                String colLabel = String.valueOf((char) ('A' + i));
                int cw = fm.stringWidth(colLabel);
                g2.drawString(colLabel, pos - cw / 2, BOARD_PADDING - 12);
            }
        }

        /**
         * Ve tat ca quan co tren ban.
         */
        private void drawPieces(Graphics2D g2) {
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] != 0) {
                        int cx = BOARD_PADDING + c * CELL_SIZE;
                        int cy = BOARD_PADDING + r * CELL_SIZE;
                        if (board[r][c] == 1) {
                            drawX(g2, cx, cy);
                        } else {
                            drawO(g2, cx, cy);
                        }
                    }
                }
            }
        }

        /**
         * Ve quan X (hai duong cheo).
         */
        private void drawX(Graphics2D g2, int cx, int cy) {
            int half = CELL_SIZE / 2 - 6;
            // Bong
            g2.setColor(new Color(0, 0, 0, 30));
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - half + 1, cy - half + 1, cx + half + 1, cy + half + 1);
            g2.drawLine(cx + half + 1, cy - half + 1, cx - half + 1, cy + half + 1);
            // Duong chinh
            g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COLOR_X_DARK);
            g2.drawLine(cx - half, cy - half, cx + half, cy + half);
            g2.drawLine(cx + half, cy - half, cx - half, cy + half);
            // Highlight
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COLOR_X);
            g2.drawLine(cx - half, cy - half, cx + half, cy + half);
            g2.drawLine(cx + half, cy - half, cx - half, cy + half);
        }

        /**
         * Ve quan O (hinh tron).
         */
        private void drawO(Graphics2D g2, int cx, int cy) {
            int radius = CELL_SIZE / 2 - 6;
            // Bong
            g2.setColor(new Color(0, 0, 0, 30));
            g2.setStroke(new BasicStroke(3.5f));
            g2.drawOval(cx - radius + 1, cy - radius + 1, radius * 2, radius * 2);
            // Duong chinh
            g2.setStroke(new BasicStroke(3.2f));
            g2.setColor(COLOR_O_DARK);
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            // Highlight
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(COLOR_O);
            g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }

        /**
         * Ve hieu ung hover (quan co mo khi re chuot).
         */
        private void drawHoverEffect(Graphics2D g2) {
            if (hoverRow < 0 || hoverCol < 0 || gameOver || !myTurn) return;
            if (board[hoverRow][hoverCol] != 0) return;

            int cx = BOARD_PADDING + hoverCol * CELL_SIZE;
            int cy = BOARD_PADDING + hoverRow * CELL_SIZE;

            if (myMark == 1) {
                // Hover X
                int half = CELL_SIZE / 2 - 6;
                g2.setColor(COLOR_HOVER_X);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - half, cy - half, cx + half, cy + half);
                g2.drawLine(cx + half, cy - half, cx - half, cy + half);
            } else {
                // Hover O
                int radius = CELL_SIZE / 2 - 6;
                g2.setColor(COLOR_HOVER_O);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            }
        }

        /**
         * Ve dau hieu nuoc di cuoi cung (cham vang).
         */
        private void drawLastMoveIndicator(Graphics2D g2) {
            if (lastRow < 0 || lastCol < 0) return;
            int cx = BOARD_PADDING + lastCol * CELL_SIZE;
            int cy = BOARD_PADDING + lastRow * CELL_SIZE;
            g2.setColor(COLOR_LAST_MOVE);
            g2.fillOval(cx - 4, cy - 4, 8, 8);
        }

        /**
         * Highlight 5 o thang.
         */
        private void drawWinHighlight(Graphics2D g2) {
            if (winCells == null) return;
            g2.setColor(COLOR_WIN_BG);
            for (int[] cell : winCells) {
                int cx = BOARD_PADDING + cell[1] * CELL_SIZE;
                int cy = BOARD_PADDING + cell[0] * CELL_SIZE;
                int r = CELL_SIZE / 2 - 2;
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            }
        }
    }

    //XU LY LOGIC
    /**
     * Xu ly khi nguoi choi click vao o.
     */
    private void handleCellClick(int row, int col) {
        if (gameOver || !myTurn) return;
        if (board[row][col] != 0) return;

        // Dat quan co
        placeMove(row, col, myMark);

        // Gui nuoc di qua mang TRUOC KHI kiem tra thang
        // (de doi thu luon nhan duoc nuoc di, ke ca nuoc thang)
        if (moveListener != null) {
            moveListener.onMoveMade(row, col);
        }

        // Kiem tra thang
        if (checkWin(row, col, myMark)) {
            winCells = getWinCells(row, col, myMark);
            gameOver = true;
            String sym = (myMark == 1) ? "X" : "O";
            statusLabel.setText("🎉 Bạn thắng! (" + sym + ")");
            statusLabel.setForeground(new Color(76, 175, 80));
            boardPanel.repaint();
            JOptionPane.showMessageDialog(this,
                "Chúc mừng! Bạn đã thắng!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Kiem tra hoa (ban co day)
        if (moveCount >= BOARD_SIZE * BOARD_SIZE) {
            gameOver = true;
            statusLabel.setText("🤝 Hòa!");
            statusLabel.setForeground(COLOR_TEXT);
            boardPanel.repaint();
            return;
        }

        // Chuyen luot
        myTurn = false;
        updateTurnDisplay();
        boardPanel.repaint();
    }

    /**
     * Dat quan co len ban.
     */
    private void placeMove(int row, int col, int mark) {
        board[row][col] = mark;
        lastRow = row;
        lastCol = col;
        moveCount++;
    }

    public void receiveOpponentMove(int row, int col) {
        SwingUtilities.invokeLater(() -> {
            if (gameOver) {
                // Luu nuoc di cho den khi board reset (race condition protection)
                pendingMoveRow = row;
                pendingMoveCol = col;
                return;
            }
            applyOpponentMove(row, col);
        });
    }

    /**
     * Xu ly nuoc di cua doi thu (dat quan, kiem tra thang, chuyen luot).
     */
    private void applyOpponentMove(int row, int col) {
        int opponentMark = 3 - myMark;

        placeMove(row, col, opponentMark);

        // Kiem tra doi thu thang
        if (checkWin(row, col, opponentMark)) {
            winCells = getWinCells(row, col, opponentMark);
            gameOver = true;
            String sym = (opponentMark == 1) ? "X" : "O";
            statusLabel.setText("😞 Đối thủ thắng! (" + sym + ")");
            statusLabel.setForeground(COLOR_O);
            boardPanel.repaint();
            JOptionPane.showMessageDialog(this,
                "Đối thủ đã thắng!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Chuyen luot ve minh
        myTurn = true;
        updateTurnDisplay();
        boardPanel.repaint();
    }

    //KIEM TRA THANG 
    /**
     * Kiem tra co 5 quan lien tiep khong.
     * Kiem tra 4 huong: ngang, doc, cheo phai, cheo trai.
     */
    private boolean checkWin(int row, int col, int mark) {
        return countDirection(row, col, mark, 0, 1) >= 5  // ngang
            || countDirection(row, col, mark, 1, 0) >= 5  // doc
            || countDirection(row, col, mark, 1, 1) >= 5  // cheo phai
            || countDirection(row, col, mark, 1, -1) >= 5; // cheo trai
    }

    /**
     * Dem so quan lien tiep theo 1 huong (ca 2 phia).
     */
    private int countDirection(int row, int col, int mark, int dr, int dc) {
        int count = 1;
        // Di ve phia duong
        count += countOneWay(row, col, mark, dr, dc);
        // Di ve phia am
        count += countOneWay(row, col, mark, -dr, -dc);
        return count;
    }

    private int countOneWay(int row, int col, int mark, int dr, int dc) {
        int count = 0;
        int r = row + dr, c = col + dc;
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == mark) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    /**
     * Lay danh sach 5 o thang de highlight.
     */
    private int[][] getWinCells(int row, int col, int mark) {
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int[] dir : directions) {
            int dr = dir[0], dc = dir[1];
            if (countDirection(row, col, mark, dr, dc) >= 5) {
                // Tim diem bat dau (di ve phia am)
                int r = row, c = col;
                while (r - dr >= 0 && r - dr < BOARD_SIZE
                    && c - dc >= 0 && c - dc < BOARD_SIZE
                    && board[r - dr][c - dc] == mark) {
                    r -= dr;
                    c -= dc;
                }
                // Lay 5 o tu diem bat dau
                int total = countDirection(row, col, mark, dr, dc);
                int[][] cells = new int[total][2];
                for (int i = 0; i < total; i++) {
                    cells[i][0] = r;
                    cells[i][1] = c;
                    r += dr;
                    c += dc;
                }
                return cells;
            }
        }
        return null;
    }

    //API CONG KHAI
    /**
     * Dat callback khi nguoi choi thuc hien nuoc di.
     */
    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }

    /**
     * Dat callback khi nguoi choi nhan "Van moi".
     * Su dung de gui "NEWGAME" qua mang.
     */
    public void setNewGameRequestListener(Runnable listener) {
        this.newGameRequestListener = listener;
    }

    /**
     * Dat ten nguoi choi.
     */
    public void setPlayerNames(String xName, String oName) {
        this.playerXName = xName;
        this.playerOName = oName;
    }

    /**
     * Nguoi choi nhan nut "Van moi" — gui yeu cau va cho doi thu.
     */
    private void requestNewGame() {
        if (!gameOver) return; // chi cho phep khi game da ket thuc
        myNewGameReady = true;
        newGameBtn.setEnabled(false);
        newGameBtn.setText("Đang chờ...");

        if (newGameRequestListener != null) {
            newGameRequestListener.run();
        }

        if (opponentNewGameReady) {
            // Doi thu da san sang truoc → reset ngay
            startNewGame();
        } else {
            statusLabel.setText("⏳ Chờ đối thủ đồng ý chơi lại...");
            statusLabel.setForeground(COLOR_TEXT_DIM);
        }
    }

    /**
     * Nhan yeu cau van moi tu doi thu (goi tu client receiver).
     */
    public void receiveNewGameRequest() {
        SwingUtilities.invokeLater(() -> {
            // Bo qua NEWGAME cu neu game da duoc reset va dang choi
            if (!gameOver) return;

            opponentNewGameReady = true;

            if (myNewGameReady) {
                // Ca 2 da san sang → bat dau van moi
                startNewGame();
            } else {
                // Doi thu san sang, cho minh an nut
                statusLabel.setText("🔔 Đối thủ muốn chơi lại! Nhấn 'Ván mới'");
                statusLabel.setForeground(new Color(255, 193, 7));
            }
        });
    }

    /**
     * Ca 2 da dong y → reset ban co.
     */
    private void startNewGame() {
        myNewGameReady = false;
        opponentNewGameReady = false;
        resetBoard();
    }

    /**
     * Reset ban co de choi van moi.
     */
    public void resetBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        gameOver = false;
        myTurn = (myMark == 1);
        hoverRow = -1;
        hoverCol = -1;
        lastRow = -1;
        lastCol = -1;
        winCells = null;
        moveCount = 0;
        myNewGameReady = false;
        opponentNewGameReady = false;
        newGameBtn.setEnabled(true);
        newGameBtn.setText("VÁN MỚI");
        updateTurnDisplay();
        boardPanel.repaint();

        // Ap dung nuoc di dang cho (neu doi thu da danh truoc khi board reset)
        if (pendingMoveRow >= 0 && pendingMoveCol >= 0) {
            int r = pendingMoveRow, c = pendingMoveCol;
            pendingMoveRow = -1;
            pendingMoveCol = -1;
            applyOpponentMove(r, c);
        }
    }

    /**
     * Ket thuc game voi thong bao tu ben ngoai (vd: doi thu thoat).
     */
    public void endGame(String message) {
        SwingUtilities.invokeLater(() -> {
            gameOver = true;
            statusLabel.setText(message);
            statusLabel.setForeground(COLOR_TEXT);
            boardPanel.repaint();
        });
    }

    //CHE DO TEST LOCAL
    /**
     * Chay doc lap de test giao dien.
     * Che do 2 nguoi choi tren cung 1 ban co (X va O luan phien).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Tao board choi X, che do local 2 nguoi (tu dong chuyen luot)
            GUIBoard board = new GUIBoard(1);

            // Che do local: sau moi nuoc di, tu dong chuyen luot (khong can server)
            board.setMoveListener((row, col) -> {
                // Trong che do test, doi thu (O) tu dong duoc chuyen luot
                // tai handleCellClick da set myTurn = false, ta chi can set lai = true
                // va doi myMark de doi thu danh
                // -> Thay the: ta dung che do "hot-seat" bang cach toggle
            });

            // Override: Che do hot-seat (2 nguoi choi cung ban)
            // Bo moveListener va sua handleCellClick de luon cho phep danh
            board.moveListener = null;
            board.enableLocalTwoPlayer();

            board.setVisible(true);
        });
    }

    /**
     * Bat che do 2 nguoi choi local (hot-seat).
     * Ca X va O deu click tren cung ban co.
     */
    private int currentTurn = 1; // 1 = X, 2 = O (cho che do local)
    private boolean localMode = false;

    public void enableLocalTwoPlayer() {
        localMode = true;
        myTurn = true;
        currentTurn = 1;
        myMark = 1;
        updateTurnDisplay();

        // Override mouse listener cho che do local
        for (MouseListener ml : boardPanel.getMouseListeners()) {
            boardPanel.removeMouseListener(ml);
        }

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameOver) return;
                int[] cell = boardPanel.getCellFromMouse(e.getX(), e.getY());
                if (cell == null) return;
                int row = cell[0], col = cell[1];
                if (board[row][col] != 0) return;

                placeMove(row, col, currentTurn);

                if (checkWin(row, col, currentTurn)) {
                    winCells = getWinCells(row, col, currentTurn);
                    gameOver = true;
                    String sym = (currentTurn == 1) ? "X" : "O";
                    statusLabel.setText("🎉 " + sym + " thắng!");
                    statusLabel.setForeground(new Color(76, 175, 80));
                    boardPanel.repaint();
                    JOptionPane.showMessageDialog(GUIBoard.this,
                        sym + " đã thắng!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (moveCount >= BOARD_SIZE * BOARD_SIZE) {
                    gameOver = true;
                    statusLabel.setText("🤝 Hòa!");
                    boardPanel.repaint();
                    return;
                }

                // Chuyen luot
                currentTurn = 3 - currentTurn; // 1 -> 2, 2 -> 1
                myMark = currentTurn;
                updateTurnDisplay();
                boardPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                boardPanel.repaint();
            }
        });

        // Giu hover listener
        for (MouseMotionListener ml : boardPanel.getMouseMotionListeners()) {
            boardPanel.removeMouseMotionListener(ml);
        }
        boardPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int[] cell = boardPanel.getCellFromMouse(e.getX(), e.getY());
                int newR = cell != null ? cell[0] : -1;
                int newC = cell != null ? cell[1] : -1;
                if (newR != hoverRow || newC != hoverCol) {
                    hoverRow = newR;
                    hoverCol = newC;
                    myMark = currentTurn; // de hover dung mau
                    boardPanel.repaint();
                }
            }
        });
    }
}
