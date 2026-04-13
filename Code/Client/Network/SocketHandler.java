package Code.Client.Network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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

    private void listen() {
        byte[] buffer = new byte[1024];
        boolean running = true;

        while (running) {
            try {
                int read = is.read(buffer);
                if (read == -1) break;

                String msg = new String(buffer, 0, read);
                String[] lines = msg.split("\n");

                for (String line : lines) {
                    line = line.trim();

                    if (line.isEmpty()) continue;

                    if (line.contains("Login success")) {
                        if (listener != null) listener.onLogin(true, line);
                    } else if (line.contains("Error")) {
                        if (listener != null) listener.onLogin(false, line);
                    } else {
                        if (listener != null) listener.onMessage(line);
                    }
                }

            } catch (Exception e) {
                running = false;
            }
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
}