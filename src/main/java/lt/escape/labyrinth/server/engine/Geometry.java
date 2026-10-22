package lt.escape.labyrinth.server.engine;

/** Small shared helper for the subsystems: plain axis-aligned box overlap. */
final class Geometry {

    private Geometry() {
    }

    static boolean overlaps(double ax, double ay, double aw, double ah,
                            double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}