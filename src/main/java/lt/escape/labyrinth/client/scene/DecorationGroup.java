package lt.escape.labyrinth.client.scene;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Composite, safety variant:
// child management is available only on groups.
public final class DecorationGroup implements SceneDecoration {

    private final List<SceneDecoration> children = new ArrayList<>();

    private int x;
    private int y;

    public DecorationGroup(
            int x,
            int y,
            SceneDecoration... children
    ) {
        this.x = x;
        this.y = y;

        for (SceneDecoration child : children) {
            add(child);
        }
    }

    //transparency
    // @Override
    public void add(SceneDecoration child) {
        Objects.requireNonNull(child, "Child must not be null");

        if (child == this) {
            throw new IllegalArgumentException(
                    "A group cannot contain itself"
            );
        }

        if (child instanceof DecorationGroup group
                && group.contains(this)) {
            throw new IllegalArgumentException(
                    "Adding this group would create a cycle"
            );
        }

        if (!children.contains(child)) {
            children.add(child);
        }
    }

    //transparency
    // @Override
    public void remove(SceneDecoration child) {
        children.remove(child);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g, int parentX, int parentY) {
        int groupX = parentX + x;
        int groupY = parentY + y;

        for (SceneDecoration child : children) {
            child.draw(g, groupX, groupY);
        }
    }

    private boolean contains(SceneDecoration target) {
        for (SceneDecoration child : children) {
            if (child == target) {
                return true;
            }

            if (child instanceof DecorationGroup group
                    && group.contains(target)) {
                return true;
            }
        }

        return false;
    }
}