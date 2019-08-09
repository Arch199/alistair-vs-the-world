package game;

import control.App;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class SelectionSortTower extends Tower {
    public SelectionSortTower(float x, float y, Type type) throws SlickException {
        super(x, y, type);
    }

    @Override
    protected void shoot(Vector2f dir) throws SlickException {
        // This assumes that the given dir is already scaled by speed
        App.getWorld().addProjectile(new Ball(getX(), getY(), dir));
    }
    
    @Override
    protected Vector2f aimAt(Enemy target) {
        // Assume the target keeps moving in a straight line (leading the shot)
        return super.aimAt(target).scale(Ball.SPEED).add(target.getV());
    }
    
    private static class Ball extends Projectile {
        private static final int DAMAGE = 1;
        private static final float SPEED = 6f;
        
        public Ball(float startX, float startY, Vector2f vec) throws SlickException {
            super(startX, startY, vec, new Image(Projectile.SPRITE_PATH + "defaultproj.png"), DAMAGE);
        }
        
    }
}
