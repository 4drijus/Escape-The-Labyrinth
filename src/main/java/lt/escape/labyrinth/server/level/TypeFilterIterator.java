package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.world.GameObject;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ITERATOR: wraps another iterator and returns only the objects of one
 * type, in the same order. The type can be a class (Switch, Enemy) or an
 * interface (Mechanism). Used through Level.objects(type).
 */
public class TypeFilterIterator<T> implements Iterator<T> {

    private final Iterator<GameObject> source;
    private final Class<T> type;
    private T nextMatch;

    TypeFilterIterator(Iterator<GameObject> source, Class<T> type) {
        this.source = source;
        this.type = type;
    }

    @Override
    public boolean hasNext() {
        while (nextMatch == null && source.hasNext()) {
            GameObject object = source.next();
            if (type.isInstance(object)) {
                nextMatch = type.cast(object);
            }
        }
        return nextMatch != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T match = nextMatch;
        nextMatch = null;
        return match;
    }
}
