package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/** 
 * Moving item fired by a tower.
 */
public abstract class Projectile extends DynamicSprite {
    protected static final String SPRITE_PATH = "assets/sprites/projectiles/";
    
    private World world;
    
    /** Create a projectile.
     * @param startx Starting x position
     * @param starty Starting y position
     * @param vec Velocity vector to begin moving with
     * @param img Projectile image
     * @param damage Damage it deals
     */
    public Projectile(World world, float startx, float starty, Vector2f vec, Image im, int damage) {
        super(startx, starty, vec, im, damage);
        this.world = world;
    }
    
    @Override
    public boolean isDead() {
        return isOffScreen(world.getWidth(), world.getHeight());
    }
}
