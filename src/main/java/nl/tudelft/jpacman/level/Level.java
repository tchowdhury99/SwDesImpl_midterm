package nl.tudelft.jpacman.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.Ghost;

/**
 * A level of Pac-Man. A level consists of the board with the players and the
 * AIs on it.
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Level {

    Board board;

    Object moveLock = new Object();

    Object startStopLock = new Object();

    Map<Ghost, ScheduledExecutorService> npcs;

    boolean inProgress;

    List<Square> startSquares;

    int startSquareIndex;

    List<Player> players;

    CollisionMap collisions;

    Set<LevelObserver> observers;

    LevelMovement movement;

    PelletCounter pelletCounter;

    public Level(Board board, List<Ghost> ghosts, List<Square> startPositions,
                 CollisionMap collisionMap) {
        assert board != null;
        assert ghosts != null;
        assert startPositions != null;

        this.board = board;
        this.inProgress = false;
        this.npcs = createNpcMap(ghosts);
        this.startSquares = startPositions;
        this.startSquareIndex = 0;
        this.players = new ArrayList<>();
        this.collisions = collisionMap;
        this.observers = new HashSet<>();
        this.movement = new LevelMovement(collisionMap);
        this.pelletCounter = new PelletCounter(board);
    }

    Map<Ghost, ScheduledExecutorService> createNpcMap(List<Ghost> ghosts) {
        Map<Ghost, ScheduledExecutorService> result = new HashMap<>();

        for (Ghost ghost : ghosts) {
            result.put(ghost, null);
        }

        return result;
    }

    public void addObserver(LevelObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LevelObserver observer) {
        observers.remove(observer);
    }

    public void registerPlayer(Player player) {
        assert player != null;
        assert !startSquares.isEmpty();

        if (players.contains(player)) {
            return;
        }

        players.add(player);
        placePlayerOnNextStartSquare(player);
    }

    void placePlayerOnNextStartSquare(Player player) {
        Square square = currentStartSquare();
        player.occupy(square);
        advanceStartSquare();
    }

    Square currentStartSquare() {
        return startSquares.get(startSquareIndex);
    }

    void advanceStartSquare() {
        startSquareIndex++;
        startSquareIndex %= startSquares.size();
    }

    public Board getBoard() {
        return board;
    }

    public void move(nl.tudelft.jpacman.board.Unit unit, Direction direction) {
        assert unit != null;
        assert direction != null;
        assert unit.hasSquare();

        if (!isInProgress()) {
            return;
        }

        synchronized (moveLock) {
            movement.move(unit, direction);
            updateObservers();
        }
    }

    public void start() {
        synchronized (startStopLock) {
            if (isInProgress()) {
                return;
            }

            startNpcSchedulers();
            inProgress = true;
            updateObservers();
        }
    }

    public void stop() {
        synchronized (startStopLock) {
            if (!isInProgress()) {
                return;
            }

            stopNpcSchedulers();
            inProgress = false;
        }
    }

    void startNpcSchedulers() {
        for (Ghost npc : npcs.keySet()) {
            ScheduledExecutorService service = createScheduler();
            scheduleNpcMove(service, npc);
            npcs.put(npc, service);
        }
    }

    ScheduledExecutorService createScheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    void scheduleNpcMove(ScheduledExecutorService service, Ghost npc) {
        long delay = initialNpcDelay(npc);
        service.schedule(new NpcMoveTask(service, npc), delay, TimeUnit.MILLISECONDS);
    }

    long initialNpcDelay(Ghost npc) {
        return npc.getInterval() / 2;
    }

    void stopNpcSchedulers() {
        for (Entry<Ghost, ScheduledExecutorService> entry : npcs.entrySet()) {
            ScheduledExecutorService schedule = entry.getValue();
            assert schedule != null;
            schedule.shutdownNow();
        }
    }

    public boolean isInProgress() {
        return inProgress;
    }

    void updateObservers() {
        notifyLossIfNeeded();
        notifyWinIfNeeded();
    }

    void notifyLossIfNeeded() {
        if (!isAnyPlayerAlive()) {
            notifyLevelLost();
        }
    }

    void notifyWinIfNeeded() {
        if (remainingPellets() == 0) {
            notifyLevelWon();
        }
    }

    void notifyLevelLost() {
        for (LevelObserver observer : observers) {
            observer.levelLost();
        }
    }

    void notifyLevelWon() {
        for (LevelObserver observer : observers) {
            observer.levelWon();
        }
    }

    public boolean isAnyPlayerAlive() {
        for (Player player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public int remainingPellets() {
        return pelletCounter.countRemaining();
    }

    class NpcMoveTask implements Runnable {

        ScheduledExecutorService service;

        Ghost npc;

        NpcMoveTask(ScheduledExecutorService service, Ghost npc) {
            this.service = service;
            this.npc = npc;
        }

        @Override
        public void run() {
            Direction nextMove = npc.nextMove();

            if (nextMove != null) {
                move(npc, nextMove);
            }

            reschedule();
        }

        void reschedule() {
            long interval = npc.getInterval();
            service.schedule(this, interval, TimeUnit.MILLISECONDS);
        }
    }

    public interface LevelObserver {

        void levelWon();

        void levelLost();
    }
}