package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.Level;

/**
 * Handles the session lifecycle for a game.
 */
public class GameSession {

    Game game;

    public GameSession(Game game) {
        this.game = game;
    }

    public boolean canStartSession() {
        Level level = game.currentLevel();
        return level.isAnyPlayerAlive() && level.remainingPellets() > 0;
    }

    public void startSession() {
        if (!canStartSession()) {
            return;
        }

        game.setInProgress(true);

        Level level = game.currentLevel();
        level.addObserver(game);
        level.start();
    }

    public void stopSession() {
        game.setInProgress(false);

        Level level = game.currentLevel();
        level.stop();
    }
}