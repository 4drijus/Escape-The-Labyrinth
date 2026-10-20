package lt.escape.labyrinth.server.level;

import lt.escape.labyrinth.server.enemy.ChaseBehavior;
import lt.escape.labyrinth.server.enemy.InstaDeathTurret;
import lt.escape.labyrinth.server.enemy.NormalTurret;
import lt.escape.labyrinth.server.enemy.PatrolBehavior;
import lt.escape.labyrinth.server.enemy.SpikyBush;
import lt.escape.labyrinth.server.enemy.StationaryBehavior;
import lt.escape.labyrinth.server.enemy.VerticalPatrolBehavior;
import lt.escape.labyrinth.server.enemy.ZigZagBehavior;
import lt.escape.labyrinth.server.interactive.InteractiveElementFactory;
import lt.escape.labyrinth.server.world.BlackPuddle;
import lt.escape.labyrinth.server.world.BluePuddle;
import lt.escape.labyrinth.server.world.Bush;
import lt.escape.labyrinth.server.world.RedPuddle;

/**
 * Concrete builder: HARD. A damaging black puddle on top of the slowing
 * ones, two spiky bushes, fast turrets that fire often, and two turrets that
 * hunt the nearest player.
 */
public class HardLevelBuilder extends AbstractLevelBuilder {

    public HardLevelBuilder(InteractiveElementFactory elementFactory, LevelLayout layout) {
        super(elementFactory, layout);
    }

    @Override
    public void buildTiles() {
        LevelLayout.Area cover = layout.getCoverSlots().get(0);
        level.addTile(new Bush(cover.x(), cover.y(), cover.width(), cover.height()));

        LevelLayout.Area black = layout.getPuddleSlots().get(0);
        level.addTile(new BlackPuddle(black.x(), black.y(), black.width(), black.height(), 1));
        LevelLayout.Area red = layout.getPuddleSlots().get(1);
        level.addTile(new RedPuddle(red.x(), red.y(), red.width(), red.height()));
        LevelLayout.Area blue = layout.getPuddleSlots().get(2);
        level.addTile(new BluePuddle(blue.x(), blue.y(), blue.width(), blue.height()));
    }

    @Override
    public void buildEnemies() {
        for (int i = 0; i < 2; i++) {
            LevelLayout.Area ground = layout.getGroundSlots().get(i);
            level.addEnemy(new SpikyBush(ground.x(), ground.y(), 1));
        }

        LevelLayout.Area shifterSpot = layout.getTurretSlots().get(0);
        NormalTurret shifter = new NormalTurret(shifterSpot.x(), shifterSpot.y(), 1, 1800);
        shifter.setBehaviorRotation(4.0,
                new PatrolBehavior(150, 80),
                new VerticalPatrolBehavior(70, 80),
                new ZigZagBehavior(120, 60, 80));
                //new ChaseBehavior(50, 120));
        level.addEnemy(shifter);

        LevelLayout.Area sniperSpot = layout.getTurretSlots().get(1);
        InstaDeathTurret sniper = new InstaDeathTurret(sniperSpot.x(), sniperSpot.y(), 3500);
        sniper.setBehaviorRotation(5.0, new ZigZagBehavior(80, 40, 60), new ChaseBehavior(35, 160));
        level.addEnemy(sniper);

        LevelLayout.Area hunterSpot = layout.getTurretSlots().get(2);
        NormalTurret hunter = new NormalTurret(hunterSpot.x(), hunterSpot.y(), 1, 2200);
        hunter.setBehaviorRotation(6.0, new ChaseBehavior(60, 100), new StationaryBehavior());
        level.addEnemy(hunter);
    }
}