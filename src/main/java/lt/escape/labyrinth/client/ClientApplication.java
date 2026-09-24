package lt.escape.labyrinth.client;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) {
        try {
            Assets.load();
        } catch (RuntimeException e) {
            System.out.println("Could not load images: " + e.getMessage());
            System.out.println("Make sure your PNGs are under src/main/resources/images");
            return;
        }

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