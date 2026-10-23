package lt.escape.labyrinth.server.interactive;

public final class TimedDoor extends Door {
    private final double durationSeconds;
    private double remainingSeconds;
    public TimedDoor(double durationSeconds) {
        if (!Double.isFinite(durationSeconds) || durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be finite and positive");
        }
        this.durationSeconds = durationSeconds;
    }
    // Keeps the countdown, so a copy of an open door closes at the same time.
    private TimedDoor(TimedDoor source) {
        super(source);
        durationSeconds = source.durationSeconds;
        remainingSeconds = source.remainingSeconds;
    }
    @Override public TimedDoor copy() { return new TimedDoor(this); }
    @Override public void open() {
        super.open();
        remainingSeconds = durationSeconds;
    }
    @Override public void close() {
        super.close();
        remainingSeconds = 0;
    }
    @Override public void update(double deltaSeconds) {
        super.update(deltaSeconds);
        if (isOpen()) {
            remainingSeconds = Math.max(0, remainingSeconds - deltaSeconds);
            if (remainingSeconds == 0) close();
        }
    }
}
