package lt.escape.labyrinth.shared;

public class GameState {
    private final PlayerState player1;
    private final PlayerState player2;

    public GameState() {
        player1 = new PlayerState(1, 500, 480);
        player2 = new PlayerState(2, 100, 480);
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public PlayerState getPlayer(int playerId) {
        if (playerId == 1) {
            return player1;
        }

        if (playerId == 2) {
            return player2;
        }

        return null;
    }
}