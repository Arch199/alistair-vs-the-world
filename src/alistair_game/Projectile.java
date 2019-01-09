package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Projectile extends Sprite {
    private Vector2f v;

    Projectile(float startx, float starty, Vector2f vec, Image im) {
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
