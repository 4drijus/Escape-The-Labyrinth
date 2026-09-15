package lt.escape.labyrinth.client;

public class ClientGameState {
    private double player1X;
    private double player1Y;

    private double player2X;
    private double player2Y;

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
}