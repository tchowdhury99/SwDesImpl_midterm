package nl.tudelft.jpacman.level;

/**
 * Encapsulates player score behavior.
 */
public class PlayerScore {

    int points;

    public PlayerScore() {
        this.points = 0;
    }

    public void add(int amount) {
        points += amount;
    }

    public int value() {
        return points;
    }
}