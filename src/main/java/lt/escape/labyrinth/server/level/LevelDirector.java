package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.InstaDeathTurret;
import lt.escape.labyrinth.server.enemy.NormalTurret;
import lt.escape.labyrinth.server.enemy.PatrolBehavior;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.world.BlackPuddle;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.Potion;
import lt.escape.labyrinth.server.world.RedPuddle;
import lt.escape.labyrinth.server.world.Wall;

/**
 * Director: knows the "recipe" for each concrete level, but delegates
 * all actual assembly to a builder. GameServer only ever calls one of
 * these methods -- it never touches a builder directly.
 *
 * Each method below declares its local variable as ILevelBuilder, not
 * LevelBuilder -- the only place the concrete class is named is the
 * single `new LevelBuilder()` call. Swapping in a different
 * ILevelBuilder implementation later wouldn't touch anything else here.
 *
 * withDoor/withButton/withPressurePlate now also take a position --
 * those classes don't carry x/y themselves, so this is where each
 * level decides where they sit visually. Positions below are a first
 * guess at your tile scale; nudge them once you see the level rendered.
 */
public final class LevelDirector {

    private LevelDirector() {
    }

    /**
     * Same enemy layout GameServer used to hardcode, plus walls so the
     * patrolling turret's wall-collision is actually exercised, and a
     * puddle + potion to show the new world-object builder methods.
     */
    public static Level buildLevelOne(InteractiveElementFactory elementFactory) {
        Door door = elementFactory.createDoor();
        Button button = elementFactory.createButton(door);
        PressurePlate pressurePlate = elementFactory.createPressurePlate(door);

        ILevelBuilder builder = new LevelBuilder();

        return builder
                .withDimensions(960, 480)
                // Floor -- required now that GameServer has no hardcoded ground;
                // players spawn at y=480 and this wall's top is exactly y=520,
                // matching the old floor collision's position.
                .addWall(new Wall(0, 520, 960, 60))
                .withPlayerStarts(500, 480, 100, 480)
                .addEnemy(new SpikyBush(300, 480, 1))
                // Patrols a 150px range but there's a wall at x=820 --
                // it will turn around at the wall well before reaching
                // the edge of its own patrol range.
                .addEnemy(new NormalTurret(700, 200, 1, 2000), new PatrolBehavior(150, 80))
                .addEnemy(new InstaDeathTurret(200, 200, 3000))
                .addWall(new Wall(820, 150, 20, 120))
                .addTile(new BluePuddle(400, 450, 60, 30))
                .addPotion(new Potion(650, 450, 20, 20))
                .withDoor(door, 900, 380, 40, 100)
                .withButton(button, 50, 440, 30, 30)
                .withPressurePlate(pressurePlate, 150, 450, 50, 20)
                .build();
    }

    public static Level buildLevelTwo(InteractiveElementFactory elementFactory) {
        Door door = elementFactory.createDoor();
        Button button = elementFactory.createButton(door);
        PressurePlate pressurePlate = elementFactory.createPressurePlate(door);

        ILevelBuilder builder = new LevelBuilder();

        return builder
                .withDimensions(960, 480)
                // Floor -- required now that GameServer has no hardcoded ground;
                // players spawn at y=480 and this wall's top is exactly y=520,
                // matching the old floor collision's position.
                .addWall(new Wall(0, 520, 960, 60))
                .withPlayerStarts(500, 480, 100, 480)
                .addEnemy(new SpikyBush(250, 480, 1))
                .addEnemy(new SpikyBush(600, 480, 1))
                .addEnemy(new InstaDeathTurret(800, 150, 2500))
                .withDoor(door, 900, 380, 40, 100)
                .withButton(button, 50, 440, 30, 30)
                .withPressurePlate(pressurePlate, 400, 450, 50, 20)
                .build();
    }

    /**
     * Loosely modeled on a Fireboy & Watergirl-style layout: hazards spread
     * across different heights, one that patrols back and forth, and a
     * high-risk instant-death turret tucked in a corner.
     */
    public static Level buildLevelThree(InteractiveElementFactory elementFactory) {
        Door door = elementFactory.createDoor();
        Button button = elementFactory.createButton(door);
        PressurePlate pressurePlate = elementFactory.createPressurePlate(door);

        ILevelBuilder builder = new LevelBuilder();

        return builder
                .withDimensions(960, 480)
                // Floor -- required now that GameServer has no hardcoded ground;
                // players spawn at y=480 and this wall's top is exactly y=520,
                // matching the old floor collision's position.
                .addWall(new Wall(0, 520, 960, 60))
                .withPlayerStarts(60, 480, 150, 480)
                .addEnemy(new SpikyBush(50, 100, 1))
                .addEnemy(new NormalTurret(480, 220, 1, 1800), new PatrolBehavior(100, 60))
                .addEnemy(new SpikyBush(780, 260, 1))
                .addEnemy(new InstaDeathTurret(850, 400, 3000))
                .withDoor(door, 900, 380, 40, 100)
                .withButton(button, 500, 440, 30, 30)
                .withPressurePlate(pressurePlate, 300, 450, 50, 20)
                .build();
    }

    /**
     * Tiles (all four: both puddle colors, the black one, and a bush),
     * two walls, one patrolling NormalTurret, a door opened by a single
     * button (no pressure plate this time -- build() no longer requires
     * one), and an Exit both players must reach together.
     */
    public static Level buildLevelFour(InteractiveElementFactory elementFactory) {
        Door door = elementFactory.createDoor();
        Button button = elementFactory.createButton(door);

        ILevelBuilder builder = new LevelBuilder();

        return builder
                .withDimensions(960, 480)
                // Floor -- required now that GameServer has no hardcoded ground;
                // players spawn at y=480 and this wall's top is exactly y=520,
                // matching the old floor collision's position.
                .addWall(new Wall(0, 520, 1000, 40))
                .withPlayerStarts(500, 480, 100, 480)
                .addWall(new Wall(0, 360, 460, 20))
                .addWall(new Wall(0, 300, 255, 20))
                .addWall(new Wall(320, 300, 140, 20))

                .addWall(new Wall(420, 470, 20, 40))
                .addWall(new Wall(480, 420, 20, 60))
                .addWall(new Wall(760, 100, 20, 220))
                .addWall(new Wall(15, 15, 20, 20))
                .addEnemy(new NormalTurret(600, 200, 1, 1800), new PatrolBehavior(120, 70))
                .addTile(new BluePuddle(200, 450, 60, 15))
                .addTile(new RedPuddle(560, 450, 60, 15))
                .addTile(new BlackPuddle(850, 480, 50, 15, 1))
                .addTile(new Bush(660, 420, 40, 40))
                .withDoor(door, 900, 440, 40, 80)
                .withButton(button, 50, 440, 30, 30)
                .withExit(new Exit(780, 440, 60, 60))
                .build();
    }
}