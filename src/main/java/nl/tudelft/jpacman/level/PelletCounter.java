package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;

/**
 * Counts remaining pellets on the board.
 */
public class PelletCounter {

    Board board;

    public PelletCounter(Board board) {
        this.board = board;
    }

    public int countRemaining() {
        int pellets = 0;

        for (int x = 0; x < board.getWidth(); x++) {
            pellets += countPelletsInColumn(x);
        }

        assert pellets >= 0;
        return pellets;
    }

    int countPelletsInColumn(int x) {
        int pellets = 0;

        for (int y = 0; y < board.getHeight(); y++) {
            pellets += countPelletsOnSquare(x, y);
        }

        return pellets;
    }

    int countPelletsOnSquare(int x, int y) {
        int pellets = 0;
        Square square = board.squareAt(x, y);

        for (Unit unit : square.getOccupants()) {
            if (unit instanceof Pellet) {
                pellets++;
            }
        }

        return pellets;
    }
}