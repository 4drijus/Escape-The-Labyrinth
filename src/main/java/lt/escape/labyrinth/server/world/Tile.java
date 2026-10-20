package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.server.GameConstants;
import lt.escape.labyrinth.shared.PlayerState;

/**
 * Base class for fixed level terrain: walls, bushes, puddles. Unlike
 * Enemy/Bullet, tiles never move on their own.
 *
 * onPlayerContact() is polymorphic on purpose: GameServer can iterate
 * one List<Tile> and call onPlayerContact() on whichever ones a player
 * is touching, without an instanceof chain for puddle colors. Wall and
 * Bush simply don't override it (no effect on contact).
 */
public abstract class Tile extends GameObject {
    protected Tile(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }

    /** Uses the player's whole 40x40 box, so a puddle lying on the floor is touched by a player standing on it. */
    public boolean isTouching(PlayerState player) {
        return intersects(player.getX(), player.getY(),
                GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT);
    }

    /** No-op by default (Wall, Bush); puddle subclasses override this. */
    public void onPlayerContact(PlayerState player) {
    }
}