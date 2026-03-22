package Code;

import java.io.*;
import java.net.*;

public class client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void Client_Connect(String host, int port) throws IOException {
        //kiem tra hop le
        if (host == null || host.isEmpty()) {
            System.err.println("Host cannot be null or empty.");
            return;
        }
        if (port <= 0 || port > 65535) {
            System.err.println("Invalid port number.");
            return;
        }
        //ket noi den server
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to server at " + host + ":" + port);
    }
    public String Client_Send(String message) {
        if(socket == null || socket.isClosed()) {
            System.err.println("Client is not connected to a server.");
            return null;
        }
        try {
            out.println(message);
            String response = in.readLine();
            return response;
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return null;
        }
    }
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
    public void Client_Register(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.err.println("Username and password cannot be null or empty.");
            return;
        }
        String registrationMessage = "REGISTER " + username + " " + password;
        Client_Send(registrationMessage);
    }
    //test client
    public static void main(String[] args) {
        client client = new client();
        try {
            client.Client_Connect("localhost", 12345);
            client.Client_Register("testuser", "testpassword");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            client.Client_Disconnect();
        }
    }
}