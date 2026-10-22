package lt.escape.labyrinth.client;

import java.util.ArrayList;
import java.util.List;

public abstract class ISubject {
    private final List<IObserver> observers = new ArrayList<>();

    public synchronized void registerObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    public synchronized void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }
}
