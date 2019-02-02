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
    enum Type {
        // TODO: implement and enable all of these
        BUBBLE("Bubble Sort Alistair", "bubble.png", 3000)/*,
        SELECTION("Selection Sort Alistair", null, 0),
        INSERTION("Insertion Sort Alistair", null, 0),
        QUICK("Quicksort Alistair", null, 0),
        HEAP("Heapsort Alistair", null, 0),
        MERGE("Mergesort Alistair", null, 0)*/;
        final String title, imPath;
        final int fireRate;
        Type(String title, String imPath, int fireRate) {
            this.title = title;
            this.imPath = imPath;
            this.fireRate = fireRate;
        }
    }
    static final String SPRITE_PATH = "assets\\sprites\\towers\\";
    
    private Type type;
    private boolean placed = false;
    private float range = 150f; // Range is radius from center
    private int fireRate = 0; // In ms
    private long nextShot = 0L; // Time until next fire (in ms)
    private float projSpeed = 10f;

    /**
     * Create a tower.
     * @param startx x-position
     * @param starty y-position
     * @param type Enum for the tower's type
     */
    Tower(float startx, float starty, Tower.Type type) {
        super(startx, starty, null);
        this.type = type;
        fireRate = type.fireRate; // Could actually remove this and determine everything from type
        try {
            setImage(new Image(SPRITE_PATH + type.imPath));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /** Makes the shot. Generates a projectile and sets a new time. */
    void shoot(World world) {
        // Target the next enemy in range
        Vector2f vec = targetNext(world.getEnemies());
        if (vec == null) {
            // Instead of firing, just wait and try again next tick
            return;
        }
        
        // Create projectile
        world.newProjectile(getX(), getY(), vec, Projectile.Type.valueOf(type.name()));

        // Reset the timer for the next shot
        nextShot = fireRate;
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

        vec.add(target.getV());
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
        // This could be split into two functions rather than being a check with side effects
        // Could also just move this into the shoot() method directly (might be best)
        nextShot -= delta;
        return nextShot <= 0;
    }

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

    Type getType() { return type; }
    boolean isPlaced() { return placed; }
}
