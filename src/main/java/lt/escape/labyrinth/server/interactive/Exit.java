package lt.escape.labyrinth.server.interactive;

import lt.escape.labyrinth.server.GameConstants;
import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.Prototype;

import java.util.List;

/**
 * "Norint pabaigti lygį abu žaidėjai turi pasiekti išėjimo zoną. Vienas
 * žaidėjas ją pasiekęs žaidimo pabaigti negalės." Both players must be
 * standing inside the exit zone at the same time before the level ends.
 *
 * The zone's position and size are the bounds inherited from
 * InteractiveElement (setBounds / getX / getY / getWidth / getHeight).
 */
public class Exit extends InteractiveElement implements Prototype<Exit> {

    private boolean gameEnded = false;

    @Override
    public Exit copy() {
        Exit copy = copyBoundsTo(new Exit());
        copy.gameEnded = gameEnded;
        return copy;
    }

    public boolean checkPlayerAmount(List<PlayerState> players) {
        for (PlayerState player : players) {
            if (!isInside(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A player counts as "in the exit" when the centre of their 40x40 box is
     * inside the zone.
     */
    private boolean isInside(PlayerState player) {
        double centerX = player.getX() + GameConstants.PLAYER_WIDTH / 2;
        double centerY = player.getY() + GameConstants.PLAYER_HEIGHT / 2;
        return centerX >= getX() && centerX <= getX() + getWidth()
                && centerY >= getY() && centerY <= getY() + getHeight();
    }

    /** Call once checkPlayerAmount() has returned true. */
    public void endGame() {
        gameEnded = true;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    @Override
    public void update(double deltaSeconds) {
        super.update(deltaSeconds); // validates deltaSeconds, same as TimedDoor does
        // No time-based state of its own -- GameServer calls checkPlayerAmount()/endGame() directly each tick.
    }
}