package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.Lever;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.world.Box;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.Sign;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.Prototype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder pattern "product". It has no constructor of its own: a builder
 * creates an empty Level and fills it step by step through the
 * package-private methods below. Nothing outside this package can change
 * a Level; everyone else only reads it through the public getters.
 *
 * Door, Button, PressurePlate and Exit carry their own position (see
 * InteractiveElement.setBounds), so Level just holds the elements.
 */
public class Level implements Prototype<Level> {

    private double width = 960;
    private double height = 600;

    private double player1StartX = 500;
    private double player1StartY = 480;
    private double player2StartX = 100;
    private double player2StartY = 480;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();
    private final List<Tile> tiles = new ArrayList<>();
    private final List<Lift> lifts = new ArrayList<>();
    private final List<Box> boxes = new ArrayList<>();
    private final List<Potion> potions = new ArrayList<>();
    private final List<Sign> signs = new ArrayList<>();

    private Door door;
    private Button button;
    private PressurePlate pressurePlate;
    private Lever lever;
    private Exit exit;

    // ---- used by the builders (same package) ----

    void setDimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    void setPlayerStarts(double p1X, double p1Y, double p2X, double p2Y) {
        this.player1StartX = p1X;
        this.player1StartY = p1Y;
        this.player2StartX = p2X;
        this.player2StartY = p2Y;
    }

    void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    void addWall(Wall wall) {
        walls.add(wall);
    }

    void addTile(Tile tile) {
        tiles.add(tile);
    }

    void addLift(Lift lift) {
        lifts.add(lift);
    }

    void addBox(Box box) {
        boxes.add(box);
    }

    void addPotion(Potion potion) {
        potions.add(potion);
    }

    void addSign(Sign sign) {
        signs.add(sign);
    }

    void setDoor(Door door) {
        this.door = door;
    }

    void setButton(Button button) {
        this.button = button;
    }

    void setPressurePlate(PressurePlate pressurePlate) {
        this.pressurePlate = pressurePlate;
    }

    void setLever(Lever lever) {
        this.lever = lever;
    }

    void setExit(Exit exit) {
        this.exit = exit;
    }

    // ---- read-only access for everyone else ----

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getPlayer1StartX() {
        return player1StartX;
    }

    public double getPlayer1StartY() {
        return player1StartY;
    }

    public double getPlayer2StartX() {
        return player2StartX;
    }

    public double getPlayer2StartY() {
        return player2StartY;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    /** Puddles and bushes. */
    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    public List<Lift> getLifts() {
        return Collections.unmodifiableList(lifts);
    }

    public List<Box> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    public List<Potion> getPotions() {
        return Collections.unmodifiableList(potions);
    }

    public List<Sign> getSigns() {
        return Collections.unmodifiableList(signs);
    }

    public Door getDoor() {
        return door;
    }

    public Button getButton() {
        return button;
    }

    public PressurePlate getPressurePlate() {
        return pressurePlate;
    }

    /** Nullable. */
    public Lever getLever() {
        return lever;
    }

    /** Nullable. */
    public Exit getExit() {
        return exit;
    }

    // ---- Prototype ----

    /**
     * Prototype: an independent copy of this level, so GameWorld can keep
     * the level the Director built as an untouched template and restart
     * from a copy of it instead of running the recipe again.
     */
    @Override
    public Level copy() {
        Level copy = new Level();
        copy.setDimensions(width, height);
        copy.setPlayerStarts(player1StartX, player1StartY, player2StartX, player2StartY);

        // Parts that change during play get their own copies...
        for (Enemy enemy : enemies) {
            copy.addEnemy(enemy.copy());
        }
        for (Lift lift : lifts) {
            copy.addLift(lift.copy());
        }
        for (Box box : boxes) {
            copy.addBox(box.copy());
        }
        for (Potion potion : potions) {
            copy.addPotion(potion.copy());
        }
        if (exit != null) {
            copy.setExit(exit.copy());
        }

        // ...the switches are linked to the copied door, not this one...
        Door doorCopy = door == null ? null : door.copy();
        copy.setDoor(doorCopy);
        if (button != null) {
            copy.setButton(button.copyFor(doorCopy));
        }
        if (pressurePlate != null) {
            copy.setPressurePlate(pressurePlate.copyFor(doorCopy));
        }
        if (lever != null) {
            copy.setLever(lever.copyFor(doorCopy));
        }

        // ...and walls, tiles and signs never change, so both levels share them.
        for (Wall wall : walls) {
            copy.addWall(wall);
        }
        for (Tile tile : tiles) {
            copy.addTile(tile);
        }
        for (Sign sign : signs) {
            copy.addSign(sign);
        }

        return copy;
    }
}