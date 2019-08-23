package game;

import org.newdawn.slick.geom.Vector2f;

import control.App;

import static control.Util.newImage;

public class SelectionSortTower extends AbstractGunTower {
    public SelectionSortTower(float x, float y, Tower.Type type) {
        super(x, y, type);
    }

    @Override
    protected Projectile newShot(float x, float y, Vector2f v) {
        return new Projectile(x, y, v, newImage(Projectile.SPRITE_PATH + "defaultproj.png"), 1, 1);
    }

    @Override
    protected float getShotSpeed() { return 0.3f; }
}
