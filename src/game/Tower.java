package game;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/**
 * Towers are placed on a grid and shoot projectiles at enemies.
 */
public class Tower extends Sprite {
    public enum Type {
        // TODO: implement and enable all of these
        BUBBLE("Bubble Sort Alistair", "bubble.png", 4000, 150f),
        SELECTION("Selection Sort Alistair", "selection.png", 3000, 100f)/*,
        INSERTION("Insertion Sort Alistair", null, 0, 0f),
        QUICK("Quicksort Alistair", null, 0, 0f),
        HEAP("Heapsort Alistair", null, 0, 0f),
        MERGE("Mergesort Alistair", null, 0, 0f)*/;
        public final String title, imPath;
        public final int fireRate;
        public final float range;
        public final Projectile.Type proj;
        Type(String title, String imPath, int fireRate, float range) {
            this.title = title;
            this.imPath = imPath;
            this.fireRate = fireRate;
            this.range = range;
            proj = Projectile.Type.valueOf(name());
        }
        public static Type fromTitle(String title) {
            for (Type t : values()) {
                if (t.title.equals(title)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("No tower type for title '" + title + "'");
        }
    }
    public static final String SPRITE_PATH = "assets/sprites/towers/";
    
    private Type type;
    private boolean placed = false;
    private float range;
    private int fireRate; // In ms
    private long nextShot = 0L; // Time until next fire (in ms)

    /**
     * Create a tower.
     * @param x x-position
     * @param y y-position
     * @param type Enum for the tower's type
     */
    public Tower(float x, float y, Tower.Type type) {
        super(x, y, null);
        this.type = type;
        fireRate = type.fireRate; // Could actually remove this and determine everything from type
        range = type.range;
        try {
            setImage(new Image(SPRITE_PATH + type.imPath));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /** Makes the shot. Generates a projectile and sets a new time. */
    public void shoot(World world) {
        // Target the next enemy in range
        Vector2f vec = targetNext(world.getEnemies());
        if (vec == null) {
            // Instead of firing, just wait and try again next tick
            return;
        }
        
        // Create projectile
        world.newProjectile(getX(), getY(), vec, type.proj);

        // Reset the timer for the next shot
        nextShot = fireRate;
    }

    /** Returns a velocity vector aimed at the first enemy in range. */
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
        vec.normalise().scale(type.proj.speed);

        // Assume it keeps moving in a straight line
        vec.add(target.getV());

        return vec;
    }
    
    /** Places the tower. */
    public void place(float x, float y) {
        teleport(x, y);
        placed = true;
    }
    
    /** Counts down the shot timer.
     * Returns true if enough time has passed to shoot.
     */
    public boolean countDown(int delta) {
        // This could be split into two functions rather than being a check with side effects
        // Could also just move this into the shoot() method directly (might be best)
        nextShot -= delta;
        return nextShot <= 0;
    }

    /** Draws a range circle around towers. */
    public void drawRange(Graphics g) {
        // Top-left corner of the circle
        float xCorner = getX() - range, yCorner = getY() - range;

        // Draw circumference
        g.setColor(new Color(110, 110, 110, 110));
        g.drawOval(xCorner, yCorner, range * 2, range * 2);
        
        // Fill with a shade of grey (can change vals depending on contrast w/ textures)
        g.setColor(new Color(80, 80, 80, 80));
        g.fillOval(xCorner, yCorner, range * 2, range * 2);
    }

    public void waveReset() {
        nextShot = 0;
    }

    public Type getType() { return type; }
    public boolean isPlaced() { return placed; }
}
