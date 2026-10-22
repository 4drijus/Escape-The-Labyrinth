package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.InstaDeathTurret;
import lt.escape.labyrinth.server.enemy.NormalTurret;
import lt.escape.labyrinth.server.enemy.PatrolBehavior;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.enemy.StationaryBehavior;
import lt.escape.labyrinth.server.enemy.VerticalPatrolBehavior;
import lt.escape.labyrinth.server.enemy.ZigZagBehavior;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.RedPuddle;

/**
 * Concrete builder: MEDIUM. Slowing puddles, a turret that cycles through
 * three movement strategies, and a slow-firing insta-death turret.
 */
public class MediumLevelBuilder extends AbstractLevelBuilder {

    public MediumLevelBuilder(InteractiveElementFactory elementFactory, LevelLayout layout) {
        super(elementFactory, layout);
    }

    @Override
    public void buildTiles() {
        LevelLayout.Area cover = layout.getCoverSlots().get(0);
        level.addTile(new Bush(cover.x(), cover.y(), cover.width(), cover.height()));

        LevelLayout.Area red = layout.getPuddleSlots().get(0);
        level.addTile(new RedPuddle(red.x(), red.y(), red.width(), red.height()));
        LevelLayout.Area blue = layout.getPuddleSlots().get(1);
        level.addTile(new BluePuddle(blue.x(), blue.y(), blue.width(), blue.height()));
    }

    @Override
    public void buildEnemies() {
        LevelLayout.Area ground = layout.getGroundSlots().get(0);
        level.addEnemy(new SpikyBush(ground.x(), ground.y(), 1));

        LevelLayout.Area roamerSpot = layout.getTurretSlots().get(0);
        NormalTurret roamer = new NormalTurret(roamerSpot.x(), roamerSpot.y(), 1, 2500);
        roamer.setBehaviorRotation(5.0,
                new VerticalPatrolBehavior(60, 50),
                new ZigZagBehavior(100, 50, 60),
                new PatrolBehavior(120, 60));
        level.addEnemy(roamer);

        LevelLayout.Area sniperSpot = layout.getTurretSlots().get(1);
        InstaDeathTurret sniper = new InstaDeathTurret(sniperSpot.x(), sniperSpot.y(), 5000); // fires every 5s
        sniper.setBehaviorRotation(8.0, new ZigZagBehavior(60, 30, 40), new StationaryBehavior());
        level.addEnemy(sniper);
    }
}