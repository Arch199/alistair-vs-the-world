package alistair_game;

import org.newdawn.slick.Image;

public class Projectile extends Sprite {
    private Vector v;

    Projectile(float startx, float starty, Vector vec, Image im) {
        super(startx, starty, im);
        v = vec;
    }

    /** Move according to current speed */
    void advance() {
        move(v.hsp, v.vsp);
    }

    float getXSpeed() {
        return v.hsp;
    }

    float getYSpeed() {
        return v.vsp;
    }
}
