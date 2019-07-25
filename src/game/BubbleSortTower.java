package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class BubbleSortTower extends Tower {
    public BubbleSortTower(float x, float y, Type type, World world) throws SlickException {
        super(x, y, type, world);
    }

    @Override
    protected void shoot(Vector2f dir) throws SlickException {
        world.addProjectile(new Bubble(world, getX(), getY()));
    }
    
    private static class Bubble extends Projectile {
        private static final int DAMAGE = 1;
        private static final float SCALE_INCR = 0.1f, SCALE_MAX = 7;
        
        public Bubble(World world, float startX, float startY) throws SlickException {
            super(world, startX, startY, new Vector2f(0, 0), new Image(Projectile.SPRITE_PATH + "bubble.png"), DAMAGE);
        }
        
        @Override
        public void advance() {
            super.advance();
            setScale(getScale() + SCALE_INCR);
        }
        
        @Override
        public boolean isDead() {
            return super.isDead() || getScale() > SCALE_MAX;
        }
    }
}
