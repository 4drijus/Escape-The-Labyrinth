package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.InteractiveElement;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.PLAYER_HEIGHT;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_WIDTH;

/**
 * SUBSYSTEM: everything a player can touch that isn't dangerous. Puddles,
 * potions, lifts, the button and pressure plate, the door they open, and the
 * exit that ends the level.
 */
final class InteractionSystem {

    private Door door;
    private Button button;
    private PressurePlate pressurePlate;
    private Exit exit;

    private List<Tile> tiles = List.of();
    private List<Lift> lifts = List.of();
    private List<Potion> potions = List.of();

    // Edge detection, so press()/trigger()/release() fire once per step on/off, not every tick.
    private boolean buttonTouched;
    private boolean plateOccupied;

    void load(Level level) {
        door = level.getDoor();
        button = level.getButton();
        pressurePlate = level.getPressurePlate();
        exit = level.getExit();

        tiles = level.getTiles();
        lifts = level.getLifts();
        potions = level.getPotions();

        buttonTouched = false;
        plateOccupied = false;
    }

    void update(List<PlayerState> players, double deltaTime) {
        applyTileEffects(players);
        collectPotions(players);
        for (Lift lift : lifts) {
            lift.update(deltaTime);
        }
        checkSwitches(players);

        if (door != null) {
            door.update(deltaTime);
        }
    }

    /** True (once) when both players are inside the exit zone. */
    boolean isExitReached(List<PlayerState> players) {
        if (exit == null || exit.isGameEnded()) {
            return false;
        }
        if (exit.checkPlayerAmount(players)) {
            exit.endGame();
            return true;
        }
        return false;
    }

    /**
     * After a checkpoint load: a player already standing on the button or
     * plate counts as already on it, so loading doesn't press it again.
     */
    void resumeSwitches(List<PlayerState> players) {
        buttonTouched = button != null && anyPlayerOverlaps(players, button);
        plateOccupied = pressurePlate != null && anyPlayerOverlaps(players, pressurePlate);
    }

    Door getDoor() {
        return door;
    }

    /** Puddles and bushes -- whatever each Tile's onPlayerContact() does. */
    private void applyTileEffects(List<PlayerState> players) {
        for (Tile tile : tiles) {
            for (PlayerState player : players) {
                if (tile.isTouching(player)) {
                    tile.onPlayerContact(player);
                }
            }
        }
    }

    private void collectPotions(List<PlayerState> players) {
        for (Potion potion : potions) {
            for (PlayerState player : players) {
                if (potion.isTouching(player)) {
                    potion.collect(player);
                }
            }
        }
    }

    /**
     * Touching the button presses it once; stepping on the pressure plate
     * triggers it, stepping off releases it.
     */
    private void checkSwitches(List<PlayerState> players) {
        if (button != null) {
            boolean touched = anyPlayerOverlaps(players, button);
            if (touched && !buttonTouched) {
                button.press();
            }
            buttonTouched = touched;
        }

        if (pressurePlate != null) {
            boolean occupied = anyPlayerOverlaps(players, pressurePlate);
            if (occupied && !plateOccupied) {
                pressurePlate.trigger();
            } else if (!occupied && plateOccupied) {
                pressurePlate.release();
            }
            plateOccupied = occupied;
        }
    }

    private boolean anyPlayerOverlaps(List<PlayerState> players, InteractiveElement element) {
        for (PlayerState player : players) {
            if (Geometry.overlaps(player.getX(), player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT,
                    element.getX(), element.getY(), element.getWidth(), element.getHeight())) {
                return true;
            }
        }
        return false;
    }
}