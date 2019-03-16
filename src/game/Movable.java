package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/**
 * Object that is automatically "movable" i.e. it has a set speed.
 */
public abstract class Movable extends Sprite {
    private Vector2f v;
    private int damage;

    /**
     * Create a sprite that can move.
     * @param startx start x-coord
     * @param starty start y-coord
     * @param v initial velocity
     * @param im sprite image file
     * @param damage arbritrary int counter always >= 0
     */
    public Movable(float startx, float starty, Vector2f v, Image im, int damage) {
        super(startx, starty, im);
        this.v = v;
        this.damage = damage;
    }
    
    /** Move according to current velocity. */
    public void advance() {
        move(v.x, v.y);
    }
    
    public Vector2f getV() { return v; }
    public int getDamage() { return damage; }

    public void setV(Vector2f v) { this.v = v; }
    public void setV(float x, float y) { v.x = x; v.y = y; }
    public void setDamage(int d) {
        if (d < 0) {
            throw new IllegalArgumentException("Damage must be >= 0");
        }
        damage = d;
    }
}
