package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.world.Wall;
import lt.escape.labyrinth.shared.PlayerCommand;
import lt.escape.labyrinth.shared.PlayerState;

import java.util.List;

import static lt.escape.labyrinth.server.GameConstants.PLAYER_HEIGHT;
import static lt.escape.labyrinth.server.GameConstants.PLAYER_WIDTH;

/**
 * SUBSYSTEM: how players move. Turns commands into velocities, applies
 * gravity, and resolves collisions. Walls are the only floor, and walls plus
 * a closed door block movement.
 */
final class PlayerPhysics {

    private static final double MOVE_SPEED = 4;
    private static final double JUMP_VELOCITY = -8;
    private static final double GRAVITY = 0.5;

    private List<Wall> walls = List.of();
    private Door door;
    private double levelWidth;

    void load(Level level) {
        walls = level.getWalls();
        door = level.getDoor();
        levelWidth = level.getWidth();
    }

    /** Puts both players at the level's start positions, standing still. */
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

        for (Wall wall : walls) {
            if (wall.intersects(newX, player.getY(), PLAYER_WIDTH, PLAYER_HEIGHT)) {
                if (player.getVelocityX() > 0) {
                    newX = wall.getPositionX() - PLAYER_WIDTH; // moving right -> stop at wall's left edge
                } else if (player.getVelocityX() < 0) {
                    newX = wall.getPositionX() + wall.getWidth(); // moving left -> stop at wall's right edge
                }
            }
        }

        // A closed door blocks movement exactly like a Wall; an open one doesn't.
        if (doorBlocks(newX, player.getY())) {
            if (player.getVelocityX() > 0) {
                newX = door.getX() - PLAYER_WIDTH;
            } else if (player.getVelocityX() < 0) {
                newX = door.getX() + door.getWidth();
            }
        }

        player.setX(newX);
    }

    private void moveVertically(PlayerState player) {
        player.setVelocityY(player.getVelocityY() + GRAVITY);
        double newY = player.getY() + player.getVelocityY();

        boolean onGround = false;
        for (Wall wall : walls) {
            if (wall.intersects(player.getX(), newY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                if (player.getVelocityY() > 0) {
                    // falling -> land on top of the wall
                    newY = wall.getPositionY() - PLAYER_HEIGHT;
                    player.setVelocityY(0);
                    onGround = true;
                } else if (player.getVelocityY() < 0) {
                    // jumping -> bump into the wall's underside
                    newY = wall.getPositionY() + wall.getHeight();
                    player.setVelocityY(0);
                }
            }
        }

        if (doorBlocks(player.getX(), newY)) {
            if (player.getVelocityY() > 0) {
                newY = door.getY() - PLAYER_HEIGHT;
                player.setVelocityY(0);
                onGround = true;
            } else if (player.getVelocityY() < 0) {
                newY = door.getY() + door.getHeight();
                player.setVelocityY(0);
            }
        }

        player.setY(newY);
        player.setOnGround(onGround);
    }

    /** True if the door is closed and a player box at (x, y) would overlap it. */
    private boolean doorBlocks(double x, double y) {
        return door != null && !door.isOpen()
                && Geometry.overlaps(x, y, PLAYER_WIDTH, PLAYER_HEIGHT,
                door.getX(), door.getY(), door.getWidth(), door.getHeight());
    }
}