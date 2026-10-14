package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.ButtonState;
import lt.escape.labyrinth.shared.DoorState;
import lt.escape.labyrinth.shared.EnemyState;
import lt.escape.labyrinth.shared.ExitState;
import lt.escape.labyrinth.shared.PressurePlateState;
import lt.escape.labyrinth.shared.TileState;
import lt.escape.labyrinth.shared.WallState;

import java.util.List;
import java.util.ArrayList;

public class ClientGameState implements ISubject {
    private double player1X;
    private double player1Y;

    private double player2X;
    private double player2Y;

    private List<EnemyState> enemies = List.of();
    private List<BulletState> bullets = List.of();

    // Static level geometry -- arrives once via the LEVEL: message, not every tick.
    private List<WallState> walls = List.of();
    private List<TileState> tiles = List.of();
    private DoorState door; // nullable until the LEVEL: message arrives
    private ButtonState button;
    private PressurePlateState pressurePlate;
    private ExitState exit;

    private final List<IObserver> observers = new ArrayList<>();

    @Override
    public synchronized void registerObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    public synchronized void update(
            double player1X,
            double player1Y,
            double player2X,
            double player2Y
    ) {
        this.player1X = player1X;
        this.player1Y = player1Y;

        this.player2X = player2X;
        this.player2Y = player2Y;

        notifyObservers();
    }

    public synchronized void updateEnemies(List<EnemyState> enemies) {

        this.enemies = enemies;
        notifyObservers();
    }

    public synchronized void updateBullets(List<BulletState> bullets) {

        this.bullets = bullets;
        notifyObservers();
    }

    /** Called once, when the LEVEL: message arrives. */
    public synchronized void updateLevelData(
            List<WallState> walls,
            List<TileState> tiles,
            DoorState door,
            ButtonState button,
            PressurePlateState pressurePlate,
            ExitState exit
    ) {
        this.walls = walls;
        this.tiles = tiles;
        this.door = door;
        this.button = button;
        this.pressurePlate = pressurePlate;
        this.exit = exit;
        notifyObservers();
    }

    /** Called every tick -- door position never changes, but open/closed does. */
    public synchronized void updateDoorOpen(boolean open) {
        if (door != null) {
            door.setOpen(open);
            notifyObservers();
        }
    }

    public synchronized double getPlayer1X() {
        return player1X;
    }

    public synchronized double getPlayer1Y() {
        return player1Y;
    }

    public synchronized double getPlayer2X() {
        return player2X;
    }

    public synchronized double getPlayer2Y() {
        return player2Y;
    }

    public synchronized List<EnemyState> getEnemies() {
        return enemies;
    }

    public synchronized List<BulletState> getBullets() {
        return bullets;
    }

    public synchronized List<WallState> getWalls() {
        return walls;
    }

    public synchronized List<TileState> getTiles() {
        return tiles;
    }

    /** Nullable until the LEVEL: message arrives. */
    public synchronized DoorState getDoor() {
        return door;
    }

    public synchronized ButtonState getButton() {
        return button;
    }

    public synchronized PressurePlateState getPressurePlate() {
        return pressurePlate;
    }

    public synchronized ExitState getExit() {
        return exit;
    }
}