package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.enemy.Bullet;
import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.BULLET_SIZE;

/**
 * SUBSYSTEM: everything that hurts. Moves the enemies (each through its own
 * Strategy), collects the bullets they fire, flies the bullets (walls stop
 * them, players get hit), and applies contact damage from spiky bushes.
 */
final class EnemySystem {

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private List<Wall> walls = List.of();
    private double levelWidth;
    private double levelHeight;

    void load(Level level) {
        enemies.clear();
        enemies.addAll(level.getEnemies());
        bullets.clear();
        walls = level.getWalls();
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();
    }

    void update(List<PlayerState> players, double deltaTime) {
        for (Enemy enemy : enemies) {
            bullets.addAll(enemy.update(players, walls, deltaTime));
        }

        updateBullets(players, deltaTime);
        applyContactDamage(players);
    }

    private void updateBullets(List<PlayerState> players, double deltaTime) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update(deltaTime);

            if (bullet.isOutOfBounds(levelWidth, levelHeight) || hitsWall(bullet)) {
                it.remove();
                continue;
            }

            for (PlayerState player : players) {
                if (bullet.hits(player)) {
                    bullet.onHit(player);
                    it.remove();
                    break;
                }
            }
        }
    }

    private boolean hitsWall(Bullet bullet) {
        for (Wall wall : walls) {
            if (wall.intersects(bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE)) {
                return true;
            }
        }
        return false;
    }

    /** Enemies like SpikyBush that damage on contact rather than by firing. */
    private void applyContactDamage(List<PlayerState> players) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof SpikyBush bush) {
                for (PlayerState player : players) {
                    if (bush.isTouching(player)) {
                        bush.damage(player);
                    }
                }
            }
        }
    }

    List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    List<Bullet> getBullets() {
        return Collections.unmodifiableList(bullets);
    }
}