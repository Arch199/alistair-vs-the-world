package game;

import control.App;
import control.World;
import org.newdawn.slick.geom.Vector2f;

import static control.Util.newImage;

/** Sprite that moves down the path and does damage to Alistair. */
public class Enemy extends DynamicSprite {
    public enum Type {
        // In order of increasing strength. Note that speed is pixels per ms.
        // Each enemy deals damage equal to its health.
        PYTHON("python-icon.png", 1, 0.16f, 2), // TODO: balance money values
        INT_MAX("int_max.png", 2, 0.18f, 4),
        DO("do.png", 2, 0.22f, 5),
        COMMERCE("fbe1.png", 3, 0.15f, 8),
        OVERFLOW("stackoverflow32.png", 4, 0.2f, 15),
        THOMAS("thomas100.png", 3, 0.3f, 21);
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
    private static final String SPRITE_PATH = "assets/sprites/enemies/";
    
    private Type type;

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
        World world = App.getWorld();
        // Go one step at a time to check for collisions
        for (int i = 0; i < delta; i++) {
            int gridX = world.toGrid(getX()), gridY = world.toGrid(getY());
            float nextX = getX() + App.TILE_SIZE * Math.signum(getV().x) / 2,
                  nextY = getY() + App.TILE_SIZE * Math.signum(getV().y) / 2;
            if (!world.inGridBounds(gridX, gridY)) {
                // Head away from the edge of the map
                setV(world.inwardDir(gridX, gridY).scale(type.speed));
            } else if (world.getTile(nextX, nextY).isWall() && world.inGridBounds(gridX, gridY)) {
                // Follow the path
                setV(world.pathDir(gridX, gridY).scale(type.speed));
            }
            super.update(1);
        }

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
