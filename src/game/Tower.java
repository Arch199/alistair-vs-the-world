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
        SELECTION("Selection Sort Alistair", "selection.png", 2000, 200f, 50, Color.black),
        BUBBLE("Bubble Sort Alistair", "bubble.png", 2200, 175f, 80, Color.blue),
        INSERTION("Insertion Sort Alistair", "insertion.png", 2100, 400f, 100, Color.black),
        QUICK("Quicksort Alistair", "quick.png", 2400, 150f, 150, Color.yellow);
        private final String title, imName;
        private final int cooldown, cost;
        private final float range;
        private final Color borderColor;
        Type(String title, String imName, int cooldown, float range, int cost, Color borderColor) {
            this.title = title;
            this.imName = imName;
            this.cooldown = cooldown;
            this.range = range;
            this.cost = cost;
            this.borderColor = borderColor;
        }
        public String toString() {
            return title;
        }
        public Image getImage() {
            return newImage(SPRITE_PATH + imName);
        }
        public float getRange() { return range; }
        public int getCost() { return cost; }
    }
    private static final String SPRITE_PATH = "assets/sprites/towers/";

    private boolean placed = false;
    private long nextShot = 0L; // Time until next fire (in ms)
    private Type type;

    /** Create a tower.
     * @param x x-position
     * @param y y-position
     * @param type the type of tower to create
     */
    public Tower(float x, float y, Type type) {
        super(x, y, type.getImage());
        this.type = type;
    }

    /** Fire a projectile at a given target. */
    protected abstract void shoot(Enemy target);

    /** Create a tower of a given type. */
    public static Tower create(Type type, float x, float y) {
        switch (type) {
            case BUBBLE: return new BubbleSortTower(x, y, type);
            case SELECTION: return new SelectionSortTower(x, y, type);
            case INSERTION: return new InsertionSortTower(x, y, type);
            case QUICK: return new QuickSortTower(x, y, type);
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
        setBorderColor(type.borderColor);
    }

    @Override
    public void update(int delta) {
        nextShot -= delta;
        if (nextShot <= 0) {
            // Target the next enemy in range
            chooseTarget().ifPresent(target -> {
                shoot(target);
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
