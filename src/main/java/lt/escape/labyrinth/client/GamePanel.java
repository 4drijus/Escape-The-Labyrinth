package lt.escape.labyrinth.client;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class GamePanel extends JPanel {
    private final Player player;

    public GamePanel() {
        setBackground(Color.DARK_GRAY);

        player = new Player(500,480);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //ground
        g.setColor(Color.GREEN);
        g.fillRect(0, 520, getWidth(), 80);

        //player
        g.setColor(Color.BLUE);
        g.fillRect(
                (int) player.getX(),
                (int) player.getY(),
                40,
                40
        );
    }
}