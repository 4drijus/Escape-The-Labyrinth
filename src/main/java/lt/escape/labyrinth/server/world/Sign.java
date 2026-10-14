package lt.escape.labyrinth.server.world;

/** "Ženklas – Sufleravimas žaidėjui." A hint the player can read. Display is a client-side concern -- this just carries the text. */
public class Sign extends GameObject {
    private final String suggestion;

    public Sign(double positionX, double positionY, double width, double height, String suggestion) {
        super(positionX, positionY, width, height);
        this.suggestion = suggestion;
    }

    public String showSuggestion() {
        return suggestion;
    }
}