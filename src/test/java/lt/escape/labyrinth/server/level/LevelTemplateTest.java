package lt.escape.labyrinth.server.level;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import lt.escape.labyrinth.server.enemy.InstaDeathTurret;
import lt.escape.labyrinth.server.enemy.NormalTurret;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.interactive.NormalDoor;
import lt.escape.labyrinth.server.interactive.NormalElementFactory;
import lt.escape.labyrinth.server.interactive.TimedDoor;
import lt.escape.labyrinth.server.interactive.TimedElementFactory;
import lt.escape.labyrinth.server.world.BlackPuddle;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.RedPuddle;

public class LevelTemplateTest {
    public static void main(String[] args) throws Exception {
        testTemplateOrderAndFinalMethod();
        testDirectorDelegation();
        testStandardLevels(new NormalElementFactory(), NormalDoor.class);
        testStandardLevels(new TimedElementFactory(), TimedDoor.class);
        System.out.println("All level template tests passed.");
    }

    private static void testTemplateOrderAndFinalMethod() throws Exception {
        check(Modifier.isFinal(AbstractLevelBuilder.class.getMethod("buildLevel").getModifiers()),
                "buildLevel must be final");
        RecordingBuilder builder = new RecordingBuilder();
        Level result = new LevelDirector().construct(builder);
        check(result != null, "template must return a level");
        check(builder.calls.equals(List.of("reset", "dimensions", "starts", "walls",
                "tiles", "enemies", "door", "exit", "result")), "incorrect order: " + builder.calls);
    }

    private static void testDirectorDelegation() {
        Level expected = new Level();
        List<String> calls = new ArrayList<>();
        ILevelBuilder builder = (ILevelBuilder) Proxy.newProxyInstance(
                ILevelBuilder.class.getClassLoader(), new Class<?>[] { ILevelBuilder.class },
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    if (method.getName().equals("buildLevel")) {
                        return expected;
                    }
                    throw new AssertionError("Director called " + method.getName());
                });
        check(new LevelDirector().construct(builder) == expected, "director must return builder result");
        check(calls.equals(List.of("buildLevel")), "director must only call buildLevel");
    }

    private static void testStandardLevels(InteractiveElementFactory factory, Class<?> doorType) {
        verifyLevel(new EasyLevelBuilder(factory, LevelLayout.cave()), LevelLayout.cave(),
                List.of(Bush.class), List.of(SpikyBush.class, NormalTurret.class),
                true, false, doorType);
        verifyLevel(new EasyLevelBuilder(factory, LevelLayout.plains()), LevelLayout.plains(),
                List.of(Bush.class), List.of(SpikyBush.class, NormalTurret.class),
                false, false, doorType);
        verifyLevel(new MediumLevelBuilder(factory, LevelLayout.hill()), LevelLayout.hill(),
                List.of(Bush.class, RedPuddle.class, BluePuddle.class),
                List.of(SpikyBush.class, NormalTurret.class, InstaDeathTurret.class),
                false, true, doorType);
        verifyLevel(new HardLevelBuilder(factory, LevelLayout.tower()), LevelLayout.tower(),
                List.of(Bush.class, BlackPuddle.class, RedPuddle.class, BluePuddle.class),
                List.of(SpikyBush.class, SpikyBush.class, NormalTurret.class, InstaDeathTurret.class,
                        NormalTurret.class),
                false, false, doorType);
    }

    private static void verifyLevel(AbstractLevelBuilder builder, LevelLayout layout,
            List<Class<?>> tiles, List<Class<?>> enemies, boolean hasPlate, boolean hasLift,
            Class<?> doorType) {
        LevelDirector director = new LevelDirector();
        builder.buildWalls();
        Level first = director.construct(builder);
        Level second = director.construct(builder);
        check(first != second, "builder must return fresh levels");
        check(first.getDoor() != second.getDoor(), "levels must not share doors");
        for (Level level : List.of(first, second)) {
            check(level.getWidth() == 960 && level.getHeight() == 600, "wrong dimensions");
            check(level.getPlayer1StartX() == layout.getPlayer1Start().x()
                    && level.getPlayer1StartY() == layout.getPlayer1Start().y()
                    && level.getPlayer2StartX() == layout.getPlayer2Start().x()
                    && level.getPlayer2StartY() == layout.getPlayer2Start().y(), "wrong starts");
            check(level.getWalls().size() == layout.getWalls().size(), "walls duplicated or missing");
            check(level.getTiles().stream().map(Object::getClass).toList().equals(tiles), "wrong tiles");
            check(level.getEnemies().stream().map(Object::getClass).toList().equals(enemies), "wrong enemies");
            check(doorType.isInstance(level.getDoor()), "wrong door type");
            check(level.getButton() != null && level.getButton().getTarget() == level.getDoor(),
                    "button must target this level's door");
            check(level.getExit() != null, "exit missing");
            check((level.getPressurePlate() != null) == hasPlate, "wrong pressure plate");
            if (hasPlate) {
                check(level.getPressurePlate().getTarget() == level.getDoor(), "plate targets wrong door");
            }
            check(level.getLifts().size() == (hasLift ? 1 : 0), "wrong number of lifts");
            check((level.getLever() != null) == hasLift, "wrong lever");
            if (hasLift) {
                check(level.getLever().getTarget() == level.getLifts().get(0), "lever targets wrong lift");
            }
            Level copy = level.copy();
            check(copy != level && copy.getDoor() != level.getDoor(), "copy must have a separate door");
            check(copy.getButton().getTarget() == copy.getDoor(), "copied button targets wrong door");
            if (hasPlate) {
                check(copy.getPressurePlate().getTarget() == copy.getDoor(), "copied plate targets wrong door");
            }
            if (hasLift) {
                check(copy.getLifts().get(0) != level.getLifts().get(0), "copy must have a separate lift");
                check(copy.getLever().getTarget() == copy.getLifts().get(0), "copied lever targets wrong lift");
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class RecordingBuilder extends AbstractLevelBuilder {
        private final List<String> calls = new ArrayList<>();

        private RecordingBuilder() {
            super(new NormalElementFactory(), LevelLayout.plains());
        }

        @Override public void reset() { calls.add("reset"); super.reset(); }
        @Override public void buildDimensions() { calls.add("dimensions"); super.buildDimensions(); }
        @Override public void buildPlayerStarts() { calls.add("starts"); super.buildPlayerStarts(); }
        @Override public void buildWalls() { calls.add("walls"); super.buildWalls(); }
        @Override public void buildTiles() { calls.add("tiles"); }
        @Override public void buildEnemies() { calls.add("enemies"); }
        @Override public void buildDoorAndSwitches() { calls.add("door"); super.buildDoorAndSwitches(); }
        @Override public void buildExit() { calls.add("exit"); super.buildExit(); }
        @Override public Level getResult() { calls.add("result"); return super.getResult(); }
    }
}
