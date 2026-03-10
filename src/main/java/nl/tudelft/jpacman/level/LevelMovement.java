package nl.tudelft.jpacman.level;

import java.util.List;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;

/**
 * Handles movement and collision processing inside a level.
 */
public class LevelMovement {

    CollisionMap collisions;

    public LevelMovement(CollisionMap collisionMap) {
        this.collisions = collisionMap;
    }

    public void move(Unit unit, Direction direction) {
        prepareMove(unit, direction);

        Square destination = destinationOf(unit);

        if (!destinationAllows(unit, destination)) {
            return;
        }

        List<Unit> occupants = occupantsAt(destination);
        relocateUnit(unit, destination);
        processCollisions(unit, occupants);
    }

    void prepareMove(Unit unit, Direction direction) {
        unit.setDirection(direction);
    }

    Square destinationOf(Unit unit) {
        Square current = unit.getSquare();
        Direction currentDirection = unit.getDirection();
        return current.getSquareAt(currentDirection);
    }

    boolean destinationAllows(Unit unit, Square destination) {
        return destination.isAccessibleTo(unit);
    }

    List<Unit> occupantsAt(Square destination) {
        return destination.getOccupants();
    }

    void relocateUnit(Unit unit, Square destination) {
        unit.occupy(destination);
    }

    void processCollisions(Unit mover, List<Unit> occupants) {
        for (Unit occupant : occupants) {
            collisions.collide(mover, occupant);
        }
    }
}