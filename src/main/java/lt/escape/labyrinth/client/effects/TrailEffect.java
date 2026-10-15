package lt.escape.labyrinth.client.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public final class TrailEffect extends PlayerRendererDecorator {

    public TrailEffect(PlayerRenderer wrapped) {
        super(wrapped);
    }

    @Override
    public void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    ) {
        super.draw(g, sprite, x, y, moving, jumping);

        if (!moving || !jumping) {
            return;
        }

        int centerX = x + sprite.getWidth(null) / 2;
        int bottomY = y + sprite.getHeight(null);
        Color previousColor = g.getColor();

        g.setColor(new Color(255, 230, 100, 190));

        for (int i = 0; i < 3; i++) {
            int lineX = centerX - 6 + i * 6;
            g.drawLine(lineX, bottomY + 5, lineX, bottomY + 13);
        }

        g.setColor(previousColor);
    }
}