package lt.escape.labyrinth.client.scene;

import java.awt.Graphics;
import java.util.Objects;

public final class DecorationSprite implements SceneDecoration {

    private final DecorationFlyweight flyweight;

    // Extrinsic state: individual for each decoration
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public DecorationSprite(
            DecorationFlyweight flyweight,
            int x,
            int y,
            int width,
            int height
    ) {
        this.flyweight = Objects.requireNonNull(flyweight);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g, int parentX, int parentY) {
        flyweight.draw(
                g,
                parentX + x,
                parentY + y,
                width,
                height
        );
    }

    // transparency
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
}