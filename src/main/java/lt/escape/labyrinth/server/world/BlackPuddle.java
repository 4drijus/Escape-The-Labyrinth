package lt.escape.labyrinth.server.world;

import lt.escape.labyrinth.shared.PlayerState;

/** "Juoda bala žalinga abiejiems žaidėjams" -- harmful to both players, no exceptions. */
public class BlackPuddle extends Tile {
    private final int damageAmount;

    public BlackPuddle(double positionX, double positionY, double width, double height, int damageAmount) {
        super(positionX, positionY, width, height);
        this.damageAmount = damageAmount;
    }

    @Override
    public void onPlayerContact(PlayerState player) {
        player.takeDamage(damageAmount);
    }
}