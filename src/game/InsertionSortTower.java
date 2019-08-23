package game;

import org.newdawn.slick.geom.Vector2f;

import static control.Util.angleTowards;
import static control.Util.newImage;

public class InsertionSortTower extends AbstractGunTower<InsertionSortTower.Laser> {
    public InsertionSortTower(float x, float y, Tower.Type type) {
        super(x, y, type);
    }

    @Override
    protected Laser newShot(float x, float y, Vector2f v) {
        return new Laser(x, y, v);
    }

    @Override
    protected float getShotSpeed() { return 1f; }

    public static class Laser extends Projectile {
        private Laser(float x, float y, Vector2f v) {
            super(x, y, v, newImage(Projectile.SPRITE_PATH + "laser.png"), 1, 3);
            setAngle(angleTowards(v));
        }
    }
}
