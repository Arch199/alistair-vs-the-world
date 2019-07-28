package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/** 
 * Moving item fired by a tower.
 */
public abstract class Projectile extends DynamicSprite {
    protected static final String SPRITE_PATH = "assets/sprites/projectiles/";
    
    public World world;
    
    /** Create a projectile.
     * @param startX Starting x position
     * @param startY Starting y position
     * @param vec Velocity vector to begin moving with
     * @param img Projectile image
     * @param damage Damage it deals
     */
    public Projectile(World world, float startX, float startY, Vector2f vec, Image im, int damage) {
        super(startX, startY, vec, im, damage);
        this.world = world;
    }
    
    @Override
    public boolean isDead() {
        return super.isDead() || isOffScreen(world.getWidth(), world.getHeight());
    }

    public void pop() {
        world.play("lowpop");
    }

    public World getWorld() { return world; }
}
