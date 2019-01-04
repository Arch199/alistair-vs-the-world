package alistair_game;

import org.newdawn.slick.Image;

public class Projectile extends Sprite {
    private float hsp = 0, vsp = 0;

    Projectile(float startx, float starty, float hsp, float vsp, Image im) {
        super(startx, starty, im);
        this.hsp = hsp;
        this.vsp = vsp;
    }

    /** Move according to current speed */
    void advance() {
        move(hsp, vsp);
    }

    float getXSpeed() { return this.hsp; }
    float getYSpeed() { return this.vsp; }
}
