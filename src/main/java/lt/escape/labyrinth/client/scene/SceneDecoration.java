package lt.escape.labyrinth.client.scene;

import java.awt.Graphics;

// Component: a single decoration or a group of decorations.
public interface SceneDecoration {

    void draw(Graphics g, int parentX, int parentY);
    //transparency
    //void add(SceneDecoration child);
    //void remove(SceneDecoration child);
}
