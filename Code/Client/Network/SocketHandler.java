package Code.Client.Network;

import java.io.IOException;

public class SocketHandler {

    private ClientSocket client;
    private Thread listener;

    public SocketHandler(ClientSocket client) {
        this.client = client;
    }

    public void startListening() {
        listener = new Thread(() -> {
            while (true) {
                try {
                    String data = client.receive();
                    System.out.println("RECEIVED: " + data);
                    handle(data);

                } catch (IOException e) {
                    System.out.println("Disconnected from server!");
                    break;
                }
            }
        });
        listener.start();
    }
    
    private void handle(String data) {
        String[] parts = data.split(";");
        String type = parts[0];

        switch (type) {

            case "LOGIN":
                handleLogin(parts);
                break;

            case "SIGNUP":
                handleSignup(parts);
                break;

            case "LIST_ROOM":
                handleListRoom(parts);
                break;

            case "JOIN_ROOM":
                handleJoinRoom(parts);
                break;

            case "CHAT":
                handleChat(parts);
                break;

            case "MOVE":
                handleMove(parts);
                break;

            default:
                System.out.println("Unknown type: " + type);
        }
    }

    // ================= EVENT =================
    private void handleLogin(String[] data) {
        String status = data[1];

        if (status.equals("success")) {
            System.out.println("Login success: " + data[2]);
        } else {
            System.out.println("Login failed: " + data[2]);
        }
    }

    private void handleSignup(String[] data) {
        String status = data[1];

        if (status.equals("success")) {
            System.out.println("Signup success");
        } else {
            System.out.println("Signup failed: " + data[2]);
        }
    }

    private void handleListRoom(String[] data) {
        int count = Integer.parseInt(data[1]);
        System.out.println("Room list:");

        int index = 2;
        for (int i = 0; i < count; i++) {
            String roomId = data[index++];
            String name = data[index++];
            String players = data[index++];

            System.out.println(roomId + " | " + name + " | " + players);
        }
    }

    private void handleJoinRoom(String[] data) {
        String roomId = data[1];
        System.out.println("Joined room: " + roomId);
    }

    private void handleChat(String[] data) {
        String user = data[1];
        String msg = data[2];

        System.out.println(user + ": " + msg);
    }

    private void handleMove(String[] data) {
        int x = Integer.parseInt(data[1]);
        int y = Integer.parseInt(data[2]);

        System.out.println("Move at: " + x + ", " + y);
    }
}