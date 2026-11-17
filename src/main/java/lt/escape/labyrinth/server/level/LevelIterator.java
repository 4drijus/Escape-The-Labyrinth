package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.world.GameObject;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ITERATOR: walks everything in a Level, one group after another, without
 * the caller knowing that Level keeps several lists plus single fields.
 *
 * The order is fixed: walls, tiles, signs, lifts, boxes, potions, door,
 * button, pressure plate, lever, exit, enemies. Anything that refers to
 * objects by position in this order (enemy #2, a saved state) relies on it.
 *
 * A level without a lever, plate or exit simply has nothing there -- the
 * iterator never returns null. remove() is not supported: a Level can only
 * be changed by its builders.
 */
public class LevelIterator implements Iterator<GameObject> {

    private final List<List<? extends GameObject>> groups;
    private int group = 0;
    private int index = 0;

    LevelIterator(Level level) {
        groups = List.of(
                level.getWalls(),
                level.getTiles(),
                level.getSigns(),
                level.getLifts(),
                level.getBoxes(),
                level.getPotions(),
                single(level.getDoor()),
                single(level.getButton()),
                single(level.getPressurePlate()),
                single(level.getLever()),
                single(level.getExit()),
                level.getEnemies());
    }

    @Override
    public boolean hasNext() {
        while (group < groups.size() && index >= groups.get(group).size()) {
            group++;
            index = 0;
        }
        return group < groups.size();
    }

    @Override
    public GameObject next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return groups.get(group).get(index++);
    }

    private static List<GameObject> single(GameObject object) {
        return object == null ? List.of() : List.of(object);
    }
}
