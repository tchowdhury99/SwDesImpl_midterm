package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * Encapsulates the life-cycle state of a player.
 */
public class PlayerLife {

    boolean alive;

    Unit killer;

    AnimatedSprite deathAnimation;

    public PlayerLife(AnimatedSprite deathSprite) {
        this.alive = true;
        this.killer = null;
        this.deathAnimation = deathSprite;
        this.deathAnimation.setAnimating(false);
    }

    public boolean isAlive() {
        return alive;
    }

    public Unit getKiller() {
        return killer;
    }

    public void setKiller(Unit attacker) {
        killer = attacker;
    }

    public void killBy(Unit attacker) {
        killer = attacker;
        alive = false;
        deathAnimation.restart();
    }

    public void revive() {
        killer = null;
        alive = true;
        deathAnimation.setAnimating(false);
    }

    public Sprite deathSprite() {
        return deathAnimation;
    }
}