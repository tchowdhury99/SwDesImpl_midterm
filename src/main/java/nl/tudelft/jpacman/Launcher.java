package nl.tudelft.jpacman;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.points.PointCalculator;
import nl.tudelft.jpacman.points.PointCalculatorLoader;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.ui.Action;
import nl.tudelft.jpacman.ui.PacManUI;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

/**
 * Creates and launches the JPacMan UI.
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Launcher {

    static PacManSprites SPRITE_STORE = new PacManSprites();

    public static String DEFAULT_MAP = "/board.txt";

    String levelMap = DEFAULT_MAP;

    PacManUI pacManUI;

    Game game;

    /**
     * Creates a launcher instance.
     */
    public Launcher() {
        super();
    }

    /**
     * @return The game object this launcher will start when {@link #launch()}
     *         is called.
     */
    public Game getGame() {
        return game;
    }

    /**
     * The map file used to populate the level.
     *
     * @return The name of the map file.
     */
    String getLevelMap() {
        return levelMap;
    }

    /**
     * Set the name of the file containing this level's map.
     *
     * @param fileName
     *            Map to be used.
     * @return Level corresponding to the given map.
     */
    public Launcher withMapFile(String fileName) {
        levelMap = fileName;
        return this;
    }

    /**
     * Creates a new game using the level from {@link #makeLevel()}.
     *
     * @return a new Game.
     */
    public Game makeGame() {
        GameFactory gameFactory = getGameFactory();
        Level level = makeLevel();
        game = gameFactory.createSinglePlayerGame(level, loadPointCalculator());
        return game;
    }

    PointCalculator loadPointCalculator() {
        return new PointCalculatorLoader().load();
    }

    /**
     * Creates a new level. By default this method will use the map parser to
     * parse the default board stored in the <code>board.txt</code> resource.
     *
     * @return A new level.
     */
    public Level makeLevel() {
        MapParser parser = getMapParser();
        String mapFile = getLevelMap();

        try {
            return parser.parseMap(mapFile);
        } catch (IOException exception) {
            throw new PacmanConfigurationException(
                "Unable to create level, name = " + mapFile, exception
            );
        }
    }

    /**
     * @return A new map parser object using the factories from
     *         {@link #getLevelFactory()} and {@link #getBoardFactory()}.
     */
    MapParser getMapParser() {
        return new MapParser(getLevelFactory(), getBoardFactory());
    }

    /**
     * @return A new board factory using the sprite store from
     *         {@link #getSpriteStore()}.
     */
    BoardFactory getBoardFactory() {
        return new BoardFactory(getSpriteStore());
    }

    /**
     * @return The default {@link PacManSprites}.
     */
    PacManSprites getSpriteStore() {
        return SPRITE_STORE;
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}
     *         and the ghosts from {@link #getGhostFactory()}.
     */
    LevelFactory getLevelFactory() {
        return new LevelFactory(getSpriteStore(), getGhostFactory(), loadPointCalculator());
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}.
     */
    GhostFactory getGhostFactory() {
        return new GhostFactory(getSpriteStore());
    }

    /**
     * @return A new factory using the players from {@link #getPlayerFactory()}.
     */
    GameFactory getGameFactory() {
        return new GameFactory(getPlayerFactory());
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}.
     */
    PlayerFactory getPlayerFactory() {
        return new PlayerFactory(getSpriteStore());
    }

    /**
     * Adds key events UP, DOWN, LEFT and RIGHT to a game.
     *
     * @param builder
     *            The {@link PacManUiBuilder} that will provide the UI.
     */
    void addSinglePlayerKeys(PacManUiBuilder builder) {
        builder.addKey(KeyEvent.VK_UP, moveTowardsDirection(Direction.NORTH));
        builder.addKey(KeyEvent.VK_DOWN, moveTowardsDirection(Direction.SOUTH));
        builder.addKey(KeyEvent.VK_LEFT, moveTowardsDirection(Direction.WEST));
        builder.addKey(KeyEvent.VK_RIGHT, moveTowardsDirection(Direction.EAST));
    }

    Action moveTowardsDirection(Direction direction) {
        return () -> moveSinglePlayer(direction);
    }

    void moveSinglePlayer(Direction direction) {
        Game currentGame = requireGame();
        Player player = getSinglePlayer(currentGame);
        currentGame.move(player, direction);
    }

    Game requireGame() {
        assert game != null;
        return game;
    }

    Player getSinglePlayer(Game currentGame) {
        List<Player> players = currentGame.getPlayers();
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Game has 0 players.");
        }
        return players.get(0);
    }

    PacManUiBuilder createConfiguredBuilder() {
        PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        addSinglePlayerKeys(builder);
        return builder;
    }

    /**
     * Creates and starts a JPac-Man game.
     */
    public void launch() {
        makeGame();
        PacManUiBuilder builder = createConfiguredBuilder();
        pacManUI = builder.build(requireGame());
        pacManUI.start();
    }

    /**
     * Disposes of the UI. For more information see
     * {@link javax.swing.JFrame#dispose()}.
     *
     * Precondition: The game was launched first.
     */
    public void dispose() {
        assert pacManUI != null;
        pacManUI.dispose();
    }

    /**
     * Main execution method for the Launcher.
     *
     * @param args
     *            The command line arguments - which are ignored.
     * @throws IOException
     *             When a resource could not be read.
     */
    public static void main(String[] args) throws IOException {
        new Launcher().launch();
    }
}