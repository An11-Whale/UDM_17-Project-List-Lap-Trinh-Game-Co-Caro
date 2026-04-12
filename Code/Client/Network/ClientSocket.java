package Code.Client.Network;

import java.io.*;
import java.net.*;

public class ClientSocket {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 4000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to server!");
            return true;

        } catch (IOException e) {
            System.out.println("Connect failed: " + e.getMessage());
            return false;
        }
    }

    public void send(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() throws IOException {
        return dis.readUTF();
    }

    public void close() {
        try {
            socket.close();
            dis.close();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}