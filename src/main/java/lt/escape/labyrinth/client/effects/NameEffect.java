package lt.escape.labyrinth.client.effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

public final class NameEffect extends PlayerRendererDecorator {

    private final String name;

    public NameEffect(PlayerRenderer wrapped, String name) {
        super(wrapped);
        this.name = name;
    }

    @Override
    public void draw(
            Graphics g, Image sprite, int x, int y,
            boolean moving, boolean jumping
    ) {
        // draw the wrapped renderer and all existing effects.
        super.draw(g, sprite, x, y, moving, jumping);

        // draw the additional name layer.
        Graphics labelGraphics = g.create();

        try {
            labelGraphics.setFont(
                    new Font(Font.SANS_SERIF, Font.BOLD, 14)
            );

            FontMetrics metrics = labelGraphics.getFontMetrics();
            int spriteWidth = Math.max(0, sprite.getWidth(null));
            int textX = x + (spriteWidth - metrics.stringWidth(name)) / 2;
            int textY = y - 8;

            labelGraphics.setColor(Color.BLACK);
            labelGraphics.drawString(name, textX + 1, textY + 1);

            labelGraphics.setColor(Color.WHITE);
            labelGraphics.drawString(name, textX, textY);
        } finally {
            labelGraphics.dispose();
        }
    }

    public PlayerRenderer unwrap() {
        return wrapped;
    }
}