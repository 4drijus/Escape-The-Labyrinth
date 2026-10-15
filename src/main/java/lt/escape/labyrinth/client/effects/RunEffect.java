package lt.escape.labyrinth.client.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public final class RunEffect extends PlayerRendererDecorator {

    public RunEffect(PlayerRenderer wrapped) {
        super(wrapped);
    }

    @Override
    public void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    ) {
        super.draw(g, sprite, x, y, moving, jumping);

        if (!moving) {
            return;
        }

        int feetY = y + sprite.getHeight(null);
        Color previousColor = g.getColor();

        g.setColor(new Color(220, 205, 170, 190));
        g.fillOval(x - 8, feetY - 4, 6, 6);
        g.fillOval(x - 15, feetY - 7, 4, 4);
        g.fillOval(x + 5, feetY - 2, 4, 4);

        g.setColor(previousColor);
    }
}