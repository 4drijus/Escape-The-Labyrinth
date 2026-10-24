package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.interactive.Button;
import lt.escape.labyrinth.server.interactive.Door;
import lt.escape.labyrinth.server.interactive.Exit;
import lt.escape.labyrinth.server.interactive.InteractiveElement;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.interactive.PressurePlate;
import lt.escape.labyrinth.server.world.Wall;

/**
 * Shared part of every concrete builder. It builds everything that does NOT
 * depend on difficulty straight from the LevelLayout (map size, spawns, walls,
 * door, button, exit). The two steps that DO depend on difficulty --
 * buildTiles() and buildEnemies() -- are left abstract for
 * Easy/Medium/HardLevelBuilder to fill in.
 */
public abstract class AbstractLevelBuilder implements ILevelBuilder {

    protected final InteractiveElementFactory elementFactory;
    protected final LevelLayout layout;
    protected Level level = new Level();

    protected AbstractLevelBuilder(InteractiveElementFactory elementFactory, LevelLayout layout) {
        this.elementFactory = elementFactory;
        this.layout = layout;
    }

    @Override
    public void reset() {
        level = new Level();
    }

    @Override
    public Level getResult() {
        if (level.getDoor() == null) {
            throw new IllegalStateException("A level needs a door before it can be finished");
        }
        Level finished = level;
        level = new Level(); // builder is immediately reusable
        return finished;
    }

    // ---- difficulty-independent steps ----
    @Override
    public void buildDimensions() {
        level.setDimensions(960, 600);
    }

    @Override
    public void buildPlayerStarts() {
        LevelLayout.Area p1 = layout.getPlayer1Start();
        LevelLayout.Area p2 = layout.getPlayer2Start();
        level.setPlayerStarts(p1.x(), p1.y(), p2.x(), p2.y());
    }

    @Override
    public void buildWalls() {
        for (Wall wall : layout.getWalls()) {
            level.addWall(wall);
        }
    }

    @Override
    public void buildDoorAndSwitches() {
        Door door = elementFactory.createDoor();
        place(door, layout.getDoor());
        level.setDoor(door);

        Button button = elementFactory.createButton(door);
        place(button, layout.getButton());
        level.setButton(button);

        LevelLayout.Area plateArea = layout.getPressurePlate();

        if (plateArea != null) {
            PressurePlate plate
                    = elementFactory.createPressurePlate(door);

            place(plate, plateArea);
            level.setPressurePlate(plate);
        }
    }

    @Override
    public void buildExit() {
        Exit exit = new Exit();
        place(exit, layout.getExit());
        level.setExit(exit);
    }

    /**
     * The factories create elements without a position; the builder gives each
     * one its bounds.
     */
    private static void place(InteractiveElement element, LevelLayout.Area area) {
        element.setBounds(area.x(), area.y(), area.width(), area.height());
    }

    // ---- difficulty-dependent steps: each concrete builder decides ----
    @Override
    public abstract void buildTiles();

    @Override
    public abstract void buildEnemies();
}
