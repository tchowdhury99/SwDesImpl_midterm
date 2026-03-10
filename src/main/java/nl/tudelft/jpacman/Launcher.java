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

@SuppressWarnings("PMD.TooManyMethods")
public class Launcher {

    static PacManSprites spriteStore = new PacManSprites();

    public static String defaultMap = "/board.txt";

    String levelMap = defaultMap;

    PacManUI pacManUI;

    Game game;

    public Game getGame() {
        return game;
    }

    String getLevelMap() {
        return levelMap;
    }

    public Launcher withMapFile(String fileName) {
        levelMap = fileName;
        return this;
    }

    public Game makeGame() {
        GameFactory gameFactory = getGameFactory();
        Level level = makeLevel();
        PointCalculator calculator = loadPointCalculator();

        game = gameFactory.createSinglePlayerGame(level, calculator);
        return game;
    }

    PointCalculator loadPointCalculator() {
        PointCalculatorLoader loader = new PointCalculatorLoader();
        return loader.load();
    }

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

    MapParser getMapParser() {
        LevelFactory levelFactory = getLevelFactory();
        BoardFactory boardFactory = getBoardFactory();
        return new MapParser(levelFactory, boardFactory);
    }

    BoardFactory getBoardFactory() {
        PacManSprites sprites = getSpriteStore();
        return new BoardFactory(sprites);
    }

    PacManSprites getSpriteStore() {
        return spriteStore;
    }

    LevelFactory getLevelFactory() {
        PacManSprites sprites = getSpriteStore();
        GhostFactory ghostFactory = getGhostFactory();
        PointCalculator calculator = loadPointCalculator();

        return new LevelFactory(sprites, ghostFactory, calculator);
    }

    GhostFactory getGhostFactory() {
        PacManSprites sprites = getSpriteStore();
        return new GhostFactory(sprites);
    }

    GameFactory getGameFactory() {
        PlayerFactory playerFactory = getPlayerFactory();
        return new GameFactory(playerFactory);
    }

    PlayerFactory getPlayerFactory() {
        PacManSprites sprites = getSpriteStore();
        return new PlayerFactory(sprites);
    }

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
        Player player = singlePlayerOf(currentGame);
        movePlayer(currentGame, player, direction);
    }

    void movePlayer(Game currentGame, Player player, Direction direction) {
        currentGame.move(player, direction);
    }

    Game requireGame() {
        assert game != null;
        return game;
    }

    Player singlePlayerOf(Game currentGame) {
        List<Player> players = playersOf(currentGame);

        if (players.isEmpty()) {
            throw new IllegalArgumentException("Game has 0 players.");
        }

        return firstPlayer(players);
    }

    List<Player> playersOf(Game currentGame) {
        return currentGame.getPlayers();
    }

    Player firstPlayer(List<Player> players) {
        return players.get(0);
    }

    PacManUiBuilder createConfiguredBuilder() {
        PacManUiBuilder builder = createBaseBuilder();
        addSinglePlayerKeys(builder);
        return builder;
    }

    PacManUiBuilder createBaseBuilder() {
        PacManUiBuilder builder = new PacManUiBuilder();
        builder.withDefaultButtons();
        return builder;
    }

    public void launch() {
        makeGame();

        PacManUiBuilder builder = createConfiguredBuilder();
        Game runningGame = requireGame();

        pacManUI = builder.build(runningGame);
        pacManUI.start();
    }

    public void dispose() {
        assert pacManUI != null;
        pacManUI.dispose();
    }

    public static void main(String[] args) throws IOException {
        Launcher launcher = new Launcher();
        launcher.launch();
    }
}