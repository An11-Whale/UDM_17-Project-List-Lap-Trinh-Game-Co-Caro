package Code.Client.Network;

public class ClientSocket {

    private SocketHandler socket;

    public interface ClientListener {
        void onConnected();

        void onLogin(boolean success, String message);

        void onGameStart(int myId, String opponentName);

        void onMove(int row, int col, int player);

        void onMessage(String msg);

        void onDisconnected();

        void onPlayersList(String[] players);

        void onChallengeFrom(String fromUser);

        void onChallengeAccepted();

        void onChallengeDeclined(String byUser);

        void onChallengeCancelled(String byUser);

        void onHistoryData(String data);

        void onOpponentSurrendered();
    }

    private ClientListener listener;

    public SocketHandler getSocketHandler() {
        return socket;
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;

        // Cap nhat listener cho SocketHandler
        socket.setListener(new SocketHandler.SocketListener() {
            @Override
            public void onConnected() {
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void onLogin(boolean success, String message) {
                if (listener != null)
                    listener.onLogin(success, message);
            }

            @Override
            public void onMessage(String msg) {
                handleServerMessage(msg);
            }

            @Override
            public void onDisconnected() {
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void onGameStart(int myId, String opponentName) {
                if (listener != null) {
                    listener.onGameStart(myId, opponentName);
                }
            }

            @Override
            public void onPlayersList(String[] players) {
                if (listener != null)
                    listener.onPlayersList(players);
            }

            @Override
            public void onChallengeFrom(String fromUser) {
                if (listener != null)
                    listener.onChallengeFrom(fromUser);
            }

            @Override
            public void onChallengeAccepted() {
                if (listener != null)
                    listener.onChallengeAccepted();
            }

            @Override
            public void onChallengeDeclined(String byUser) {
                if (listener != null)
                    listener.onChallengeDeclined(byUser);
            }

            @Override
            public void onChallengeCancelled(String byUser) {
                if (listener != null)
                    listener.onChallengeCancelled(byUser);
            }

            @Override
            public void onHistoryData(String data) {
                if (listener != null)
                    listener.onHistoryData(data);
            }

            @Override
            public void onOpponentSurrendered() {
                if (listener != null)
                    listener.onOpponentSurrendered();
            }
        });
    }

    public ClientSocket() {
        socket = new SocketHandler();

        socket.setListener(new SocketHandler.SocketListener() {
            @Override
            public void onConnected() {
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void onLogin(boolean success, String message) {
                if (listener != null)
                    listener.onLogin(success, message);
            }

            @Override
            public void onMessage(String msg) {
                handleServerMessage(msg);
            }

            @Override
            public void onDisconnected() {
                if (listener != null)
                    listener.onDisconnected();
            }

            @Override
            public void onGameStart(int myId, String opponentName) {
                if (listener != null) {
                    listener.onGameStart(myId, opponentName);
                }
            }

            @Override
            public void onPlayersList(String[] players) {
                if (listener != null)
                    listener.onPlayersList(players);
            }

            @Override
            public void onChallengeFrom(String fromUser) {
                if (listener != null)
                    listener.onChallengeFrom(fromUser);
            }

            @Override
            public void onChallengeAccepted() {
                if (listener != null)
                    listener.onChallengeAccepted();
            }

            @Override
            public void onChallengeDeclined(String byUser) {
                if (listener != null)
                    listener.onChallengeDeclined(byUser);
            }

            @Override
            public void onChallengeCancelled(String byUser) {
                if (listener != null)
                    listener.onChallengeCancelled(byUser);
            }

            @Override
            public void onHistoryData(String data) {
                if (listener != null)
                    listener.onHistoryData(data);
            }

            @Override
            public void onOpponentSurrendered() {
                if (listener != null)
                    listener.onOpponentSurrendered();
            }
        });
    }

    // NETWORK API

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

    // NEW API

    public void getPlayers() {
        socket.getPlayers();
    }

    public void challenge(String targetUser) {
        socket.challenge(targetUser);
    }

    public void acceptChallenge(String fromUser) {
        socket.acceptChallenge(fromUser);
    }

    public void declineChallenge(String fromUser) {
        socket.declineChallenge(fromUser);
    }

    public void cancelChallenge(String targetUser) {
        socket.cancelChallenge(targetUser);
    }

    public void sendGameResult(String winner, String loser, String reason) {
        socket.sendGameResult(winner, loser, reason);
    }

    public void getHistory() {
        socket.getHistory();
    }

    // PARSE SERVER

    private void handleServerMessage(String msg) {
        if (msg == null || msg.isEmpty())
            return;

        String[] parts = msg.split(" ");

        switch (parts[0]) {

            case "START":
                int myId = Integer.parseInt(parts[1]);
                String opponentName = parts.length > 2 ? parts[2] : "Opponent";
                if (listener != null)
                    listener.onGameStart(myId, opponentName);
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
                if (listener != null)
                    listener.onMessage(msg);
        }
    }
}