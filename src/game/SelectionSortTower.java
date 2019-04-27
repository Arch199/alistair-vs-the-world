package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class SelectionSortTower extends Tower {
    public SelectionSortTower(float x, float y, Type type, World world) throws SlickException {
        super(x, y, type, world);
    }

    @Override
    protected void shoot(Vector2f dir) throws SlickException {
        world.addProjectile(new Ball(world, getX(), getY(), dir.scale(Ball.SPEED)));
    }
    
    private static class Ball extends Projectile {
        private static final int DAMAGE = 1;
        private static final float SPEED = 6f;
        
        public Ball(World world, float startX, float startY, Vector2f vec) throws SlickException {
            super(world, startX, startY, vec, new Image(Projectile.SPRITE_PATH + "defaultproj.png"), DAMAGE);
        }
        
    }
}
