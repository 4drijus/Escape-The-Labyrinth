package lt.escape.labyrinth.client;

import java.io.IOException;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        System.out.println("Connected to server");
    }

    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}