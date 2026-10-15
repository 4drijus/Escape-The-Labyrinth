package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.shared.PlayerState;
import lt.escape.labyrinth.shared.Prototype;

/**
 * "Eleksyras – Naudojamas atgauti gyvybei" -- heals one life on contact.
 *
 * The diagram also shows Potion as an Inventory slot (something carried
 * and used later). That's a separate, bigger feature (pick up, store,
 * drink on command) that this doesn't attempt -- this is the simpler
 * "walk over it, heal immediately, it's gone" version, which is what
 * actually fits "level building."
 */
public class Potion extends GameObject implements Prototype<Potion> {
    private boolean collected = false;

    public Potion(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }

    @Override
    public Potion copy() {
        Potion copy = new Potion(positionX, positionY, width, height);
        copy.collected = collected;
        return copy;
    }

    public boolean isTouching(PlayerState player) {
        return !collected && intersects(player.getX(), player.getY(), 1, 1);
    }

    public void collect(PlayerState player) {
        if (collected) {
            return;
        }
        player.setHealth(player.getHealth() + 1);
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }
}