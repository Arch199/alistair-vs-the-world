package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/**
 * Sprite that moves down the path and does damage to Alistair.
 */
public class Enemy extends DynamicSprite {
    public enum Type {
        // In order of increasing strength
        PYTHON("python-icon.png", 1, 1.5F),
        INT_MAX("int_max.png", 1, 2.1F),
        DO("do.png", 1, 3F),
        COMMERCE("fbe1.png", 2, 2F),
        OVERFLOW("stackoverflow32.png", 3, 2.5F),
        THOMAS("thomas100.png", 2, 5F);
        final String imPath;
        final int health;
        final float speed;
        Type(String imPath, int health, float speed) {
            this.imPath = imPath;
            this.health = health;
            this.speed = speed;
        }
    }
    public static final String SPRITE_PATH = "assets/sprites/enemies/";
    
    private Type type;
    private int health;
    private float speed;

    /**
     * Create an enemy
     * @param startx x-position of start
     * @param starty y-position of start
     * @param v Initial movement vector  (unscaled)
     * @param type Enemy type as an enum, e.g. Enemy.Type.PYTHON
     */
    public Enemy(float startx, float starty, Vector2f v, Type type) {
        super(startx, starty, v, null, 0);
        this.type = type;
        health = type.health;
        speed = type.speed;
        try {
            setImage(new Image(SPRITE_PATH + type.imPath));
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
    public void advance(float speed, World world) {
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
     */
    public void takeDamage(int damage) {
        health -= damage;
        setDamage(health);
    }
    
    @Override
    public boolean isDead() {
        return super.isDead() || health <= 0;
    }
    
    public float getSpeed() { return speed; }
    public Type getType() { return type; }
}
