package lt.escape.labyrinth.client.effects;

import java.awt.Graphics;
import java.awt.Image;

public interface PlayerRenderer {
    void draw(
        Graphics g, Image sprite, int x, int y,
        boolean moving, boolean jumping
    );
}