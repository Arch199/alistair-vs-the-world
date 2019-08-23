package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/** Sprite with a set velocity, damage, and health. */
public abstract class DynamicSprite extends Sprite {
    private Vector2f v;
    private int damage, health;

    /** Create a sprite that can move.
     * @param x Starting x-coordinate.
     * @param y Starting y-coordinate.
     * @param v Initial velocity.
     * @param im Sprite image.
     * @param damage Amount of damage to deal on contact.
     * @param health Health total.
     */
    public DynamicSprite(float x, float y, Vector2f v, Image im, int damage, int health) {
        super(x, y, im);
        this.v = v;
        this.damage = damage;
        this.health = health;
    }

    /** Move according to current velocity. */
    @Override
    public void update(int delta) {
        if (health < 0) {
            kill();
        } else {
            move(v.x * delta, v.y * delta);
        }
    }

    /** Deal damage to a sprite.
     * @param damage Amount to be deducted from health.
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            kill();
        }
    }

    public Vector2f getV() { return v; }
    public int getDamage() { return damage; }

    public void setV(Vector2f v) { this.v = v; }
    protected void setDamage(int d) {
        if (d < 0) {
            throw new IllegalArgumentException("Damage must be >= 0");
        }
        damage = d;
    }
}
