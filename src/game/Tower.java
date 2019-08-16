package game;

import control.App;
import control.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Optional;

/** Towers are placed on a grid and shoot projectiles at enemies. */
public abstract class Tower extends Sprite {
    public enum Type {
        SELECTION("Selection Sort Alistair", "selection.png", 2000, 250f, 50),
        BUBBLE("Bubble Sort Alistair", "bubble.png", 2400, 175f, 80);
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
        public int getFireRate() { return fireRate; }
        public float getRange() { return range; }
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
    protected abstract void shoot(Vector2f dir);
    
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
    protected Optional<Enemy> chooseTarget() {
        return App.getWorld().getEnemies().filter(e -> distanceTo(e) <= range).findFirst();
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

    @Override
    public void update(int delta) {
        nextShot -= delta;
        if (nextShot <= 0) {
            // Target the next enemy in range
            chooseTarget().ifPresent(target -> {
                shoot(aimAt(target));
                nextShot = fireRate;
            });
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
