package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import control.World;

public class BubbleSortTower extends Tower {
    public BubbleSortTower(float x, float y, Image im, World world) throws SlickException {
        super(x, y, im, world);
    }
    
    @Override
    protected void shoot(Vector2f dir) {
        // TODO: implement this        
    }
}
