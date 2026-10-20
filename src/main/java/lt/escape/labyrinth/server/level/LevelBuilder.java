//package lt.escape.labyrinth.server.level;
//
//import lt.escape.labyrinth.server.enemy.Behavior;
//import lt.escape.labyrinth.server.enemy.Enemy;
//import lt.escape.labyrinth.server.interactive.Button;
//import lt.escape.labyrinth.server.interactive.Door;
//import lt.escape.labyrinth.server.interactive.Exit;
//import lt.escape.labyrinth.server.interactive.Lever;
//import lt.escape.labyrinth.server.interactive.PressurePlate;
//import lt.escape.labyrinth.server.world.Box;
//import lt.escape.labyrinth.server.world.Lift;
//import lt.escape.labyrinth.server.world.Potion;
//import lt.escape.labyrinth.server.world.Sign;
//import lt.escape.labyrinth.server.world.Tile;
//import lt.escape.labyrinth.server.world.Wall;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Concrete Builder. Lets a level be assembled step by step instead of
// * one large constructor call.
// *
// * Each method's return type here is LevelBuilder (not ILevelBuilder) --
// * a legal covariant-return override, since LevelBuilder is-an
// * ILevelBuilder. That keeps the fluent chain fully typed when someone
// * uses the concrete class directly, while still satisfying the
// * interface for code (like LevelDirector) that depends on ILevelBuilder.
// *
// * addEnemy(enemy, behavior) is the first real caller of
// * Enemy.setBehavior(): it lets a level override an enemy's starting
// * Behavior right after construction.
// */
//public class LevelBuilder implements ILevelBuilder {
//
//    private double width = 960;
//    private double height = 600;
//
//    private double player1StartX = 500;
//    private double player1StartY = 480;
//    private double player2StartX = 100;
//    private double player2StartY = 480;
//
//    private final List<Enemy> enemies = new ArrayList<>();
//
//    private Door door;
//    private Button button;
//    private PressurePlate pressurePlate;
//    private Lever lever;
//    private Exit exit;
//
//    private final List<Wall> walls = new ArrayList<>();
//    private final List<Tile> tiles = new ArrayList<>();
//    private final List<Lift> lifts = new ArrayList<>();
//    private final List<Box> boxes = new ArrayList<>();
//    private final List<Potion> potions = new ArrayList<>();
//    private final List<Sign> signs = new ArrayList<>();
//
//    @Override
//    public LevelBuilder withDimensions(double width, double height) {
//        this.width = width;
//        this.height = height;
//        return this;
//    }
//
//    @Override
//    public LevelBuilder withPlayerStarts(double player1X, double player1Y,
//                                         double player2X, double player2Y) {
//        this.player1StartX = player1X;
//        this.player1StartY = player1Y;
//        this.player2StartX = player2X;
//        this.player2StartY = player2Y;
//        return this;
//    }
//
//    /** Adds an enemy, keeping whatever Behavior its own constructor already set up. */
//    @Override
//    public LevelBuilder addEnemy(Enemy enemy) {
//        enemies.add(enemy);
//        return this;
//    }
//
//    /**
//     * Adds an enemy and immediately overrides its starting Behavior.
//     * This is Enemy.setBehavior() actually being used: the level picks
//     * the movement algorithm, not the enemy's constructor.
//     */
////    @Override
////    public LevelBuilder addEnemy(Enemy enemy, Behavior startingBehavior) {
////        enemy.setBehavior(startingBehavior);
////        enemies.add(enemy);
////        return this;
////    }
//    @Override
//    public LevelBuilder addEnemy(Enemy enemy, Behavior startingBehavior, Behavior endingBehavior) {
//        enemy.setBehaviorRotation(5,startingBehavior,endingBehavior);
//        enemies.add(enemy);
//        return this;
//    }
//
//    @Override
//    public LevelBuilder withDoor(Door door, double x, double y, double width, double height) {
//        door.setBounds(x, y, width, height);
//        this.door = door;
//        return this;
//    }
//
//    @Override
//    public LevelBuilder withButton(Button button, double x, double y, double width, double height) {
//        button.setBounds(x, y, width, height);
//        this.button = button;
//        return this;
//    }
//
//    @Override
//    public LevelBuilder withPressurePlate(PressurePlate pressurePlate, double x, double y, double width, double height) {
//        pressurePlate.setBounds(x, y, width, height);
//        this.pressurePlate = pressurePlate;
//        return this;
//    }
//
//    /** Optional -- a level can use Button/PressurePlate only, Lever only, or both. */
//    @Override
//    public LevelBuilder withLever(Lever lever) {
//        lever.setBounds(0, 0, width, height);
//        this.lever = lever;
//        return this;
//    }
//
//    /** Optional for now, while most levels still end some other way. */
//    @Override
//    public LevelBuilder withExit(Exit exit) {
//        exit.setBounds(0, 0, width, height);
//        this.exit = exit;
//        return this;
//    }
//
//    @Override
//    public LevelBuilder addWall(Wall wall) {
//        walls.add(wall);
//        return this;
//    }
//
//    /** Puddle or Bush -- anything with an onPlayerContact() effect. */
//    @Override
//    public LevelBuilder addTile(Tile tile) {
//        tiles.add(tile);
//        return this;
//    }
//
//    @Override
//    public LevelBuilder addLift(Lift lift) {
//        lifts.add(lift);
//        return this;
//    }
//
//    @Override
//    public LevelBuilder addBox(Box box) {
//        boxes.add(box);
//        return this;
//    }
//
//    @Override
//    public LevelBuilder addPotion(Potion potion) {
//        potions.add(potion);
//        return this;
//    }
//
//    @Override
//    public LevelBuilder addSign(Sign sign) {
//        signs.add(sign);
//        return this;
//    }
//
//    @Override
//    public Level build() {
//        if (exit == null) {
//            throw new IllegalStateException("Level needs an exit before build()");
//        }
//        return new Level(
//                width, height,
//                player1StartX, player1StartY,
//                player2StartX, player2StartY,
//                List.copyOf(enemies),
//                door,
//                button,
//                pressurePlate,
//                lever, exit,
//                List.copyOf(walls), List.copyOf(tiles), List.copyOf(lifts),
//                List.copyOf(boxes), List.copyOf(potions), List.copyOf(signs)
//        );
//    }
//}