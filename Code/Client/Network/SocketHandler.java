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

    private boolean isClosed = false;

    private SocketListener listener;

    public interface SocketListener {
        void onConnected();

        void onLogin(boolean success, String message);

        void onMessage(String msg);

        void onDisconnected();

        void onGameStart(int myId, String opponentName);

        void onPlayersList(String[] players);

        void onChallengeFrom(String fromUser);

        void onChallengeAccepted();

        void onChallengeDeclined(String byUser);

        void onChallengeCancelled(String byUser);

        void onHistoryData(String data);

        void onOpponentSurrendered();
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

            if (listener != null)
                listener.onConnected();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleMessage(String line) {
        if (line.isEmpty())
            return;

        String[] parts = line.split(" ", 2);
        String command = parts[0];

        switch (command) {
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
                String[] moveParts = line.split(" ");
                int row = Integer.parseInt(moveParts[1]);
                int col = Integer.parseInt(moveParts[2]);
                int player = Integer.parseInt(moveParts[3]);

                if (gameManager != null) {
                    gameManager.onServerMove(row, col, player);
                }
                break;

            case "START":
                String[] startParts = line.split(" ");
                int myId = Integer.parseInt(startParts[1]);
                String opponentName = startParts.length > 2 ? startParts[2] : "Opponent";

                if (listener != null) {
                    listener.onGameStart(myId, opponentName);
                }
                break;

            case "PLAYERS_LIST":
                if (listener != null) {
                    if (parts.length > 1 && !parts[1].equals("EMPTY")) {
                        String[] players = parts[1].split(",");
                        listener.onPlayersList(players);
                    } else {
                        listener.onPlayersList(new String[0]);
                    }
                }
                break;

            case "CHALLENGE_FROM":
                if (listener != null && parts.length > 1) {
                    listener.onChallengeFrom(parts[1].trim());
                }
                break;

            case "CHALLENGE_ACCEPTED":
                if (listener != null) {
                    listener.onChallengeAccepted();
                }
                break;

            case "CHALLENGE_DECLINED":
                if (listener != null) {
                    String byUser = parts.length > 1 ? parts[1].trim() : "";
                    listener.onChallengeDeclined(byUser);
                }
                break;

            case "CHALLENGE_CANCELLED":
                if (listener != null) {
                    String cancelledBy = parts.length > 1 ? parts[1].trim() : "";
                    listener.onChallengeCancelled(cancelledBy);
                }
                break;

            case "CHALLENGE_ERROR":
                if (listener != null) {
                    listener.onMessage("CHALLENGE_ERROR " + (parts.length > 1 ? parts[1] : ""));
                }
                break;

            case "HISTORY_DATA":
                if (listener != null) {
                    listener.onHistoryData(parts.length > 1 ? parts[1] : "EMPTY");
                }
                break;

            case "OPPONENT_SURRENDERED":
                if (listener != null) {
                    listener.onOpponentSurrendered();
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
                if (read == -1)
                    break;

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
        if (isClosed)
            return;
        isClosed = true;

        try {
            if (socket != null)
                socket.close();
        } catch (Exception e) {
        }

        if (listener != null)
            listener.onDisconnected();
    }

    public void sendMove(int row, int col) {
        send("MOVE " + row + " " + col);
    }

    // NEW API METHODS

    public void getPlayers() {
        send("GET_PLAYERS");
    }

    public void challenge(String targetUser) {
        send("CHALLENGE " + targetUser);
    }

    public void acceptChallenge(String fromUser) {
        send("ACCEPT_CHALLENGE " + fromUser);
    }

    public void declineChallenge(String fromUser) {
        send("DECLINE_CHALLENGE " + fromUser);
    }

    public void cancelChallenge(String targetUser) {
        send("CANCEL_CHALLENGE " + targetUser);
    }

    public void sendGameResult(String winner, String loser, String reason) {
        send("GAME_RESULT " + winner + " " + loser + " " + reason);
    }

    public void getHistory() {
        send("GET_HISTORY");
    }
}