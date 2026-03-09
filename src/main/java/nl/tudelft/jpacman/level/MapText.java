package nl.tudelft.jpacman.level;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.jpacman.PacmanConfigurationException;

/**
 * Represents textual map input and handles validation and conversion.
 */
public class MapText {

    List<String> rows;

    public MapText(List<String> text) {
        if (text == null) {
            rows = null;
            return;
        }

        rows = new ArrayList<>(text);
    }

    public void validate() {
        checkTextExists();
        checkTextHasRows();

        String firstRow = getFirstRow();
        checkRowNotEmpty(firstRow);

        int expectedWidth = firstRow.length();
        checkEqualWidths(expectedWidth);
    }

    void checkTextExists() {
        if (rows == null) {
            throw new PacmanConfigurationException("Input text cannot be null.");
        }
    }

    void checkTextHasRows() {
        if (rows.isEmpty()) {
            throw new PacmanConfigurationException(
                "Input text must consist of at least 1 row."
            );
        }
    }

    void checkRowNotEmpty(String row) {
        if (row.length() == 0) {
            throw new PacmanConfigurationException(
                "Input text lines cannot be empty."
            );
        }
    }

    void checkEqualWidths(int expectedWidth) {
        for (String row : rows) {
            checkSingleRowWidth(row, expectedWidth);
        }
    }

    void checkSingleRowWidth(String row, int expectedWidth) {
        if (row.length() != expectedWidth) {
            throw new PacmanConfigurationException(
                "Input text lines are not of equal width."
            );
        }
    }

    public char[][] toCharGrid() {
        int height = rows.size();
        int width = getFirstRow().length();

        char[][] map = new char[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String row = rows.get(y);
                map[x][y] = row.charAt(x);
            }
        }

        return map;
    }

    String getFirstRow() {
        return rows.get(0);
    }
}