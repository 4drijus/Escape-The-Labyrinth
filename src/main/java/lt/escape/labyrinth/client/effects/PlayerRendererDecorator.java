package lt.escape.labyrinth.client.effects;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

public abstract class PlayerRendererDecorator implements PlayerRenderer {
    protected final PlayerRenderer wrapped;

    protected PlayerRendererDecorator(PlayerRenderer wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    ) {
        wrapped.draw(g, sprite, x, y, moving, jumping);
    }
}