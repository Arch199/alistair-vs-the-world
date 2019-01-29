package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.Graphics;

import java.util.List;

import org.newdawn.slick.Color;

/**
 * Towers are placed on a grid and shoot projectiles at enemies.
 */
class Tower extends Sprite {   
    private boolean placed = false;
    private float range = 150f; // Range is radius from center
    private int fireRate = 0; // In ms
    private long nextShot; // Time until next fire (in ms)
    private float projSpeed = 4f;

    /**
     * Create a tower
     * @param startx x-position
     * @param starty y-position
     * @param im Sprite image
     * @param fireRate Time between shots (ms)
     */
    Tower(float startx, float starty, Image im, int fireRate) {
        super(startx, starty, im);
        this.fireRate = fireRate;
        nextShot = 0;
    }

    /** Makes the shot. Generates a projectile and sets a new time. */
    void shoot(World world) {
        try {
            // Target the next enemy in range
            Vector2f vec = targetNext(world.getEnemies());
            if (vec == null) {
                // Instead of firing, just wait and try again next tick
                return;
            }
            
            // Create projectile
            Image im = new Image("assets\\sprites\\defaultproj.png"); // TODO: move this reference elsewhere
            world.newProjectile(getX(), getY(), vec, im);

            // Reset the timer for the next shot
            nextShot = fireRate;
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /** Returns a velocity vector to hit the first enemy in range. */
    private Vector2f targetNext(List<Enemy> enemies) {
        // Target the first enemy in range
        Enemy target = null;
        for (Enemy e : enemies) {
            if (distanceTo(e) <= range) {
                target = e;
                break;
            }
        }
        // If there is none, return null and handle above
        if (target == null) {
            return null;
        }
        
        Vector2f vec = new Vector2f(target.getX()-getX(), target.getY()-getY());
        vec.normalise().scale(projSpeed);
        
        // Assume it keeps moving in a straight line
        vec.add(target.getV());
        
        return vec;
    }
    
    /** Places the tower. */
    void place(float x, float y) {
        teleport(x, y);
        placed = true;
    }
    
    /** Counts down the shot timer.
     * Returns true if enough time has passed to shoot. */
    boolean countDown(int delta) {
        nextShot -= delta;
        return nextShot <= 0;
    }
    // ^ This could be split into two functions rather than
    // being a check with side effects

    /** Draws a range circle around towers. */
    void drawRange(Graphics g) {
        // Top-left corner of the circle
        float xcorner = getX() - range, ycorner = getY() - range;

        // Draw circumference
        g.setColor(new Color(110, 110, 110, 110));
        g.drawOval(xcorner, ycorner, range * 2, range * 2);
        // Fill with a shade of grey (can change vals depending on contrast w/ textures)
        g.setColor(new Color(80, 80, 80, 80));
        g.fillOval(xcorner, ycorner, range * 2, range * 2);
    }

    void waveReset() {
        nextShot = 0;
    }

    boolean isPlaced() { return placed; }
}
