package lt.escape.labyrinth.client.scene;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

// Leaf: draws one image relative to its parent group.
public final class DecorationSprite implements SceneDecoration {

    private final Image image;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public DecorationSprite(
            Image image,
            int x,
            int y,
            int width,
            int height
    ) {
        this.image = Objects.requireNonNull(image);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    //transparency
/* 
    @Override
    public void add(SceneDecoration child) {
        throw new UnsupportedOperationException(
                "A single decoration cannot contain children"
        );
    }

    @Override
    public void remove(SceneDecoration child) {
        throw new UnsupportedOperationException(
                "A single decoration cannot contain children"
        );
    }
    */
    @Override
    public void draw(Graphics g, int parentX, int parentY) {
        g.drawImage(
                image,
                parentX + x,
                parentY + y,
                width,
                height,
                null
        );
    }
}
