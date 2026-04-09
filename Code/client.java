package Code;

import Code.GUI.GUIBoard;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class client {

    //KET NOI MANG
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // GUI
    private GUIBoard board;
    private JFrame loginFrame;
    
    private static final Color BG = new Color(18, 18, 35);
    private static final Color PANEL = new Color(28, 28, 52);
    private static final Color CARD_BG = new Color(35, 35, 65);
    private static final Color BORDER = new Color(55, 55, 90);
    private static final Color ACCENT = new Color(126, 87, 194);
    private static final Color ACCENT_HOVER = new Color(149, 105, 210);
    private static final Color TEXT = new Color(224, 224, 230);
    private static final Color TEXT_DIM = new Color(140, 140, 160);
    private static final Color SUCCESS = new Color(76, 175, 80);
    private static final Color ERROR_COLOR = new Color(239, 83, 80);

    //PHUONG THUC MANG
    /**
     * Ket noi den server.
     */
    public void Client_Connect(String host, int port) throws IOException {
        if (host == null || host.isEmpty()) {
            throw new IOException("Host khong duoc de trong.");
        }
        if (port <= 0 || port > 65535) {
            throw new IOException("Port khong hop le.");
        }
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to server at " + host + ":" + port);
    }

    /**
     * Gui tin nhan den server.
     */
    public void Client_Send(String message) {
        if (socket == null || socket.isClosed()) {
            System.err.println("Client is not connected to a server.");
            return;
        }
        out.println(message);
    }

    /**
     * Gui tin nhan va cho phan hoi tu server.
     */
    public String Client_SendAndReceive(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }

    /**
     * Cho nhan 1 tin nhan tu server.
     */
    public String Client_WaitMessage() throws IOException {
        return in.readLine();
    }

    /**
     * Ngat ket noi.
     */
    public void Client_Disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    //GIAO DIEN DANG NHAP

    /**
     * Hien thi cua so dang nhap.
     */
    private void showLoginWindow() {
        loginFrame = new JFrame("Cờ Caro — Kết nối");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);
        loginFrame.getContentPane().setBackground(BG);
        loginFrame.setLayout(new BorderLayout());

        // Panel chinh
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        //Tieu de
        JLabel title = new JLabel("CỜ CARO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        JLabel subtitle = new JLabel("Kết nối để chơi cùng bạn bè");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(30));

        // Server address
        mainPanel.add(createLabel("ĐỊA CHỈ SERVER"));
        mainPanel.add(Box.createVerticalStrut(5));

        JTextField hostField = createTextField("localhost");
        JTextField portField = createTextField("12345");

        JPanel hostRow = new JPanel(new BorderLayout(8, 0));
        hostRow.setOpaque(false);
        hostRow.setMaximumSize(new Dimension(320, 38));
        hostField.setPreferredSize(new Dimension(220, 38));
        portField.setPreferredSize(new Dimension(82, 38));
        hostRow.add(hostField, BorderLayout.CENTER);
        hostRow.add(portField, BorderLayout.EAST);
        mainPanel.add(hostRow);
        mainPanel.add(Box.createVerticalStrut(18));

        // Username
        mainPanel.add(createLabel("TÊN ĐĂNG NHẬP"));
        mainPanel.add(Box.createVerticalStrut(5));
        JTextField userField = createTextField("");
        userField.setMaximumSize(new Dimension(320, 38));
        mainPanel.add(userField);
        mainPanel.add(Box.createVerticalStrut(18));

        // Password
        mainPanel.add(createLabel("MẬT KHẨU"));
        mainPanel.add(Box.createVerticalStrut(5));
        JPasswordField passField = new JPasswordField();
        styleTextField(passField);
        passField.setMaximumSize(new Dimension(320, 38));
        mainPanel.add(passField);
        mainPanel.add(Box.createVerticalStrut(28));

        //Nut ket noi 
        JButton connectBtn = new JButton("KẾT NỐI & CHƠI");
        connectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setBackground(ACCENT);
        connectBtn.setFocusPainted(false);
        connectBtn.setBorderPainted(false);
        connectBtn.setOpaque(true);
        connectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        connectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectBtn.setMaximumSize(new Dimension(320, 44));
        connectBtn.setPreferredSize(new Dimension(320, 44));
        // Hover effect
        connectBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (connectBtn.isEnabled()) connectBtn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                connectBtn.setBackground(ACCENT);
            }
        });
        mainPanel.add(connectBtn);
        mainPanel.add(Box.createVerticalStrut(18));

        //Status label
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_DIM);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        //Xu ly nut ket noi
        connectBtn.addActionListener(e -> {
            // Doc input
            String host = hostField.getText().trim();
            String portStr = portField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            // Validate
            if (host.isEmpty()) {
                showStatus(statusLabel, "Vui lòng nhập địa chỉ server!", ERROR_COLOR);
                return;
            }
            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                showStatus(statusLabel, "Port không hợp lệ!", ERROR_COLOR);
                return;
            }
            if (user.isEmpty() || pass.isEmpty()) {
                showStatus(statusLabel, "Vui lòng nhập tên và mật khẩu!", ERROR_COLOR);
                return;
            }

            // Khoa nut, chay ket noi tren background thread
            connectBtn.setEnabled(false);
            showStatus(statusLabel, "Đang kết nối...", TEXT_DIM);

            new Thread(() -> connectAndPlay(host, port, user, pass, connectBtn, statusLabel)).start();
        });

        // Cho phep Enter de ket noi
        passField.addActionListener(e -> connectBtn.doClick());

        loginFrame.add(mainPanel);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    // LOGIC KET NOI VA CHOI

    /**
     * Ket noi den server, dang ky, dang nhap, cho doi thu, mo ban co.
     * Chay tren background thread (KHONG phai EDT).
     */
    private void connectAndPlay(String host, int port, String user, String pass,
                                 JButton btn, JLabel status) {
        try {
            // Buoc 1: Ket noi den server
            Client_Connect(host, port);
            showStatus(status, "✓ Đã kết nối! Đang đăng ký...", SUCCESS);

            // Buoc 2: Dang ky (bo qua neu tai khoan da ton tai)
            String regResp = Client_SendAndReceive("REGISTER " + user + " " + pass);
            System.out.println("Register response: " + regResp);

            // Buoc 3: Dang nhap
            showStatus(status, "Đang đăng nhập...", TEXT_DIM);
            String loginResp = Client_SendAndReceive("LOGIN " + user + " " + pass);
            System.out.println("Login response: " + loginResp);

            if (loginResp == null || !loginResp.contains("success")) {
                // Dang nhap that bai
                String errorMsg = (loginResp != null) ? loginResp : "Mất kết nối!";
                showStatus(status, "✗ " + errorMsg, ERROR_COLOR);
                SwingUtilities.invokeLater(() -> btn.setEnabled(true));
                Client_Disconnect();
                return;
            }

            // Buoc 4: Cho doi thu
            showStatus(status, "⏳ Đang chờ đối thủ... (chờ 1 người nữa kết nối)", SUCCESS);

            // Blocking: cho den khi server gui "START: You are X/O"
            String startMsg = Client_WaitMessage();
            System.out.println("Start message: " + startMsg);

            if (startMsg == null || !startMsg.startsWith("START")) {
                showStatus(status, "✗ Lỗi kết nối server!", ERROR_COLOR);
                SwingUtilities.invokeLater(() -> btn.setEnabled(true));
                Client_Disconnect();
                return;
            }

            // Buoc 5: Xac dinh la X hay O
            int myMark = startMsg.contains("X") ? 1 : 2;
            String mySymbol = (myMark == 1) ? "X" : "O";
            System.out.println("I am: " + mySymbol);

            // Buoc 6: Mo ban co
            SwingUtilities.invokeLater(() -> {
                loginFrame.dispose(); // dong cua so dang nhap
                board = new GUIBoard(myMark);
                board.setPlayerNames(user, "Đối thủ");
                // Khi click vao ban co → gui nuoc di den server
                board.setMoveListener((row, col) -> {
                    Client_Send("MOVE " + row + " " + col);
                });
                // Khi nhan "Van moi" → gui NEWGAME den server
                board.setNewGameRequestListener(() -> {
                    Client_Send("NEWGAME");
                });
                board.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                board.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Client_Disconnect();
                        System.exit(0);
                    }
                });
                board.setVisible(true);
            });

            // Buoc 7: Bat dau nhan nuoc di tu doi thu
            startGameReceiver();

        } catch (IOException e) {
            showStatus(status, "✗ Không thể kết nối server! (" + e.getMessage() + ")", ERROR_COLOR);
            SwingUtilities.invokeLater(() -> btn.setEnabled(true));
        }
    }

    // NHAN NUOC DI TU DOI THU

    /**
     * Luong lien tuc nhan du lieu tu server.
     * Khi nhan "MOVE row col" → cap nhat ban co.
     */
    private void startGameReceiver() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Received: " + msg);

                    // Xu ly nuoc di cua doi thu
                    if (msg.startsWith("MOVE")) {
                        String[] parts = msg.trim().split("\\s+");
                        if (parts.length >= 3) {
                            try {
                                int row = Integer.parseInt(parts[1]);
                                int col = Integer.parseInt(parts[2]);
                                if (board != null) {
                                    board.receiveOpponentMove(row, col);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid MOVE format: " + msg);
                            }
                        }
                    }
                    // Xu ly yeu cau van moi tu doi thu
                    else if (msg.startsWith("NEWGAME")) {
                        if (board != null) {
                            board.receiveNewGameRequest();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }

            // Doi thu ngat ket noi hoac server dong
            if (board != null) {
                board.endGame("⚠ Đối thủ đã ngắt kết nối!");
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(board,
                        "Đối thủ đã ngắt kết nối!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                });
            }
        }).start();
    }

    //HELPER UI

    private void showStatus(JLabel label, String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            label.setText(text);
            label.setForeground(color);
        });
    }

    private JTextField createTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(CARD_BG);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_DIM);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    //MAIN

    /**
     * Khoi chay ung dung client.
     * Hien thi giao dien dang nhap, sau do tu dong ket noi va choi.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            client c = new client();
            c.showLoginWindow();
        });
    }
}
