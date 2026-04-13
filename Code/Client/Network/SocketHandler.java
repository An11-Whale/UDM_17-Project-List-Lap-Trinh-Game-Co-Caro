package Code.Client.Network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import Code.Client.Game.GameManager;

public class SocketHandler {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private Thread listenerThread;

    private SocketListener listener;

    public interface SocketListener {
        void onConnected();
        void onLogin(boolean success, String message);
        void onMessage(String msg);
        void onDisconnected();
        void onGameStart(int myId);
    }

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setListener(SocketListener listener) {
        this.listener = listener;
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 4000);

            is = socket.getInputStream();
            os = socket.getOutputStream();

            listenerThread = new Thread(this::listen);
            listenerThread.start();

            if (listener != null) listener.onConnected();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleMessage(String line) {
        if (line.isEmpty()) return;

        String[] parts = line.split(" ");

        switch (parts[0]) {
            case "LOGIN_SUCCESS":
                if (listener != null) {
                    listener.onLogin(true, "OK");
                }
                break;

            case "LOGIN_ERROR":
                if (listener != null) {
                    listener.onLogin(false, parts.length > 1 ? parts[1] : "");
                }
                break;

            case "MOVE":
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                int player = Integer.parseInt(parts[3]);

                if (gameManager != null) {
                    gameManager.onServerMove(row, col, player);
                }
                break;

            case "START":
                int myId = Integer.parseInt(parts[1]);

                if (listener != null) {
                    listener.onGameStart(myId);
                }
                break;

            default:
                if (listener != null) {
                    listener.onMessage(line);
                }
        }
    }

    private void listen() {
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();

        try {
            while (true) {
                int read = is.read(buffer);
                if (read == -1) break;

                sb.append(new String(buffer, 0, read));

                int index;
                while ((index = sb.indexOf("\n")) != -1) {
                    String line = sb.substring(0, index).trim();
                    sb.delete(0, index + 1);

                    handleMessage(line);
                }
            }
        } catch (Exception e) {
    }
    close();
    }

    public void login(String user, String pass) {
        send("LOGIN " + user + " " + pass);
    }

    public void register(String user, String pass) {
        send("REGISTER " + user + " " + pass);
    }

    public void send(String data) {
        try {
            os.write((data + "\n").getBytes());
            os.flush();
        } catch (Exception e) {
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        }

        if (listener != null) listener.onDisconnected();
    }

    public void sendMove(int row, int col) {
        send("MOVE " + row + " " + col);
    }
}