package game;

import control.App;
import control.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Optional;

import static control.Util.newImage;

/** Towers are placed on a grid and shoot projectiles at enemies. */
public abstract class Tower extends Sprite {
    public enum Type {
        SELECTION("Selection Sort Alistair", "selection.png", 2000, 200f, 50),
        BUBBLE("Bubble Sort Alistair", "bubble.png", 2400, 175f, 80),
        INSERTION("Insertion Sort Alistair", "insertion.png", 1400, 400f, 100);
        private final String title, imName;
        private final int cooldown, cost;
        private final float range;
        Type(String title, String imName, int cooldown, float range, int cost) {
            this.title = title;
            this.imName = imName;
            this.cooldown = cooldown;
            this.range = range;
            this.cost = cost;
        }
        public String toString() {
            return title;
        }
        public Image getImage() {
            return newImage(SPRITE_PATH + imName);
        }
        public int getCooldown() { return cooldown; }
        public float getRange() { return range; }
        public int getCost() { return cost; }
    }
    private static final String SPRITE_PATH = "assets/sprites/towers/";

    private boolean placed = false;
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
        this.type = type;
    }

    /** Fires a projectile in the given direction. */
    protected abstract void shoot(Vector2f dir);

    /** Create a tower of a given type. */
    public static Tower create(Type type, float x, float y) {
        switch (type) {
            case BUBBLE: return new BubbleSortTower(x, y, type);
            case SELECTION: return new SelectionSortTower(x, y, type);
            case INSERTION: return new InsertionSortTower(x, y, type);
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }
    
    /** Choose an enemy to target (the first enemy in range by default). */
    protected Optional<Enemy> chooseTarget() {
        return App.getWorld().getAll(Enemy.class).filter(e -> distanceTo(e) <= type.range).findFirst();
    }
    
    /** Calculate a direction vector aiming at the target enemy. */
    protected Vector2f aimAt(Enemy target) {
        Vector2f vec = new Vector2f(target.getX() - getX(), target.getY() - getY());
        return vec.add(target.getV()).normalise();
    }
    
    /** Places the tower. */
    public void place(float x, float y) {
        teleport(x, y);
        placed = true;
    }

    @Override
    public void update(int delta) {
        nextShot -= delta;
        if (nextShot <= 0) {
            // Target the next enemy in range
            chooseTarget().ifPresent(target -> {
                shoot(aimAt(target));
                nextShot = type.cooldown;
            });
        }
    }

    /** Draw a range circle around towers. */
    public void drawRange(Graphics g) {
        // Top-left corner of the circle
        float xCorner = getX() - type.range, yCorner = getY() - type.range;

        // Draw circumference
        g.setColor(new Color(110, 110, 110, 110));
        g.drawOval(xCorner, yCorner, type.range * 2, type.range * 2);
        
        // Fill with a shade of grey (can change values depending on contrast w/ textures)
        g.setColor(new Color(80, 80, 80, 80));
        g.fillOval(xCorner, yCorner, type.range * 2, type.range * 2);
    }

    public void waveReset() {
        nextShot = 0;
    }

    public boolean isPlaced() { return placed; }
    public Type getType() { return type; }
}
