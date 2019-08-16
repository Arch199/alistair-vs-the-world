package game;

import control.App;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

import static control.Util.newImage;

/** Sprite that moves down the path and does damage to Alistair. */
public class Enemy extends DynamicSprite {
    public enum Type {
        // In order of increasing strength. Note that speed is pixels per ms.
        // Each enemy deals damage equal to its health.
        PYTHON("python-icon.png", 1, 0.16f, 5), // TODO: balance money values
        INT_MAX("int_max.png", 1, 0.18f, 7),
        DO("do.png", 1, 0.21f, 10),
        COMMERCE("fbe1.png", 2, 0.15f, 21),
        OVERFLOW("stackoverflow32.png", 3, 0.2f, 30),
        THOMAS("thomas100.png", 2, 0.3f, 42);
        final String imPath;
        final int health, money;
        final float speed;
        Type(String imPath, int health, float speed, int money) {
            this.imPath = imPath;
            this.health = health;
            this.speed = speed;
            this.money = money;
        }
    }
    public static final String SPRITE_PATH = "assets/sprites/enemies/";
    
    private Type type;
    private int health;

    /** Create an enemy.
     * @param x Starting x-coordinate.
     * @param y Starting y-coordinate.
     * @param v Initial velocity (unscaled).
     * @param type Enemy type as an enum.
     */
    public Enemy(float x, float y, Vector2f v, Type type) {
        super(x, y, v, newImage(SPRITE_PATH + type.imPath), type.health, type.health);
        this.type = type;
        setV(v.normalise().scale(type.speed));
    }
    
    /** Move the enemy along the pre-calculated path. */
    @Override
    public void update(int delta) {
        // TODO: remake pathfinding and selection sort aiming
        World world = App.getWorld();
        int nextX = world.toGrid(getX() + getWidth() * Math.signum(getV().x));
        int nextY = world.toGrid(getY() + getHeight() * Math.signum(getV().y));
        int gridX = world.toGrid(getX()), gridY = world.toGrid(getY());

        // See if we are in the process of turning (our position, plus the entire width/height of the sprite is at the edge)
        if (world.getTile(nextX, nextY).isWall()) {
            // Optimally turn at the center of the tile (small buffer in case of lag/fast sprites)
            final int buffer = 5;
            if (Math.floorMod((int)getX(), App.TILE_SIZE*gridX + App.TILE_SIZE/2) < buffer &&
                Math.floorMod((int)getY(), App.TILE_SIZE*gridY + App.TILE_SIZE/2) < buffer) {
                // Turn
                if (world.inGridBounds(gridX, gridY)) {
                    setV(type.speed * world.getPathXDir(gridX, gridY), type.speed * world.getPathYDir(gridX, gridY));
                }
            }
        } else if (!world.inGridBounds(gridX, gridY)) {
            // Head away from the edge of the map
            setV(type.speed * world.inwardDirX(gridX), type.speed * world.inwardDirY(gridY));
        }
        super.update(delta);

        // Hitting Alistair
        if (checkCollision(world.getAlistair())) {
            world.damageAlistair(getDamage());
            kill();
        }
    }

    @Override
    public void takeDamage(int damage) {
        boolean wasDead = isDead();
        super.takeDamage(damage);
        if (isDead() && !wasDead) {
            App.getWorld().addMoney(type.money);
        }
    }

    public Type getType() { return type; }
}
