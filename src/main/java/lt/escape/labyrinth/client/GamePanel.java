package lt.escape.labyrinth.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import lt.escape.labyrinth.client.effects.BasicPlayerRenderer;
import lt.escape.labyrinth.client.effects.JumpEffect;
import lt.escape.labyrinth.client.effects.NameEffect;
import lt.escape.labyrinth.client.effects.PlayerRenderer;
import lt.escape.labyrinth.client.effects.RunEffect;
import lt.escape.labyrinth.client.effects.TrailEffect;
import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.WallState;

public class GamePanel extends JPanel implements IObserver {

    private final NetworkClient networkClient;
    private final InputHandler inputHandler;
    private final Timer gameTimer;

    private final PlayerRenderer[] playerRenderers = {
        createPlayerRenderer(),
        createPlayerRenderer()
    };

    private static PlayerRenderer createPlayerRenderer() {
        return new TrailEffect(
                new JumpEffect(
                        new RunEffect(
                                new BasicPlayerRenderer()
                        )
                )
        );
    }

// Movement tracking for the two players.
    private final double[] previousX = new double[2];
    private final double[] previousY = new double[2];
    private final boolean[] positionKnown = new boolean[2];

    private final long[] lastHorizontalMove = new long[2];
    private final long[] lastVerticalMove = new long[2];

    private static final long EFFECT_HOLD_NANOS = 150_000_000L;

    public GamePanel(NetworkClient networkClient) {
        this.networkClient = networkClient;
        setBackground(Color.DARK_GRAY);

        inputHandler = new InputHandler(
                networkClient,
                this::editPlayerName
        );
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
        drawPlayer(
                g, 0, Assets.getPlayerBlueSprite(),
                state.getPlayer1X(), state.getPlayer1Y()
        );

        // Player 2
        drawPlayer(
                g, 1, Assets.getPlayerRedSprite(),
                state.getPlayer2X(), state.getPlayer2Y()
        );
        drawHealth(g, "P1", state.getPlayer1Health(), 10, new Color(80, 140, 255));
        drawHealth(g, "P2", state.getPlayer2Health(), getWidth() - 110, new Color(255, 90, 90));
    }

    private void drawHealth(Graphics g, String label, int health, int x, Color color) {
        g.setColor(color);
        g.drawString(label, x, 20);
        for (int i = 0; i < health; i++) {
            g.fillRect(x + 25 + i * 20, 8, 14, 14);
        }
    }

    private void drawPlayer(
            Graphics g, int index, Image sprite, double x, double y
    ) {
        long now = System.nanoTime();

        if (positionKnown[index]) {
            if (Math.abs(x - previousX[index]) > 0.1) {
                lastHorizontalMove[index] = now;
            }

            if (Math.abs(y - previousY[index]) > 0.1) {
                lastVerticalMove[index] = now;
            }
        }

        previousX[index] = x;
        previousY[index] = y;
        positionKnown[index] = true;

        boolean moving = lastHorizontalMove[index] != 0
                && now - lastHorizontalMove[index] < EFFECT_HOLD_NANOS;

        boolean jumping = lastVerticalMove[index] != 0
                && now - lastVerticalMove[index] < EFFECT_HOLD_NANOS;

        playerRenderers[index].draw(
                g, sprite, (int) x, (int) y, moving, jumping
        );
    }

    private void editPlayerName() {
        System.out.println(
                "N action, playerId = " + networkClient.getPlayerId()
        );
        int index = networkClient.getPlayerId() - 1;

        if (index < 0 || index >= playerRenderers.length) {
            return;
        }

        // stop movement before the dialog takes keyboard focus.
        networkClient.sendCommand(PlayerCommand.STOP);

        String enteredName = JOptionPane.showInputDialog(
                this,
                "Enter your name. Leave empty to remove it:",
                "Player name",
                JOptionPane.PLAIN_MESSAGE
        );

        requestFocusInWindow();

        // cancel keeps the current renderer unchanged.
        if (enteredName == null) {
            return;
        }

        String name = enteredName.strip();

        if (name.length() > 20) {
            JOptionPane.showMessageDialog(
                    this,
                    "The name must contain no more than 20 characters."
            );
            requestFocusInWindow();
            return;
        }

        PlayerRenderer current = playerRenderers[index];

        // remove the previous name layer before replacing it.
        if (current instanceof NameEffect nameEffect) {
            current = nameEffect.unwrap();
        }

        if (name.isEmpty()) {
            playerRenderers[index] = current;
        } else {
            // add a new decorator object at runtime.
            playerRenderers[index] = new NameEffect(current, name);
        }

        repaint();
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
