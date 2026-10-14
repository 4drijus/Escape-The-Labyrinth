package lt.escape.labyrinth.shared;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static final GameState instance = new GameState();

    private final PlayerState player1;
    private final PlayerState player2;

    private final List<EnemyState> enemies = new ArrayList<>();

    private List<BulletState> bullets = new ArrayList<>();

    // Static level layout -- set once in GameServer.loadLevel(), never
    // touched again except DoorState's open/closed flag, which is
    // updated each tick alongside the Door it mirrors.
    private List<WallState> walls = new ArrayList<>();
    private List<TileState> tiles = new ArrayList<>();
    private DoorState door;
    private ButtonState button;
    private PressurePlateState pressurePlate; // nullable -- not every level has one
    private ExitState exit; // nullable -- not every level has one


    private GameState() {
        player1 = new PlayerState(1, 500, 480);
        player2 = new PlayerState(2, 100, 480);
    }

    public static GameState getInstance() { return instance; }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public PlayerState getPlayer(int playerId) {
        if (playerId == 1) {
            return player1;
        }

        if (playerId == 2) {
            return player2;
        }

        return null;
    }

    public List<EnemyState> getEnemies() {
        return enemies;
    }

    public List<BulletState> getBullets() {
        return bullets;
    }

    public void setBullets(List<BulletState> bullets) {
        this.bullets = bullets;
    }

    public List<WallState> getWalls() {
        return walls;
    }

    public void setWalls(List<WallState> walls) {
        this.walls = walls;
    }

    public List<TileState> getTiles() {
        return tiles;
    }

    public void setTiles(List<TileState> tiles) {
        this.tiles = tiles;
    }

    public DoorState getDoor() {
        return door;
    }

    public void setDoor(DoorState door) {
        this.door = door;
    }

    public ButtonState getButton() {
        return button;
    }

    public void setButton(ButtonState button) {
        this.button = button;
    }

    public PressurePlateState getPressurePlate() {
        return pressurePlate;
    }

    public void setPressurePlate(PressurePlateState pressurePlate) {
        this.pressurePlate = pressurePlate;
    }

    public ExitState getExit() {
        return exit;
    }

    public void setExit(ExitState exit) {
        this.exit = exit;
    }
}