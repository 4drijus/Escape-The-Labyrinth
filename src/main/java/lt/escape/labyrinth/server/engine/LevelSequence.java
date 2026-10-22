package lt.escape.labyrinth.server.engine;

import lt.escape.labyrinth.server.level.ILevelBuilder;
import lt.escape.labyrinth.server.level.Level;
import lt.escape.labyrinth.server.level.LevelDirector;

import java.util.List;

/**
 * SUBSYSTEM: which level comes next. Owns the Director and the list of
 * concrete builders (the Builder pattern), and asks them for a fresh Level
 * whenever one is needed. After the last level it wraps to the first.
 */
final class LevelSequence {

    private final LevelDirector director = new LevelDirector();
    private final List<ILevelBuilder> builders;
    private int index = 0;

    LevelSequence(List<ILevelBuilder> builders) {
        if (builders.isEmpty()) {
            throw new IllegalArgumentException("Need at least one level builder");
        }
        this.builders = List.copyOf(builders);
    }

    /** Builds the level the sequence currently points at. */
    Level current() {
        return director.construct(builders.get(index));
    }

    boolean isLast() {
        return index == builders.size() - 1;
    }

    /** Moves to the next level (wrapping after the last) and builds it. */
    Level advance() {
        index = (index + 1) % builders.size();
        return current();
    }

    /** Moves to the given 1-based level and builds it -- used when a checkpoint from another level is loaded. */
    Level jumpTo(int number) {
        index = number - 1;
        return current();
    }

    /** 1-based, for messages. */
    int number() {
        return index + 1;
    }
}