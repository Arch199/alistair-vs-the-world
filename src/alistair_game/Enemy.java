package alistair_game;

import org.newdawn.slick.Image;

abstract class Enemy extends Sprite {
    // private double dir; // Direction: radians anti-clockwise from east TODO:
    // remove
    private int damage = 5;
    float hsp = 0, vsp = 0;

    Enemy(float startx, float starty, float hsp, float vsp, Image im) {
        super(startx, starty, im);
        // this.dir = dir;
        this.hsp = hsp;
        this.vsp = vsp;
    }

    void advance(float speed, World world) {
        int[][][] path = world.getPath();

        // Follow the pre-calculated path
        // If we're about to hit a wall, change direction
        int nextx = world.toGrid(getX() + world.getTileSize() / 2 * Math.signum(hsp));
        int nexty = world.toGrid(getY() + world.getTileSize() / 2 * Math.signum(vsp));
        if (!world.inGridBounds(nextx, nexty) || world.getTiles()[nextx][nexty].isWall()) {
            int gridx = world.toGrid(getX()), gridy = world.toGrid(getY());
            if (world.inGridBounds(gridx, gridy)) {
                hsp = speed * path[gridx][gridy][0];
                vsp = speed * path[gridx][gridy][1];
            } else {
                hsp = speed * world.defaultDir(gridx);
                vsp = speed * world.defaultDir(gridy);
            }
        }
        move(hsp, vsp);
    }

    // Note: this is no longer in use but may be helpful later
    boolean touchingWall(float xmove, float ymove, World world) {
        boolean result = false;
        move(xmove, ymove);
        for (Tile[] col : world.getTiles()) {
            for (Tile t : col) {
                if (t.isWall() && checkCollision(t)) {
                    result = true;
                    break;
                }
            }
        }
        move(-xmove, -ymove);
        return result;
    }

    int getDamage() {
        return damage;
    }
}
