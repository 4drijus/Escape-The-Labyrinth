package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.shared.PlayerState;

/**
 * Lava/fire puddle, slows the "water" character. Assumption: player 1
 * is the blue/water character (matches GamePanel drawing player1 with
 * the blue sprite) -- flip the playerId check below if that's backwards.
 */
public class RedPuddle extends Tile {
    private static final double SLOWED_SPEED = 1.5;

    public RedPuddle(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }

    @Override
    public void onPlayerContact(PlayerState player) {
        if (player.getPlayerId() != 1) {
            return; // only slows the water character
        }
        if (player.getVelocityX() > SLOWED_SPEED) {
            player.setVelocityX(SLOWED_SPEED);
        } else if (player.getVelocityX() < -SLOWED_SPEED) {
            player.setVelocityX(-SLOWED_SPEED);
        }
    }
}