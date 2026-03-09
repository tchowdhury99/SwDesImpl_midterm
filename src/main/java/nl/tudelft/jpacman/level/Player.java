package nl.tudelft.jpacman.level;

import java.util.Map;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A player operated unit in our game.
 *
 * @author Jeroen Roosen
 */
public class Player extends Unit {

    Map<Direction, Sprite> sprites;

    PlayerLife life;

    PlayerScore score;

    protected Player(Map<Direction, Sprite> spriteMap, AnimatedSprite deathAnimation) {
        this.sprites = spriteMap;
        this.life = new PlayerLife(deathAnimation);
        this.score = new PlayerScore();
    }

    public boolean isAlive() {
        return life.isAlive();
    }

    public Unit getKiller() {
        return life.getKiller();
    }

    public int getScore() {
        return score.value();
    }

    public void addPoints(int points) {
        score.add(points);
    }

    public void collidedWith(Unit attacker) {
        life.killBy(attacker);
    }

    public void revive() {
        life.revive();
    }

    @Override
    public Sprite getSprite() {
        if (life.isAlive()) {
            return sprites.get(getDirection());
        }
        return life.deathSprite();
    }

    /**
     * Kept for compatibility with the rest of the codebase.
     */
    public void setAlive(boolean isAlive) {
        if (isAlive) {
            revive();
            return;
        }
        life.killBy(life.getKiller());
    }

    /**
     * Kept for compatibility with the rest of the codebase.
     */
    public void setKiller(Unit killer) {
        life.setKiller(killer);
    }
}