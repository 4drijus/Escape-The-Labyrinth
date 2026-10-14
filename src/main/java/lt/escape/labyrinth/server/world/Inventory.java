package lt.escape.labyrinth.server.world;

/**
 * STUB. The diagram shows Inventory/HotbarRed/HotbarBlue with no
 * operations beyond Key.use() -- there's no specified "pick up", "drop",
 * or "swap slot" behavior to implement yet. This exists so the types
 * compile and match the diagram; an actual inventory system (what a
 * player is holding, a UI to show it, picking items up in the world)
 * is a separate, larger feature than what's described in the doc.
 */
public abstract class Inventory {
    private boolean occupied = false;

    public boolean isOccupied() {
        return occupied;
    }

    protected void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}