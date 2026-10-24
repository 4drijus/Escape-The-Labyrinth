package lt.escape.labyrinth.server.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.escape.labyrinth.server.world.Wall;

/**
 * WHERE things are, independent of how dangerous the level is.
 *
 * A layout holds the fixed geometry (walls, spawns, door, button, exit) and
 * named "slots": rectangles where a builder is allowed to put hazards.
 * Difficulty builders (Easy / Medium / Hard) pick which slots to fill, with
 * what, and how aggressively -- so adding a level means adding a layout (data),
 * not another builder class.
 *
 * Instances come from the static factories below; there is no public
 * constructor to fill in.
 */
public final class LevelLayout {

    /**
     * A rectangle on the map: where something (or a hazard slot) sits.
     */
    public record Area(double x, double y, double width, double height) {

    }

    private Area player1Start;
    private Area player2Start;
    private Area door;
    private Area button;
    private Area pressurePlate;
    private Area exit;

    private final List<Wall> walls = new ArrayList<>();
    /**
     * Floor-level (or platform-top) spots for spiky bushes.
     */
    private final List<Area> groundSlots = new ArrayList<>();
    /**
     * Thin strips lying on a surface, for puddles.
     */
    private final List<Area> puddleSlots = new ArrayList<>();
    /**
     * Spots for harmless bush cover.
     */
    private final List<Area> coverSlots = new ArrayList<>();
    /**
     * Free-air spots for turrets.
     */
    private final List<Area> turretSlots = new ArrayList<>();

    private LevelLayout() {
    }

    // ---------------------------------------------------------------
    // The three maps
    // ---------------------------------------------------------------
    /**
     * Flat ground with one low step in the middle.
     */
    public static LevelLayout plains() {
        LevelLayout l = new LevelLayout();
        l.addWall(0, 520, 960, 60);   // floor
        l.addWall(260, 470, 100, 50); // low step, 50px: jumpable

        l.player1Start = rect(500, 480, 40, 40);
        l.player2Start = rect(100, 480, 40, 40);
        l.door = rect(700, 420, 40, 100);
        l.button = rect(200, 490, 30, 30);
        l.exit = rect(780, 440, 180, 80);

        l.groundSlots.add(rect(440, 490, 30, 30));
        l.groundSlots.add(rect(570, 490, 30, 30));
        l.puddleSlots.add(rect(370, 510, 50, 10));
        l.puddleSlots.add(rect(610, 510, 50, 10));
        l.puddleSlots.add(rect(150, 510, 40, 10));
        l.coverSlots.add(rect(300, 440, 40, 30));   // on top of the step
        l.turretSlots.add(rect(600, 150, 30, 30));
        l.turretSlots.add(rect(380, 130, 30, 30));
        l.turretSlots.add(rect(820, 200, 30, 30));
        return l;
    }

    /**
     * A stepped hill; the button sits on the plateau, so someone has to climb
     * it.
     */
    public static LevelLayout hill() {
        LevelLayout l = new LevelLayout();
        l.addWall(0, 520, 960, 60);   // floor
        l.addWall(180, 470, 80, 50);  // up the hill, 50px per step
        l.addWall(260, 420, 80, 100);
        l.addWall(340, 370, 140, 150); // plateau, top at y=370
        l.addWall(480, 420, 80, 100);  // and back down
        l.addWall(560, 470, 80, 50);

        l.player1Start = rect(20, 480, 40, 40);
        l.player2Start = rect(70, 480, 40, 40);
        l.door = rect(780, 420, 40, 100);
        // Button far enough behind the door to avoid closing it on the player
        l.button = rect(880, 490, 25, 30);
        l.exit = rect(830, 440, 130, 80);

        l.groundSlots.add(rect(710, 490, 30, 30));
        l.groundSlots.add(rect(440, 340, 30, 30));   // on the plateau
        l.puddleSlots.add(rect(125, 510, 50, 10));
        l.puddleSlots.add(rect(645, 510, 50, 10));
        l.puddleSlots.add(rect(570, 460, 60, 10));   // on the last step down
        l.coverSlots.add(rect(280, 390, 40, 30));
        l.turretSlots.add(rect(250, 200, 30, 30));
        l.turretSlots.add(rect(700, 200, 30, 30));
        l.turretSlots.add(rect(500, 150, 30, 30));
        return l;
    }

    /**
     * A four-step tower with the button on top, and a hanging wall to crawl
     * under (40px gap = player height).
     */
    public static LevelLayout tower() {
        LevelLayout l = new LevelLayout();
        l.addWall(0, 520, 960, 60);   // floor
        l.addWall(140, 470, 80, 50);
        l.addWall(220, 420, 80, 100);
        l.addWall(300, 370, 80, 150);
        l.addWall(380, 320, 120, 200); // top at y=320
        l.addWall(700, 330, 30, 150);  // hanging wall

        l.player1Start = rect(10, 480, 40, 40);
        l.player2Start = rect(70, 480, 40, 40);
        l.door = rect(800, 420, 40, 100);
        l.button = rect(410, 290, 30, 30);
        l.exit = rect(850, 440, 110, 80);

        l.groundSlots.add(rect(465, 290, 30, 30));   // on top of the tower, next to the button
        l.groundSlots.add(rect(655, 490, 30, 30));
        l.puddleSlots.add(rect(530, 510, 50, 10));
        l.puddleSlots.add(rect(590, 510, 45, 10));
        l.puddleSlots.add(rect(745, 510, 40, 10));
        l.coverSlots.add(rect(150, 440, 40, 30));
        l.turretSlots.add(rect(300, 150, 30, 30));
        l.turretSlots.add(rect(650, 150, 30, 30));
        l.turretSlots.add(rect(520, 220, 30, 30));
        return l;
    }

    /**
     * Co-op level: one player holds the plate, the other crosses the door and
     * then opens it from the other side using the button.
     */
    public static LevelLayout cave() {
        LevelLayout l = new LevelLayout();

        // Floor and borders.
        l.addWall(0, 520, 960, 80);
        l.addWall(0, 0, 20, 520);
        l.addWall(940, 0, 20, 520);

        // Low platforms above the puddles.
        // Players can jump onto them or cross the puddles below.
        l.addWall(170, 460, 110, 15);
        l.addWall(350, 460, 110, 15);

        // A small final obstacle before the cooperative puzzle.
        l.addWall(520, 480, 50, 40);

        // Optional enemy shelf, away from the switches.
        l.addWall(40, 220, 100, 20);

        // Door frame: prevents jumping over the closed door.
        l.addWall(780, 0, 40, 420);

        // Players start together.
        l.player1Start = rect(50, 480, 40, 40);
        l.player2Start = rect(105, 480, 40, 40);

        // Plate BEFORE the door.
        l.pressurePlate = rect(680, 510, 55, 10);

        // Door blocks the passage.
        l.door = rect(780, 420, 40, 100);

        // Button AFTER the door: lets the second player release the first.
        l.button = rect(835, 490, 25, 30);

        // Both players finish here, beyond the button.
        l.exit = rect(875, 440, 60, 80);

        // Red and blue puddles on the floor, beneath the platforms.
        l.puddleSlots.add(rect(180, 510, 90, 10));
        l.puddleSlots.add(rect(360, 510, 90, 10));

        // Third slot for builders that use a black puddle.
        l.puddleSlots.add(rect(590, 510, 45, 10));

        // Cover before the final obstacle.
        //l.coverSlots.add(rect(470, 490, 40, 30));

        // Slots required by the existing difficulty builders.
        l.coverSlots.add(rect(135, 490, 40, 30));// neutral bush near the start.
        l.groundSlots.add(rect(475, 490, 30, 30));// spiky bush after the second platform.

        l.turretSlots.add(rect(70, 170, 30, 30));
        l.turretSlots.add(rect(350, 140, 30, 30));
        l.turretSlots.add(rect(560, 170, 30, 30));

        return l;
    }

    // ---------------------------------------------------------------
    private void addWall(double x, double y, double w, double h) {
        walls.add(new Wall(x, y, w, h));
    }

    private static Area rect(double x, double y, double w, double h) {
        return new Area(x, y, w, h);
    }

    public Area getPlayer1Start() {
        return player1Start;
    }

    public Area getPlayer2Start() {
        return player2Start;
    }

    public Area getDoor() {
        return door;
    }

    public Area getButton() {
        return button;
    }

    public Area getExit() {
        return exit;
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Area> getGroundSlots() {
        return Collections.unmodifiableList(groundSlots);
    }

    public List<Area> getPuddleSlots() {
        return Collections.unmodifiableList(puddleSlots);
    }

    public List<Area> getCoverSlots() {
        return Collections.unmodifiableList(coverSlots);
    }

    public List<Area> getTurretSlots() {
        return Collections.unmodifiableList(turretSlots);
    }

    public Area getPressurePlate() {
        return pressurePlate;
    }
}
