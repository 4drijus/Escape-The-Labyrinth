package lt.escape.labyrinth.server.engine;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.PLAYER_HEIGHT;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_WIDTH;
import lt.escape.labyrinth.server.collision.CollisionObstacle;
import lt.escape.labyrinth.server.collision.DoorCollisionAdapter;
import lt.escape.labyrinth.server.collision.WallCollisionAdapter;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;

/**
 * SUBSYSTEM: how players move. Turns commands into velocities, applies gravity,
 * and resolves collisions. Walls and closed doors block movement through a
 * common CollisionObstacle interface.
 */
final class PlayerPhysics {

    private static final double MOVE_SPEED = 4;
    private static final double JUMP_VELOCITY = -8;
    private static final double GRAVITY = 0.5;

    private List<CollisionObstacle> obstacles = List.of();
    private double levelWidth;

    void load(Level level) {
        List<CollisionObstacle> loaded = new ArrayList<>();

        for (Wall wall : level.getWalls()) {
            loaded.add(new WallCollisionAdapter(wall));
        }

        if (level.getDoor() != null) {
            loaded.add(new DoorCollisionAdapter(level.getDoor()));
        }

        obstacles = List.copyOf(loaded);
        levelWidth = level.getWidth();
    }

    /**
     * Puts both players at the level's start positions, standing still.
     */
    void placeAtStart(Level level, PlayerState player1, PlayerState player2) {
        place(player1, level.getPlayer1StartX(), level.getPlayer1StartY());
        place(player2, level.getPlayer2StartX(), level.getPlayer2StartY());
    }

    private void place(PlayerState player, double x, double y) {
        player.setX(x);
        player.setY(y);
        player.setVelocityX(0);
        player.setVelocityY(0);
    }

    void applyCommand(PlayerState player, PlayerCommand command) {
        switch (command) {
            case LEFT:
                player.setVelocityX(-MOVE_SPEED);
                break;
            case RIGHT:
                player.setVelocityX(MOVE_SPEED);
                break;
            case STOP:
                player.setVelocityX(0);
                break;
            case JUMP:
                if (player.isOnGround()) {
                    player.setVelocityY(JUMP_VELOCITY);
                    player.setOnGround(false);
                }
                break;
        }
    }

    void update(PlayerState player) {
        moveHorizontally(player);
        moveVertically(player);
    }

    private void moveHorizontally(PlayerState player) {
        double newX = player.getX() + player.getVelocityX();
        newX = Math.max(0, Math.min(newX, levelWidth - PLAYER_WIDTH));

        for (CollisionObstacle obstacle : obstacles) {
            if (!obstacle.blocksMovement()) {
                continue;
            }

            Rectangle2D bounds = obstacle.getBounds();

            if (bounds.intersects(
                    newX, player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT
            )) {
                if (player.getVelocityX() > 0) {
                    newX = bounds.getMinX() - PLAYER_WIDTH; // moving right -> stop at obstacle's left edge
                } else if (player.getVelocityX() < 0) {
                    newX = bounds.getMaxX(); // moving left -> stop at obstacle's right edge
                }
            }
        }

        player.setX(newX);
    }

    private void moveVertically(PlayerState player) {
        player.setVelocityY(player.getVelocityY() + GRAVITY);
        double newY = player.getY() + player.getVelocityY();

        boolean onGround = false;

        for (CollisionObstacle obstacle : obstacles) {
            if (!obstacle.blocksMovement()) {
                continue;
            }

            Rectangle2D bounds = obstacle.getBounds();

            if (bounds.intersects(
                    player.getX(), newY, PLAYER_WIDTH, PLAYER_HEIGHT
            )) {
                if (player.getVelocityY() > 0) {
                    // falling -> land on top of the obstacle
                    newY = bounds.getMinY() - PLAYER_HEIGHT;
                    player.setVelocityY(0);
                    onGround = true;
                } else if (player.getVelocityY() < 0) {
                    // jumping -> bump into the obstacle's underside
                    newY = bounds.getMaxY();
                    player.setVelocityY(0);
                }
            }
        }

        player.setY(newY);
        player.setOnGround(onGround);
    }
}
