package lt.escape.labyrinth.server.interactive;

import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

/**
 * "Norint pabaigti lygį abu žaidėjai turi pasiekti išėjimo zoną. Vienas
 * žaidėjas ją pasiekęs žaidimo pabaigti negalės." Both players must be
 * standing inside the exit zone at the same time before the level ends.
 */
public class Exit extends InteractiveElement {
    private final double positionX;
    private final double positionY;
    private final double width;
    private final double height;

    private boolean gameEnded = false;

    public Exit(double positionX, double positionY, double width, double height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean checkPlayerAmount(List<PlayerState> players) {
        for (PlayerState player : players) {
            if (!isInside(player)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInside(PlayerState player) {
        return player.getX() >= positionX && player.getX() <= positionX + width
                && player.getY() >= positionY && player.getY() <= positionY + height;
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