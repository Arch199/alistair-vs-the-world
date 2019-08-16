package game;

import java.util.List;

import control.App;
import control.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

/**
 * Towers are placed on a grid and shoot projectiles at enemies.
 */
public abstract class Tower extends Sprite {
    public enum Type {
        SELECTION("Selection Sort Alistair", "selection.png", 2000, 350f, 50),
        BUBBLE("Bubble Sort Alistair", "bubble.png", 1500, 224f, 80) ;
        private final String title, imName;
        private final int fireRate;
        private final float range;
        private final int cost;
        Type(String text, String imName, int fireRate, float range, int cost) {
            this.title = text;
            this.imName = imName;
            this.fireRate = fireRate;
            this.range = range;
            this.cost = cost;
        }
        public String toString() {
            return title;
        }
        public int getCost() { return cost; }
        public Image getImage() {
            return Util.newImage(SPRITE_PATH + imName);
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
    protected static final String SPRITE_PATH = "assets/sprites/towers/";

    private boolean placed = false;
    private float range;
    private int fireRate; // In ms
    private long nextShot = 0L; // Time until next fire (in ms)
    private Type type;

    /**
     * Create a tower.
     * @param x x-position
     * @param y y-position
     * @param type the type of tower to create
     */
    public Tower(float x, float y, Type type) {
        super(x, y, type.getImage());
        this.fireRate = type.fireRate; // could also remove these instance variables and just get from the type
        this.range = type.range;
        this.type = type;
    }

    /** Fires a projectile in the given direction. */
    protected abstract void shoot(Vector2f dir) throws SlickException;
    
    public static Tower create(Type type, float x, float y) {
        switch (type) {
            case BUBBLE:
                return new BubbleSortTower(x, y, type);
            case SELECTION:
                return new SelectionSortTower(x, y, type);
        }
        return null;
    }
    
    /** Choose an enemy to target (the first enemy in range by default). */
    protected Enemy chooseTarget(List<Enemy> enemies) {
        // Target the first enemy in range
        Enemy target = null;
        for (Enemy e : enemies) {
            if (distanceTo(e) <= range) {
                target = e;
                break;
            }
        }
        return target;
    }
    
    /** Calculate a direction vector aiming at the target enemy. */
    protected Vector2f aimAt(Enemy target) {
        Vector2f vec = new Vector2f(target.getX() - getX(), target.getY() - getY());
        vec.add(target.getV());
        vec.normalise();
        return vec;
    }
    
    /** Places the tower. */
    public void place(float x, float y) {
        teleport(x, y);
        placed = true;
    }

    // TODO: refactor this to encapsulate it (don't have world shoot for it), and integrate with DynamicSprite::advance
    /**
     * Count down the shot timer.
     * @param delta Time in ms since the last update.
     */
    public void update(int delta) throws SlickException {
        nextShot -= delta;
        if (nextShot <= 0) {
            // Target the next enemy in range
            Enemy target = chooseTarget(App.getWorld().getEnemies());
            if (target == null) {
                // Instead of firing, just wait and try again next tick
                return;
            }
            
            // Aim at the target and shoot
            shoot(aimAt(target));
            
            // Reset the timer for the next shot
            nextShot = fireRate;
        }
    }

    /** Draws a range circle around towers. */
    public void drawRange(Graphics g) {
        // Top-left corner of the circle
        float xCorner = getX() - range, yCorner = getY() - range;

        // Draw circumference
        g.setColor(new Color(110, 110, 110, 110));
        g.drawOval(xCorner, yCorner, range * 2, range * 2);
        
        // Fill with a shade of grey (can change values depending on contrast w/ textures)
        g.setColor(new Color(80, 80, 80, 80));
        g.fillOval(xCorner, yCorner, range * 2, range * 2);
    }

    public void waveReset() {
        nextShot = 0;
    }

    public boolean isPlaced() { return placed; }

    public Type getType() { return type; }
}
