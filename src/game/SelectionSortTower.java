package game;

import org.newdawn.slick.geom.Vector2f;

import control.App;

import static control.Util.newImage;

public class SelectionSortTower extends Tower {
    public SelectionSortTower(float x, float y, Type type) {
        super(x, y, type);
    }

    @Override
    protected void shoot(Vector2f dir) {
        // This assumes that the given dir is already scaled by speed
        App.getWorld().addEntity(new Ball(getX(), getY(), dir));
    }
    
    @Override
    protected Vector2f aimAt(Enemy target) {
        // Assume the target keeps moving in a straight line (leading the shot)
        return super.aimAt(target).scale(Ball.SPEED).add(target.getV());
    }
    
    private static class Ball extends Projectile {
        private static final float SPEED = 0.3f;
        
        public Ball(float startX, float startY, Vector2f vec) {
            super(startX, startY, vec, newImage(Projectile.SPRITE_PATH + "defaultproj.png"), 1, 1);
        }
        
    }
}
