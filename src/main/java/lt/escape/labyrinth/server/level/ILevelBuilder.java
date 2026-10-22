package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.Behavior;
import lt.escape.labyrinth.server.enemy.Enemy;
import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.Lever;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.world.Box;
import lt.escape.labyrinth.server.world.Lift;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.Sign;
import lt.escape.labyrinth.server.world.Tile;
import lt.escape.labyrinth.server.world.Wall;

/**
 * Builder contract. LevelDirector depends on this interface, not the
 * concrete LevelBuilder -- the classic GoF Builder shape: Director +
 * Builder (this) + ConcreteBuilder (LevelBuilder) + Product (Level).
 * Lets a second builder implementation (e.g. one that loads a level
 * from a file instead of code) be swapped in without changing
 * LevelDirector at all.
 */
public interface ILevelBuilder {

    void reset();

    void buildDimensions();

    void buildPlayerStarts();

    void buildWalls();

    void buildTiles();

    void buildEnemies();

    void buildDoorAndSwitches();

    void buildExit();

    /** Hands over the finished Level. */
    Level getResult();
}