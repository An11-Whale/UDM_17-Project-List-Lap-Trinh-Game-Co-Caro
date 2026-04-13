package Code.Client.Network;

public class ClientSocket {

    public static void main(String[] args) {

        SocketHandler socket = new SocketHandler();

        socket.setListener(new SocketHandler.SocketListener() {

            @Override
            public void onConnected() {
                System.out.println("Connected");
                socket.login("HP4", "hhhuuuhhhhh");
            }

            @Override
            public void onLogin(boolean success, String message) {
                System.out.println(message);
            }

            @Override
            public void onMessage(String msg) {
                System.out.println("Server: " + msg);
            }

            @Override
            public void onDisconnected() {
                System.out.println("Disconnected");
            }
        });

        boolean ok = socket.connect("localhost", 9999);

        if (!ok) {
            System.out.println("Cannot connect to server");
        }
    }
}