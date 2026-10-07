package lt.escape.labyrinth.client;

import lt.escape.labyrinth.shared.BulletState;
import lt.escape.labyrinth.shared.EnemyState;

import java.util.List;
import java.util.ArrayList;

public class ClientGameState implements ISubject {
    private double player1X;
    private double player1Y;

    private double player2X;
    private double player2Y;

    private List<EnemyState> enemies = List.of();
    private List<BulletState> bullets = List.of();

    private final List<IObserver> observers = new ArrayList<>();

    @Override
    public synchronized void registerObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    public synchronized void update(
            double player1X,
            double player1Y,
            double player2X,
            double player2Y
    ) {
        this.player1X = player1X;
        this.player1Y = player1Y;

        this.player2X = player2X;
        this.player2Y = player2Y;

        notifyObservers();
    }

    public synchronized void updateEnemies(List<EnemyState> enemies) {

        this.enemies = enemies;
        notifyObservers();
    }

    public synchronized void updateBullets(List<BulletState> bullets) {

        this.bullets = bullets;
        notifyObservers();
    }

    public synchronized double getPlayer1X() {
        return player1X;
    }

    public synchronized double getPlayer1Y() {
        return player1Y;
    }

    public synchronized double getPlayer2X() {
        return player2X;
    }

    public synchronized double getPlayer2Y() {
        return player2Y;
    }

    public synchronized List<EnemyState> getEnemies() {
        return enemies;
    }

    public synchronized List<BulletState> getBullets() {
        return bullets;
    }
}