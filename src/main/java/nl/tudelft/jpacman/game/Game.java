package nl.tudelft.jpacman.game;

import java.util.List;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.points.PointCalculator;

/**
 * A basic implementation of a Pac-Man game.
 */
public abstract class Game implements LevelObserver {

    boolean inProgress;

    Object progressLock = new Object();

    PointCalculator pointCalculator;

    GameSession session;

    protected Game(PointCalculator pointCalculator) {
        this.pointCalculator = pointCalculator;
        this.inProgress = false;
        this.session = new GameSession(this);
    }

    public void start() {
        synchronized (progressLock) {
            if (isInProgress()) {
                return;
            }

            session.startSession();
        }
    }

    public void stop() {
        synchronized (progressLock) {
            if (!isInProgress()) {
                return;
            }

            session.stopSession();
        }
    }

    public boolean isInProgress() {
        return inProgress;
    }

    void setInProgress(boolean inProgressState) {
        inProgress = inProgressState;
    }

    public abstract List<Player> getPlayers();

    public abstract Level getLevel();

    public void move(Player player, Direction direction) {
        if (!isInProgress()) {
            return;
        }

        movePlayer(player, direction);
        registerMove(player, direction);
    }

    void movePlayer(Player player, Direction direction) {
        Level level = currentLevel();
        level.move(player, direction);
    }

    void registerMove(Player player, Direction direction) {
        pointCalculator.pacmanMoved(player, direction);
    }

    Level currentLevel() {
        return getLevel();
    }

    boolean canStartGame() {
        return session.canStartSession();
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