package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * Encapsulates the life/death status of a player.
 */
public class PlayerStatus {

    boolean alive;

    Unit killer;

    AnimatedSprite deathSprite;

    public PlayerStatus(AnimatedSprite deathAnimation) {
        this.alive = true;
        this.killer = null;
        this.deathSprite = deathAnimation;
        this.deathSprite.setAnimating(false);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean isAlive) {
        if (isAlive) {
            deathSprite.setAnimating(false);
            killer = null;
        } else {
            deathSprite.restart();
        }

        alive = isAlive;
    }

    public Unit getKiller() {
        return killer;
    }

    public void setKiller(Unit newKiller) {
        killer = newKiller;
    }

    public Sprite getDeathSprite() {
        return deathSprite;
    }
}