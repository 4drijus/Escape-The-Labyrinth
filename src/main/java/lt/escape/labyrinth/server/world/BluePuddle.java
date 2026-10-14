package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.shared.PlayerState;

/**
 * "Viena iš jų žalinga vienam žaidėjui" -- water puddle, slows the
 * "fire" character. Assumption: player 2 is the red/fire character
 * (matches GamePanel drawing player2 with the red sprite) -- flip the
 * playerId check below if that mapping is backwards in your build.
 */
public class BluePuddle extends Tile {
    private static final double SLOWED_SPEED = 1.5;

    public BluePuddle(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }

    @Override
    public void onPlayerContact(PlayerState player) {
        if (player.getPlayerId() != 2) {
            return; // only slows the fire character
        }
        if (player.getVelocityX() > SLOWED_SPEED) {
            player.setVelocityX(SLOWED_SPEED);
        } else if (player.getVelocityX() < -SLOWED_SPEED) {
            player.setVelocityX(-SLOWED_SPEED);
        }
    }
}