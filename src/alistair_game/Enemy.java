package alistair_game;

import java.util.Iterator;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * Sprite that moves down the path and does damage to Alistair.
 */
class Enemy extends Movable {
    private String name;
    private int health;
    private float speed;

    /**
     * Create an enemy
     * @param startx x-position of start
     * @param starty y-position of start
     * @param v Initial movement vector  (unscaled)
     * @param name Enemy type, e.g. Python
     */
    Enemy(float startx, float starty, Vector2f v, String name) {
        super(startx, starty, v, null, 0);
        this.name = name;
        String imPath = "assets\\sprites\\enemies\\";
        switch (name) {
            // In order of increasing strength
            case "python":
                imPath += "python-icon.png";
                health = 1;
                speed = 1.5f;
                break;
            case "int_max":
                imPath += "int_max.png";
                health = 1;
                speed = 2f;
                break;
            case "do":
                imPath += "do.png";
                health = 1;
                speed  = 3.0f;
                break;
            case "commerce":
                imPath += "fbe1.png";
                health = 2;
                speed = 2f;
                break;
            case "overflow":
                imPath += "stackoverflow32.png";
                health = 3;
                speed = 2.5f;
                break;
            case "thomas":
                imPath += "thomas100.png";
                health = 2;
                speed = 5f;
                break;
            default:
                throw new IllegalArgumentException("No such enemy '" + name + "'");
        }
        try {
            setImage(new Image(imPath));
        } catch (SlickException e) {
            e.printStackTrace();
        }

        setV(v.scale(speed));
        setDamage(health);
    }
    
    /** Moves enemy along the precalculated path.
     * @param speed Magnitude of step
     * @param world Game's world instance
     * */
    void advance(float speed, World world) {
        int nextx = world.toGrid(getX() + world.getTileSize() / 2 * Math.signum(getV().x));
        int nexty = world.toGrid(getY() + world.getTileSize() / 2 * Math.signum(getV().y));
        // If we're about to hit a wall, change direction
        if (!world.inGridBounds(nextx, nexty) || world.getTile(nextx, nexty).isWall()) {
            int gridx = world.toGrid(getX()), gridy = world.toGrid(getY());
            if (world.inGridBounds(gridx, gridy)) {
                setV(speed * world.getPathXDir(gridx, gridy), speed * world.getPathYDir(gridx, gridy));
            } else {
                setV(speed * world.defaultDir(gridx), speed * world.defaultDir(gridy));
            }
        }
        super.advance();
    }

    /**
     * Make an enemy take damage
     * @param damage Amount to be deducted from health
     * @param itr The enemy to deduct from, as an iterator. (Use .iterator() on a list of enemies to convert)
     */
    void takeDamage(int damage, Iterator<Enemy> itr) {
        health -= damage;
        setDamage(health);
        if (health <= 0) {
            itr.remove();
        }
    }

    float getSpeed() {
        return this.speed;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }
    
    String getName() { return name; }
}
