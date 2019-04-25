package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class SelectionSortTower extends Tower {
    public SelectionSortTower(float x, float y, Image im, World world) throws SlickException {
        super(x, y, im, world);
    }

    @Override
    protected void shoot(Vector2f dir) {
        // Create projectile
        world.addProjectile(new Ball(getX(), getY(), dir));
    }
    
    private class Ball extends Projectile {
        public Ball(float startx, float starty, Vector2f vec) {
            super(startx, starty, vec);
        }
        
    }
}
