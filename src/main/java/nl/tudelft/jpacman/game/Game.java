package nl.tudelft.jpacman.game;

import java.util.List;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.points.PointCalculator;

/**
 * A basic implementation of a Pac-Man game.
 *
 * @author Jeroen Roosen
 */
public abstract class Game implements LevelObserver {

    private boolean inProgress;

    private final Object progressLock = new Object();

    private PointCalculator pointCalculator;

    protected Game(PointCalculator pointCalculator) {
        this.pointCalculator = pointCalculator;
        inProgress = false;
    }

    public void start() {
        synchronized (progressLock) {
            if (isInProgress()) {
                return;
            }

            if (canStartGame()) {
                startGameSession();
            }
        }
    }

    public void stop() {
        synchronized (progressLock) {
            if (!isInProgress()) {
                return;
            }

            stopGameSession();
        }
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public abstract List<Player> getPlayers();

    public abstract Level getLevel();

    public void move(Player player, Direction direction) {
        if (!isInProgress()) {
            return;
        }

        movePlayerOnLevel(player, direction);
        registerMovePoints(player, direction);
    }

    boolean canStartGame() {
        Level level = currentLevel();
        return level.isAnyPlayerAlive() && level.remainingPellets() > 0;
    }

    void startGameSession() {
        inProgress = true;

        Level level = currentLevel();
        level.addObserver(this);
        level.start();
    }

    void stopGameSession() {
        inProgress = false;
        currentLevel().stop();
    }

    void movePlayerOnLevel(Player player, Direction direction) {
        currentLevel().move(player, direction);
    }

    void registerMovePoints(Player player, Direction direction) {
        pointCalculator.pacmanMoved(player, direction);
    }

    Level currentLevel() {
        return getLevel();
    }

    @Override
    public void levelWon() {
        stop();
    }

    @Override
    public void levelLost() {
        stop();
    }
}