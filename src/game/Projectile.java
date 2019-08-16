package game;

import control.App;
import control.AudioController;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import control.World;

/** 
 * Moving item fired by a tower.
 */
public abstract class Projectile extends DynamicSprite {
    protected static final String SPRITE_PATH = "assets/sprites/projectiles/";

    /** Create a projectile.
     * @param startX Starting x position
     * @param startY Starting y position
     * @param vec Velocity vector to begin moving with
     * @param im Projectile image
     * @param damage Damage it deals
     */
    public Projectile(float startX, float startY, Vector2f vec, Image im, int damage) {
        super(startX, startY, vec, im, damage);
    }
    
    @Override
    public boolean isDead() {
        return super.isDead() || isOffScreen();
    }

    public void pop() {
        AudioController.play("lowpop");
    }
}
