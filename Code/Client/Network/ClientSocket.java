package Code.Client.Network;

public class ClientSocket {

    private SocketHandler socket;

    public interface ClientListener {
        void onConnected();
        void onLogin(boolean success, String message);
        void onGameStart(int myId);
        void onMove(int row, int col, int player);
        void onMessage(String msg);
        void onDisconnected();
    }

    private ClientListener listener;

    public SocketHandler getSocketHandler() {
        return socket;
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public ClientSocket() {
        socket = new SocketHandler();

        socket.setListener(new SocketHandler.SocketListener() {
            @Override
            public void onConnected() {
                if (listener != null) listener.onConnected();
            }
            @Override
            public void onLogin(boolean success, String message) {
                if (listener != null) listener.onLogin(success, message);
            }
            @Override
            public void onMessage(String msg) {
                handleServerMessage(msg);
            }
            @Override
            public void onDisconnected() {
                if (listener != null) listener.onDisconnected();
            }
            @Override
            public void onGameStart(int myId) {
                if (listener != null) {
                    listener.onGameStart(myId);
                }
            }
        });
    }

    //NETWORK API

    public boolean connect(String host, int port) {
        return socket.connect(host, port);
    }

    public void login(String user, String pass) {
        socket.login(user, pass);
    }

    public void register(String user, String pass) {
        socket.register(user, pass);
    }

    public void sendMove(int row, int col) {
        socket.sendMove(row, col);
    }

    public void disconnect() {
        socket.close();
    }

    //PARSE SERVER

    private void handleServerMessage(String msg) {
        if (msg == null || msg.isEmpty()) return;

        String[] parts = msg.split(" ");

        switch (parts[0]) {

            case "START":
                int myId = Integer.parseInt(parts[1]);
                if (listener != null) listener.onGameStart(myId);
                break;

            case "MOVE":
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                int player = Integer.parseInt(parts[3]);

                if (listener != null) {
                    listener.onMove(row, col, player);
                }
                break;

            default:
                if (listener != null) listener.onMessage(msg);
        }
    }
}