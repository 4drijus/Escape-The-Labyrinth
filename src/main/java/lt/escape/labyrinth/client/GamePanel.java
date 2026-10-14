package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.WallState;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GamePanel extends JPanel implements IObserver {
    private final NetworkClient networkClient;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    public GamePanel(NetworkClient networkClient) {
        this.networkClient = networkClient;
        setBackground(Color.DARK_GRAY);

        inputHandler = new InputHandler(networkClient);
        addKeyListener(inputHandler);
        setFocusable(true);

        // Užregistruojam GamePanel kaip IObserver prie ClientGameState būsenos
        if (networkClient.getGameState() != null) {
            networkClient.getGameState().registerObserver(this);
        }

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();

        requestFocusInWindow();
    }

    // IObserver interfeiso metodas: iškviečiamas automatiškai, kai ClientGameState informuoja apie pokyčius
    @Override
    public void update() {
        repaint();
    }

    private void updateGame() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ClientGameState state = networkClient.getGameState();

        // Walls -- drawn first, as solid blocking geometry.
        g.setColor(new Color(88, 53, 8));
        for (WallState wall : state.getWalls()) {
            g.fillRect((int) wall.getX(), (int) wall.getY(), (int) wall.getWidth(), (int) wall.getHeight());
        }

        // Tiles -- puddles and bushes, colored by type until real sprites exist.
        for (TileState tile : state.getTiles()) {
            g.setColor(colorFor(tile));
            g.fillRect((int) tile.getX(), (int) tile.getY(), (int) tile.getWidth(), (int) tile.getHeight());
        }

        // Pressure plate -- thin strip, only drawn if this level has one.
        PressurePlateState plate = state.getPressurePlate();
        if (plate != null) {
            g.setColor(new Color(170, 170, 170));
            g.fillRect((int) plate.getX(), (int) plate.getY(), (int) plate.getWidth(), (int) plate.getHeight());
        }

        // Button
        ButtonState button = state.getButton();
        if (button != null) {
            g.setColor(new Color(255, 236, 1));
            g.fillRect((int) button.getX(), (int) button.getY(), (int) button.getWidth(), (int) button.getHeight());
        }

        // Door -- solid while closed; outline-only once open, since the
        // player can walk through it at that point.
        DoorState door = state.getDoor();
        if (door != null) {
            if (door.isOpen()) {
                g.setColor(new Color(255, 255, 255));
                g.drawRect((int) door.getX(), (int) door.getY(), (int) door.getWidth(), (int) door.getHeight());
            } else {
                g.setColor(new Color(0, 0, 0));
                g.fillRect((int) door.getX(), (int) door.getY(), (int) door.getWidth(), (int) door.getHeight());
            }
        }

        // Exit zone -- outlined only, so players standing in it stay visible.
        ExitState exit = state.getExit();
        if (exit != null) {
            g.setColor(new Color(26, 155, 127));
            g.drawRect((int) exit.getX(), (int) exit.getY(), (int) exit.getWidth(), (int) exit.getHeight());
        }

        // Enemies
        for (EnemyState enemy : state.getEnemies()) {
            Image sprite = Assets.getEnemySprite(enemy.getType());
            g.drawImage(sprite, (int) enemy.getX(), (int) enemy.getY(), this);
        }

        // Bullets
        for (BulletState bullet : state.getBullets()) {
            Image sprite = Assets.getBulletSprite(bullet.getType());
            g.drawImage(sprite, (int) bullet.getX(), (int) bullet.getY(), this);
        }

        // Player 1
        g.drawImage(Assets.getPlayerBlueSprite(), (int) state.getPlayer1X(), (int) state.getPlayer1Y(), this);

        // Player 2
        g.drawImage(Assets.getPlayerRedSprite(), (int) state.getPlayer2X(), (int) state.getPlayer2Y(), this);
    }

    private Color colorFor(TileState tile) {
        switch (tile.getType()) {
            case BLUE_PUDDLE:
                return new Color(60, 120, 220, 180);
            case RED_PUDDLE:
                return new Color(220, 80, 60, 180);
            case BLACK_PUDDLE:
                return new Color(46, 45, 45, 200);
            case BUSH:
                return new Color(40, 120, 40);
            default:
                return new Color(71, 8, 88); // obviously-wrong color if a TileType is ever missed here
        }
    }
}