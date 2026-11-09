package lt.escape.labyrinth.server.level;

/**
 * Builder contract. The director delegates complete construction to the
 * builder, so implementations need not share the same construction recipe.
 */
public interface ILevelBuilder {

    /** Constructs and returns a complete level. */
    Level buildLevel();

    void reset();

    void buildDimensions();

    void buildPlayerStarts();

    void buildWalls();

    void buildTiles();

    void buildEnemies();

    void buildDoorAndSwitches();

    void buildExit();

    /** Hands over the finished Level. */
    Level getResult();
}