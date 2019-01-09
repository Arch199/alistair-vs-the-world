package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

abstract class Enemy extends Movable {
    private int damage = 5;
    private Vector2f v;

    Enemy(float startx, float starty, Vector2f vec, Image im) {
        super(startx, starty, vec, im);
    }
    
    /** Moves along the precalculated path. */
    void advance(float speed, World world) {
        int[][][] path = world.getPath();

        // If we're about to hit a wall, change direction
        int nextx = world.toGrid(getX() + world.getTileSize() / 2 * Math.signum(v.x));
        int nexty = world.toGrid(getY() + world.getTileSize() / 2 * Math.signum(v.y));
        if (!world.inGridBounds(nextx, nexty) || world.getTiles()[nextx][nexty].isWall()) {
            int gridx = world.toGrid(getX()), gridy = world.toGrid(getY());
            if (world.inGridBounds(gridx, gridy)) {
                v.x = speed * path[gridx][gridy][0];
                v.y = speed * path[gridx][gridy][1];
            } else {
                v.x = speed * world.defaultDir(gridx);
                v.y = speed * world.defaultDir(gridy);
            }
        }
        move(v.x, v.y);
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

    int getDamage() { return damage; }
}
