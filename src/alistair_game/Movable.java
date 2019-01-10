package alistair_game;

import java.util.Iterator;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

abstract class Movable extends Sprite {
    /**
     * Object that is automatically "movable" i.e. it has a set speed.
     */
    
    private Vector2f v;
    private int damage;
    
    Movable(float startx, float starty, Vector2f vec, Image im, int damage) {
        super(startx, starty, im);
        v = vec;
        this.damage = damage;
    }
    
    /** Move according to current speed. */
    void advance() {
        move(v.x, v.y);
    }
    
    Vector2f getV() { return v; }
    int getDamage() { return damage; }
    
    void setV(float x, float y) { v.x = x; v.y = y; }
    void setDamage(int d) {
        if (d < 0) {
            throw new IllegalArgumentException("Damage must be >= 0");
        }
        damage = d;
    }
}
