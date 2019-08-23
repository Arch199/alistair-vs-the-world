package game;

import control.App;
import control.AudioController;
import org.newdawn.slick.geom.Vector2f;

import static control.Util.newImage;

public class BubbleSortTower extends Tower {
    public BubbleSortTower(float x, float y, Type type) {
        super(x, y, type);
    }

    @Override
    protected void shoot(Vector2f dir) {
        App.getWorld().addEntity(new Bubble(getX(), getY()));
    }
    
    private static class Bubble extends Projectile {
        private static final float SCALE_INCREMENT = 0.0006f;
        
        public Bubble(float x, float y) {
            super(x, y, new Vector2f(0, 0), newImage(Projectile.SPRITE_PATH + "bubble-original.png"), 1, 2);
            setScale(0.05f);
        }
        
        @Override
        public void update(int delta) {
            if (getWidth() > Type.BUBBLE.getRange() * 2) {
                kill();
            } else {
                setScale(getScale() + SCALE_INCREMENT * delta);
                super.update(delta);
            }
        }

        @Override
        public void pop() {
            AudioController.play("bubblepop");
        }
    }
}
