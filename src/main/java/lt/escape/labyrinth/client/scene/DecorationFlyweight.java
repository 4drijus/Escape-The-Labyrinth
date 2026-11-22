package lt.escape.labyrinth.client.scene;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

public final class DecorationFlyweight {

    // Intrinsic state: shared by multiple decorations
    private final BufferedImage image;

    public DecorationFlyweight(BufferedImage image) {
        this.image = Objects.requireNonNull(image);
    }

    // Extrinsic state is supplied by the caller
    public void draw(Graphics g, int x, int y, int width, int height) {
        g.drawImage(image, x, y, width, height, null);
    }
}