package game;

import control.App;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/** Sprite that moves down the path and does damage to Alistair. */
public class Enemy extends DynamicSprite {
    public enum Type {
        // In order of increasing strength
        PYTHON("python-icon.png", 1, 3.2F),
        INT_MAX("int_max.png", 1, 3.7F),
        DO("do.png", 1, 4.2F),
        COMMERCE("fbe1.png", 2, 3F),
        OVERFLOW("stackoverflow32.png", 3, 4F),
        THOMAS("thomas100.png", 2, 5.5F);
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
     * @param startX x-position of start
     * @param startY y-position of start
     * @param v Initial movement vector  (unscaled)
     * @param type Enemy type as an enum, e.g. Enemy.Type.PYTHON
     */
    public Enemy(float startX, float startY, Vector2f v, Type type) throws SlickException {
        super(startX, startY, v, new Image(SPRITE_PATH + type.imPath), 0);
        this.type = type;
        health = type.health;
        speed = type.speed;
        setV(v.scale(speed));
        setDamage(health);
    }
    
    /** Moves enemy along the pre-calculated path.
     * @param speed Magnitude of step
     */
    public void advance(float speed) {
        World world = App.getWorld();
        int nextX = world.toGrid(getX() + getWidth() * Math.signum(getV().x));
        int nextY = world.toGrid(getY() + getHeight() * Math.signum(getV().y));
        int gridX = world.toGrid(getX()), gridY = world.toGrid(getY());

        // See if we are in the process of turning (our position, plus the entire width/height of the sprite is at the edge)
        if (world.getTile(nextX, nextY).isWall()) {
            // Optimally turn at the center of the tile (small buffer in case of lag/fast sprites)
            final int BUFFER = 5;
            if (Math.floorMod((int)getX(), App.TILE_SIZE*gridX + App.TILE_SIZE/2) < BUFFER &&
                Math.floorMod((int)getY(), App.TILE_SIZE*gridY + App.TILE_SIZE/2) < BUFFER) {
                // Turn
                if (world.inGridBounds(gridX, gridY)) {
                    setV(speed * world.getPathXDir(gridX, gridY), speed * world.getPathYDir(gridX, gridY));
                }
            }
        } else if (!world.inGridBounds(gridX, gridY)) {
            // Head away from the edge of the map
            setV(speed * world.inwardDirX(gridX), speed * world.inwardDirY(gridY));
        }

        super.advance();
    }

    /**
     * Make an enemy take damage.
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
