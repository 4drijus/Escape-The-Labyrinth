package lt.escape.labyrinth.client;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) {
        NetworkClient networkClient = new NetworkClient();

        try {
            networkClient.connect("localhost", 5000);
            System.out.println("Connected as player " + networkClient.getPlayerId());
            SwingUtilities.invokeLater(() -> {
                GameWindow window = new GameWindow(networkClient);
                window.setVisible(true);
            });
        } catch (IOException e) {
            System.out.println("Could not connect to server");
            e.printStackTrace();
        }
    }
}