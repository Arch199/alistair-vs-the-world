package game;

import control.App;
import control.AudioController;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class BubbleSortTower extends Tower {
    public BubbleSortTower(float x, float y, Type type) throws SlickException {
        super(x, y, type);
    }

    @Override
    protected void shoot(Vector2f dir) throws SlickException {
        App.getWorld().addProjectile(new Bubble(getX(), getY()));
    }
    
    private static class Bubble extends Projectile {
        private static final int DAMAGE = 1;
        private static final float SCALE_INCR = 0.015f, SCALE_MAX = 0.7f;
        
        public Bubble(float startX, float startY) throws SlickException {
            super(startX, startY, new Vector2f(0, 0), new Image(Projectile.SPRITE_PATH + "bubble-original.png"), DAMAGE);
            setScale(.1f);
        }
        
        @Override
        public void advance() {
            super.advance();
            setScale(getScale() + SCALE_INCR);
        }
        
        @Override
        public boolean isDead() {
            return super.isDead() || getScale() >= SCALE_MAX;
        }

        @Override
        public void pop() {
            AudioController.play("bubblepop");
        }
    }
}
