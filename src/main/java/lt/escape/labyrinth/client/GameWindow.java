package lt.escape.labyrinth.client;

import javax.swing.JFrame;
public class GameWindow extends JFrame {
    public GameWindow(NetworkClient networkClient) {
        setTitle("Escape the Labyrinth");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        add(new GamePanel(networkClient));
    }
}