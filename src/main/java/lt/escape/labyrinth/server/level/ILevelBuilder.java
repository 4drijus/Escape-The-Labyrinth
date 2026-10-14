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
    ILevelBuilder withDimensions(double width, double height);

    ILevelBuilder withPlayerStarts(double player1X, double player1Y, double player2X, double player2Y);

    ILevelBuilder addEnemy(Enemy enemy);

    ILevelBuilder addEnemy(Enemy enemy, Behavior startingBehavior);

    ILevelBuilder withDoor(Door door, double x, double y, double width, double height);

    ILevelBuilder withButton(Button button, double x, double y, double width, double height);

    ILevelBuilder withPressurePlate(PressurePlate pressurePlate, double x, double y, double width, double height);

    ILevelBuilder withLever(Lever lever);

    ILevelBuilder withExit(Exit exit);

    ILevelBuilder addWall(Wall wall);

    ILevelBuilder addTile(Tile tile);

    ILevelBuilder addLift(Lift lift);

    ILevelBuilder addBox(Box box);

    ILevelBuilder addPotion(Potion potion);

    ILevelBuilder addSign(Sign sign);

    Level build();
}