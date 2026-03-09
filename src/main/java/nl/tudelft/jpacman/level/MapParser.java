package nl.tudelft.jpacman.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Creates new {@link Level}s from text representations.
 *
 * @author Jeroen Roosen
 */
public class MapParser {

    LevelFactory levelCreator;

    BoardFactory boardCreator;

    public MapParser(LevelFactory levelFactory, BoardFactory boardFactory) {
        this.levelCreator = levelFactory;
        this.boardCreator = boardFactory;
    }

    public Level parseMap(char[][] map) {
        int width = map.length;
        int height = map[0].length;

        Square[][] grid = new Square[width][height];
        List<Ghost> ghosts = new ArrayList<>();
        List<Square> startPositions = new ArrayList<>();

        makeGrid(map, width, height, grid, ghosts, startPositions);

        Board board = boardCreator.createBoard(grid);
        return levelCreator.createLevel(board, ghosts, startPositions);
    }

    void makeGrid(char[][] map, int width, int height,
                  Square[][] grid, List<Ghost> ghosts, List<Square> startPositions) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char symbol = map[x][y];
                addSquare(grid, ghosts, startPositions, x, y, symbol);
            }
        }
    }

    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                             List<Square> startPositions, int x, int y, char symbol) {
        switch (symbol) {
            case ' ':
                addGroundSquare(grid, x, y);
                break;
            case '#':
                addWallSquare(grid, x, y);
                break;
            case '.':
                addPelletSquare(grid, x, y);
                break;
            case 'G':
                addGhostToGrid(grid, ghosts, x, y);
                break;
            case 'P':
                addPlayerStartSquare(grid, startPositions, x, y);
                break;
            default:
                throwInvalidCharacter(x, y, symbol);
                break;
        }
    }

    void addGroundSquare(Square[][] grid, int x, int y) {
        grid[x][y] = boardCreator.createGround();
    }

    void addWallSquare(Square[][] grid, int x, int y) {
        grid[x][y] = boardCreator.createWall();
    }

    void addPelletSquare(Square[][] grid, int x, int y) {
        Square pelletSquare = boardCreator.createGround();
        Pellet pellet = levelCreator.createPellet();

        grid[x][y] = pelletSquare;
        pellet.occupy(pelletSquare);
    }

    void addGhostToGrid(Square[][] grid, List<Ghost> ghosts, int x, int y) {
        Ghost ghost = levelCreator.createGhost();
        Square ghostSquare = makeGhostSquare(ghosts, ghost);

        grid[x][y] = ghostSquare;
    }

    void addPlayerStartSquare(Square[][] grid, List<Square> startPositions, int x, int y) {
        Square playerSquare = boardCreator.createGround();

        grid[x][y] = playerSquare;
        startPositions.add(playerSquare);
    }

    void throwInvalidCharacter(int x, int y, char symbol) {
        throw new PacmanConfigurationException(
            "Invalid character at " + x + "," + y + ": " + symbol
        );
    }

    protected Square makeGhostSquare(List<Ghost> ghosts, Ghost ghost) {
        Square ghostSquare = boardCreator.createGround();
        ghosts.add(ghost);
        ghost.occupy(ghostSquare);
        return ghostSquare;
    }

    public Level parseMap(List<String> text) {
        MapText mapText = new MapText(text);
        mapText.validate();

        char[][] map = mapText.toCharGrid();

        return parseMap(map);
    }

    public Level parseMap(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(source, "UTF-8"))) {

            List<String> lines = new ArrayList<>();
            String line = reader.readLine();

            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            return parseMap(lines);
        }
    }

    @SuppressFBWarnings(
        value = {"OBL_UNSATISFIED_OBLIGATION", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
        justification = "try with resources always cleans up / false positive in java 11"
    )
    public Level parseMap(String mapName) throws IOException {
        try (InputStream boardStream = MapParser.class.getResourceAsStream(mapName)) {
            if (boardStream == null) {
                throw new PacmanConfigurationException("Could not get resource for: " + mapName);
            }
            return parseMap(boardStream);
        }
    }

    BoardFactory getBoardCreator() {
        return boardCreator;
    }
}