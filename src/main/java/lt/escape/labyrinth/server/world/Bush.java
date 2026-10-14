package lt.escape.labyrinth.server.world;

/**
 * "Krūmas, kuriame žaidėjas gali pasislėpti nuo tureto atakų" -- a bush
 * the player can hide inside to avoid turret attacks. No onPlayerContact
 * override: standing in a bush has no direct effect on the player.
 *
 * This only answers "is a player inside me" (inherited isTouching()).
 * It does NOT yet stop a Turret from targeting/hitting a hidden player
 * -- that needs Turret.nearestPlayer()/fire() to also receive the
 * level's bushes, which is a further change beyond today's work. Wiring
 * it in later: skip any player isTouching() returns true for when a
 * Turret picks its target.
 */
public class Bush extends Tile {
    public Bush(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }
}