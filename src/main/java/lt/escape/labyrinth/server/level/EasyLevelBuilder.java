package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.NormalTurret;
import lt.escape.labyrinth.server.enemy.PatrolBehavior;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.enemy.StationaryBehavior;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.world.Bush;

/**
 * Concrete builder: EASY. One spiky bush, one slow turret that fires rarely
 * and keeps pausing, no puddles.
 */
public class EasyLevelBuilder extends AbstractLevelBuilder {

    public EasyLevelBuilder(InteractiveElementFactory elementFactory, LevelLayout layout) {
        super(elementFactory, layout);
    }

    @Override
    public void buildTiles() {
        LevelLayout.Area cover = layout.getCoverSlots().get(0);
        level.addTile(new Bush(cover.x(), cover.y(), cover.width(), cover.height()));
    }

    @Override
    public void buildEnemies() {
        LevelLayout.Area ground = layout.getGroundSlots().get(0);
        level.addEnemy(new SpikyBush(ground.x(), ground.y(), 1));

        LevelLayout.Area spot = layout.getTurretSlots().get(0);
        NormalTurret turret = new NormalTurret(spot.x(), spot.y(), 1, 3000); // fires every 3s
        turret.setBehaviorRotation(8.0, new PatrolBehavior(100, 40), new StationaryBehavior());
        level.addEnemy(turret);
    }
}