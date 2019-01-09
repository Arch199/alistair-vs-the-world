package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

abstract class Movable extends Sprite {
    /**
     * Object that is automatically "movable" i.e. it has a set speed.
     */
    
    private Vector2f v;
    
    Movable(float startx, float starty, Vector2f vec, Image im) {
        super(startx, starty, im);
        v = vec;
    }
    
    /** Move according to current speed. */
    void advance() {
        move(v.x, v.y);
    }
    
    float getXSpeed() { return v.x; }
    float getYSpeed() { return v.y; }
}
