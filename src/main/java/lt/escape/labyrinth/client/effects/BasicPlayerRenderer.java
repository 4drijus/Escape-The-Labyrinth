package lt.escape.labyrinth.client.effects;

import java.awt.Graphics;
import java.awt.Image;

public final class BasicPlayerRenderer implements PlayerRenderer {

    @Override
    public void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    ) {
        g.drawImage(sprite, x, y, null);
    }
}