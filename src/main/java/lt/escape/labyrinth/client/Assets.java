package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.EnemyType;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Loads and caches sprite images once, at startup. GamePanel.paintComponent()
 * should only ever read from here, never call ImageIO directly — decoding a
 * PNG on every repaint would tank your frame rate.
 */
public final class Assets {

    private static final Map<EnemyType, Image> ENEMY_SPRITES = new EnumMap<>(EnemyType.class);

    private static Image playerRedSprite;
    private static Image playerBlueSprite;
    private static Image bulletSprite;

    private Assets() {
    }

    /** Call this once, before the game window is shown (e.g. in ClientApplication.main()). */
    public static void load() {
        playerRedSprite = loadImage("/images/player_red.png");
        playerBlueSprite = loadImage("/images/player_blue.png");
        bulletSprite = loadImage("/images/bullet.png");

        ENEMY_SPRITES.put(EnemyType.SPIKY_BUSH, loadImage("/images/spiky_bush.png"));
        ENEMY_SPRITES.put(EnemyType.TURRET, loadImage("/images/turret.png"));
    }

    private static Image loadImage(String classpathResource) {
        try (InputStream in = Assets.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IOException("Missing resource on classpath: " + classpathResource);
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite: " + classpathResource, e);
        }
    }

    public static Image getPlayerRedSprite() {
        return playerRedSprite;
    }

    public static Image getPlayerBlueSprite() {
        return playerBlueSprite;
    }

    public static Image getBulletSprite() {
        return bulletSprite;
    }

    public static Image getEnemySprite(EnemyType type) {
        return ENEMY_SPRITES.get(type);
    }
}