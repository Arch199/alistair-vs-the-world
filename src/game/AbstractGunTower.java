package game;

import control.App;
import org.newdawn.slick.geom.Vector2f;

public abstract class AbstractGunTower<T extends Projectile> extends Tower {
    /** Create a tower that can shoot regular projectiles.
     * @param x x-position.
     * @param y y-position.
     * @param type The type of tower to create. */
    public AbstractGunTower(float x, float y, Type type) {
        super(x, y, type);
    }

    protected abstract T newShot(float x, float y, Vector2f v);
    protected abstract float getShotSpeed();

    @Override
    protected void shoot(Vector2f dir) {
        // This assumes that the given dir is already scaled by speed
        App.getWorld().addEntity(newShot(getX(), getY(), dir));
    }

    @Override
    protected Vector2f aimAt(Enemy target) {
        // Assume the enemy will keep moving in a straight line, leading the shot
        return super.aimAt(target).scale(getShotSpeed()).add(target.getV());
    }
}
