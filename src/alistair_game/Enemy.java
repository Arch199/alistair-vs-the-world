package alistair_game;

import java.util.Iterator;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

class Enemy extends Movable {
    private String name;
    private int health;
    
    Enemy(float startx, float starty, Vector2f v, String name) {
        super(startx, starty, v, null, 0);
        this.name = name;
        String imPath = "assets\\sprites\\enemies\\";
        switch (name) {
            case "python":
                imPath += "python-icon.png";
                setDamage(5);
                break;
            default:
                throw new IllegalArgumentException("No such enemy '" + name + "'");
        }
        try {
            setImage(new Image(imPath));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    /** Moves along the precalculated path. */
    void advance(float speed, World world) {
        // If we're about to hit a wall, change direction
        int nextx = world.toGrid(getX() + world.getTileSize() / 2 * Math.signum(getV().x));
        int nexty = world.toGrid(getY() + world.getTileSize() / 2 * Math.signum(getV().y));
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

    // Note: this is no longer in use but may be helpful later
    boolean touchingWall(float xmove, float ymove, World world) {
        boolean result = false;
        move(xmove, ymove);
        for (int i = 0; i < world.getGridWidth(); i++) {
            for (int j = 0; j < world.getGridHeight(); j++) {
                Tile t = world.getTile(i, j);
                if (t.isWall() && checkCollision(t)) {
                    result = true;
                    break;
                }
            }
        }
        move(-xmove, -ymove);
        return result;
    }
    
    void takeDamage(int damage, Iterator<Enemy> itr) {
        health -= damage;
        if (health <= 0) {
            itr.remove();
        }
    }
    
    String getName() { return name; }
}
