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

import java.util.List;

/**
 * Builder pattern "product": a fully assembled level, ready to hand to
 * GameServer. Package-private constructor: only LevelBuilder (same
 * package) can create one, so a Level can never exist half-built.
 *
 * Door/Button/PressurePlate carry no position of their own (confirmed
 * from the real Door.java/InteractiveElement.java -- neither has x/y),
 * so Level tracks a Rect alongside each one instead of modifying those
 * classes.
 */
public class Level {

    /** Simple position+size bundle, since Door/Button/PressurePlate don't carry their own. */
    public static final class Rect {
        public final double x;
        public final double y;
        public final double width;
        public final double height;

        public Rect(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private final double width;
    private final double height;

    private final double player1StartX;
    private final double player1StartY;
    private final double player2StartX;
    private final double player2StartY;

    private final List<Enemy> enemies;

    private final Door door;
    private final Rect doorRect;
    private final Button button;
    private final Rect buttonRect;
    private final PressurePlate pressurePlate;
    private final Rect pressurePlateRect;
    private final Lever lever;
    private final Exit exit;

    private final List<Wall> walls;
    private final List<Tile> tiles;
    private final List<Lift> lifts;
    private final List<Box> boxes;
    private final List<Potion> potions;
    private final List<Sign> signs;

    Level(double width, double height,
          double player1StartX, double player1StartY,
          double player2StartX, double player2StartY,
          List<Enemy> enemies,
          Door door, Rect doorRect,
          Button button, Rect buttonRect,
          PressurePlate pressurePlate, Rect pressurePlateRect,
          Lever lever, Exit exit,
          List<Wall> walls, List<Tile> tiles, List<Lift> lifts, List<Box> boxes,
          List<Potion> potions, List<Sign> signs) {
        this.width = width;
        this.height = height;
        this.player1StartX = player1StartX;
        this.player1StartY = player1StartY;
        this.player2StartX = player2StartX;
        this.player2StartY = player2StartY;
        this.enemies = enemies;
        this.door = door;
        this.doorRect = doorRect;
        this.button = button;
        this.buttonRect = buttonRect;
        this.pressurePlate = pressurePlate;
        this.pressurePlateRect = pressurePlateRect;
        this.lever = lever;
        this.exit = exit;
        this.walls = walls;
        this.tiles = tiles;
        this.lifts = lifts;
        this.boxes = boxes;
        this.potions = potions;
        this.signs = signs;
    }

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
        return enemies;
    }

    public Door getDoor() {
        return door;
    }

    /** Nullable -- only set if withDoor() was called with a position. */
    public Rect getDoorRect() {
        return doorRect;
    }

    public Button getButton() {
        return button;
    }

    public Rect getButtonRect() {
        return buttonRect;
    }

    public PressurePlate getPressurePlate() {
        return pressurePlate;
    }

    public Rect getPressurePlateRect() {
        return pressurePlateRect;
    }

    /** Nullable -- not every level has a lever. */
    public Lever getLever() {
        return lever;
    }

    /** Nullable -- not every level needs a configured exit yet. */
    public Exit getExit() {
        return exit;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    /** Puddles and bushes -- anything with an onPlayerContact() effect. */
    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Lift> getLifts() {
        return lifts;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public List<Sign> getSigns() {
        return signs;
    }
}