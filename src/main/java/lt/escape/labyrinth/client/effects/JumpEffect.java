package lt.escape.labyrinth.client.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public final class JumpEffect extends PlayerRendererDecorator {

    public JumpEffect(PlayerRenderer wrapped) {
        super(wrapped);
    }

    @Override
    public void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    ) {
        super.draw(g, sprite, x, y, moving, jumping);

        if (!jumping) {
            return;
        }

        int width = sprite.getWidth(null);
        int height = sprite.getHeight(null);
        Color previousColor = g.getColor();

        g.setColor(new Color(130, 220, 255, 180));
        g.drawOval(x - 4, y - 4, width + 8, height + 8);
        g.drawOval(x - 6, y - 6, width + 12, height + 12);

        g.setColor(previousColor);
    }
}