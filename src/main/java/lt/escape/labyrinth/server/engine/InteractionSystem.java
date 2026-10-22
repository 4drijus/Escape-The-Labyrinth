package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.InteractiveElement;
import lt.escape.labyrinth.server.interactive.Switch;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.ArrayList;
import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.PLAYER_HEIGHT;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_WIDTH;

/**
 * SUBSYSTEM: everything a player can touch that isn't dangerous. Puddles,
 * potions, lifts, the button and pressure plate, the door they open, and the
 * exit that ends the level.
 *
 * It is also the Bridge's client: it treats the button, pressure plate and
 * lever alike as Switches, without knowing what Mechanism each one operates.
 */
final class InteractionSystem {

    private Door door;
    private Exit exit;
    private List<Switch> switches = List.of();

    private List<Tile> tiles = List.of();
    private List<Lift> lifts = List.of();
    private List<Potion> potions = List.of();

    // Edge detection, so onPlayerEnter()/onPlayerLeave() fire once per step on/off, not every tick.
    // occupied[i] belongs to switches.get(i).
    private boolean[] occupied = new boolean[0];

    void load(Level level) {
        door = level.getDoor();
        exit = level.getExit();

        List<Switch> found = new ArrayList<>();
        for (Switch sw : new Switch[] {level.getButton(), level.getPressurePlate(), level.getLever()}) {
            if (sw != null) {
                found.add(sw);
            }
        }
        switches = found;
        occupied = new boolean[switches.size()];

        tiles = level.getTiles();
        lifts = level.getLifts();
        potions = level.getPotions();
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
        for (int i = 0; i < switches.size(); i++) {
            occupied[i] = anyPlayerOverlaps(players, switches.get(i));
        }
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
     *
     * Each switch decides what entering/leaving means (Button presses, Lever
     * pulls ...) and passes it on to whatever Mechanism it holds.
     */
    private void checkSwitches(List<PlayerState> players) {
        for (int i = 0; i < switches.size(); i++) {
            Switch sw = switches.get(i);
            boolean nowOccupied = anyPlayerOverlaps(players, sw);
            if (nowOccupied && !occupied[i]) {
                sw.onPlayerEnter();
            } else if (!nowOccupied && occupied[i]) {
                sw.onPlayerLeave();
            }
            occupied[i] = nowOccupied;
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