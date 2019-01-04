package alistair_game;

import org.lwjgl.Sys;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Projectile extends Sprite {
    private float hsp = 0, vsp = 0;

    Projectile(float startx, float starty, float hsp, float vsp, Image im) {
        super(startx, starty, im);
        //this.dir = dir;
        this.hsp = hsp;
        this.vsp = vsp;
    }

    void advance(float xdist, float ydist) {
        move(xdist, ydist);
    }

    float getXSpeed() {
        return this.hsp;
    }

    float getYSpeed() {
        return this.vsp;
    }
}
