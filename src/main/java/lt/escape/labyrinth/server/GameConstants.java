package lt.escape.labyrinth.server;

/**
 * Collision-box sizes in one place. PlayerState and Enemy carry no
 * width/height of their own, and these must match the sprite sizes drawn
 * by the client (players 40x40, enemies 30x30, bullets 8x8).
 */
public final class GameConstants {
    public static final double PLAYER_WIDTH = 40;
    public static final double PLAYER_HEIGHT = 40;
    public static final double ENEMY_SIZE = 30;
    public static final double BULLET_SIZE = 8;

    private GameConstants() {
    }
}