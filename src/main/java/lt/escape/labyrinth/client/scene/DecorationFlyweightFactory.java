package lt.escape.labyrinth.client.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class DecorationFlyweightFactory {

    private final Map<String, DecorationFlyweight> cache = new HashMap<>();

    public DecorationFlyweight getRock() {
        return cache.computeIfAbsent(
                "rock",
                key -> new DecorationFlyweight(createRockImage())
        );
    }

    public int getCachedCount() {
        return cache.size();
    }

    // Package-private: also used by the comparison benchmark
    static BufferedImage createRockImage() {
        BufferedImage image = new BufferedImage(
                40, 24, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = image.createGraphics();

        try {
            int[] xs = {1, 8, 27, 38, 35, 5};
            int[] ys = {19, 5, 2, 13, 22, 23};

            g.setColor(new Color(112, 119, 132));
            g.fillPolygon(xs, ys, xs.length);

            g.setColor(new Color(162, 170, 183));
            g.drawLine(9, 6, 26, 3);
            g.drawLine(26, 3, 35, 13);

            g.setColor(new Color(68, 74, 86));
            g.drawLine(7, 21, 33, 20);
        } finally {
            g.dispose();
        }

        return image;
    }
}