package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.Checkpoint;
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
 *
 * It also keeps the Prototype copies: the level template a restart copies,
 * and the checkpoint that SAVE (F5) and LOAD (F9) copy.
 */
public class GameWorld {

    private static final double DELTA_TIME_SECONDS = 16.0 / 1000.0;

    private final GameState state;
    private final LevelSequence levels;
    private final PlayerPhysics physics = new PlayerPhysics();
    private final EnemySystem enemies = new EnemySystem();
    private final InteractionSystem interactions = new InteractionSystem();
    private final StateMirror mirror;

    // Prototype: the Level the Director built, kept untouched. The game
    // always plays a copy of it, so restartLevel() can take a fresh one.
    private Level levelTemplate;
    private Level currentLevel;

    // Null until a player saves. Save/load are requested from the
    // ClientHandler threads and done in update(), on the game loop.
    private Checkpoint checkpoint;
    private volatile boolean saveRequested;
    private volatile boolean loadRequested;

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
        levelTemplate = levels.current();
        load(levelTemplate.copy());
    }

    /**
     * Advances the whole game by one tick.
     *
     * @return true if the players just finished a level and the next one has
     *         been loaded -- the caller must then resend the level layout to the clients.
     *         Also true after a checkpoint is loaded, since it may be from another level.
     */
    public boolean update() {
        boolean layoutChanged = false;
        if (saveRequested) {
            saveRequested = false;
            saveCheckpoint();
        }
        if (loadRequested) {
            loadRequested = false;
            layoutChanged = loadCheckpoint();
        }

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
        return layoutChanged;
    }

    /**
     * A player pressed LEFT / RIGHT / STOP / JUMP.
     * SAVE / LOAD only set a flag; update() does them on the game loop thread.
     */
    public void applyCommand(int playerId, PlayerCommand command) {
        if (command == PlayerCommand.SAVE) {
            saveRequested = true;
            return;
        }
        if (command == PlayerCommand.LOAD) {
            loadRequested = true;
            return;
        }

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
        levelTemplate = levels.advance();
        System.out.println("Starting level " + levels.number());
        load(levelTemplate.copy());
    }

    private void saveCheckpoint() {
        checkpoint = new Checkpoint(levels.number(), currentLevel,
                state.getPlayer1(), state.getPlayer2(), enemies.getBullets()).copy();
        System.out.println("Checkpoint saved");
    }

    /** @return true if a checkpoint was loaded. */
    private boolean loadCheckpoint() {
        if (checkpoint == null) {
            System.out.println("No checkpoint saved yet");
            return false;
        }
        Checkpoint saved = checkpoint.copy();
        if (saved.getLevelNumber() != levels.number()) {
            levelTemplate = levels.jumpTo(saved.getLevelNumber()); // restarts go back to that level's start
        }
        load(saved.getLevel());
        state.getPlayer1().restoreFrom(saved.getPlayer1());
        state.getPlayer2().restoreFrom(saved.getPlayer2());
        enemies.addBullets(saved.getBullets());
        interactions.resumeSwitches(List.of(state.getPlayer1(), state.getPlayer2()));
        System.out.println("Checkpoint loaded");
        return true;
    }

    /**
     * A player died: rebuild the current level and restore both players' health.
     * Prototype: the level is a fresh copy of the untouched template, not built again.
     */
    private void restartLevel(PlayerState player1, PlayerState player2) {
        System.out.println("A player died -- restarting level " + levels.number());
        player1.resetHealth();
        player2.resetHealth();
        load(levelTemplate.copy());
    }

    /** Hands the new level to every subsystem. */
    private void load(Level level) {
        currentLevel = level;
        physics.load(level);
        enemies.load(level);
        interactions.load(level);
        mirror.publishLevel(level, enemies.getEnemies());
        physics.placeAtStart(level, state.getPlayer1(), state.getPlayer2());
    }
}