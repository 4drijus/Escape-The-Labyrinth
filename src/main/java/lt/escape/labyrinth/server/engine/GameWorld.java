package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.level.EasyLevelBuilder;
import lt.escape.labyrinth.server.level.HardLevelBuilder;
import lt.escape.labyrinth.server.level.ILevelBuilder;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.level.LevelLayout;
import lt.escape.labyrinth.server.level.MediumLevelBuilder;
import lt.escape.labyrinth.shared.GameState;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * FACADE. The one class the rest of the server talks to.
 *
 * Behind it sit five subsystem classes that all have to be kept in step:
 * LevelSequence, PlayerPhysics, EnemySystem, InteractionSystem and
 * StateMirror. What the façade adds is the knowledge nobody else should
 * need: which subsystem to call, in what order, every tick, and how to swap
 * every one of them over when a level is completed.
 *
 * Clients (GameServer, ClientHandler) use only start(), update(),
 * applyCommand(), getState() and getLevelNumber(). The subsystem classes are
 * package-private, so they cannot even be referred to from outside this package.
 */
public class GameWorld {

    private static final double DELTA_TIME_SECONDS = 16.0 / 1000.0;

    private final GameState state;
    private final LevelSequence levels;
    private final PlayerPhysics physics = new PlayerPhysics();
    private final EnemySystem enemies = new EnemySystem();
    private final InteractionSystem interactions = new InteractionSystem();
    private final StateMirror mirror;

    /** The standard game: Easy on the plains, Medium on the hill, Hard on the tower. */
    public GameWorld(GameState state, InteractiveElementFactory elementFactory) {
        this(state, List.of(
                new EasyLevelBuilder(elementFactory, LevelLayout.cave()),
                new EasyLevelBuilder(elementFactory, LevelLayout.plains()),
                new MediumLevelBuilder(elementFactory, LevelLayout.hill()),
                new HardLevelBuilder(elementFactory, LevelLayout.tower())
        ));
    }

    /** A custom sequence of levels. */
    public GameWorld(GameState state, List<ILevelBuilder> levelBuilders) {
        this.state = state;
        this.mirror = new StateMirror(state);
        this.levels = new LevelSequence(levelBuilders);
    }

    /** Loads the first level. Call once, before the first update(). */
    public void start() {
        System.out.println("Starting level " + levels.number());
        load(levels.current());
    }

    /**
     * Advances the whole game by one tick.
     *
     * @return true if the players just finished a level and the next one has
     *         been loaded -- the caller must then resend the level layout to the clients.
     */
    public boolean update() {
        PlayerState player1 = state.getPlayer1();
        PlayerState player2 = state.getPlayer2();
        List<PlayerState> players = List.of(player1, player2);

        physics.update(player1);
        physics.update(player2);
        enemies.update(players, DELTA_TIME_SECONDS);
        interactions.update(players, DELTA_TIME_SECONDS);

        if (!player1.isAlive() || !player2.isAlive()) {
            restartLevel(player1, player2);
            return true;
        }

        mirror.sync(enemies.getEnemies(), enemies.getBullets(), interactions.getDoor());

        // Last on purpose: this replaces everything the other steps just worked on.
        if (interactions.isExitReached(players)) {
            nextLevel();
            return true;
        }
        return false;
    }

    /** A player pressed LEFT / RIGHT / STOP / JUMP. */
    public void applyCommand(int playerId, PlayerCommand command) {
        PlayerState player = state.getPlayer(playerId);
        if (player != null) {
            physics.applyCommand(player, command);
        }
    }

    /** What the clients should be shown. Read-only for callers. */
    public GameState getState() {
        return state;
    }

    /** 1-based number of the level being played. */
    public int getLevelNumber() {
        return levels.number();
    }

    private void nextLevel() {
        System.out.println("Level " + levels.number() + " complete!");
        if (levels.isLast()) {
            System.out.println("All levels complete -- starting again from level 1");
        }
        Level next = levels.advance();
        System.out.println("Starting level " + levels.number());
        load(next);
    }

    /** A player died: rebuild the current level and restore both players' health. */
    private void restartLevel(PlayerState player1, PlayerState player2) {
        System.out.println("A player died -- restarting level " + levels.number());
        player1.resetHealth();
        player2.resetHealth();
        load(levels.current());
    }

    /** Hands the new level to every subsystem. */
    private void load(Level level) {
        physics.load(level);
        enemies.load(level);
        interactions.load(level);
        mirror.publishLevel(level, enemies.getEnemies());
        physics.placeAtStart(level, state.getPlayer1(), state.getPlayer2());
    }
}