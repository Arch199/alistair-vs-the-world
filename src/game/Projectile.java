package game;

import org.newdawn.slick.geom.Vector2f;

/** 
 * Moving item fired by a tower.
 */
public abstract class Projectile extends DynamicSprite {
    /*public enum Type {
        BUBBLE("bubble.png", 1, 2f),
        SELECTION("defaultproj.png", 1, 6f);
        final String imPath;
        final int damage;
        final float speed;
        Type(String imPath, int damage, float speed) {
            this.imPath = imPath;
            this.damage = damage;
            this.speed = speed;
        }
    }*/
    public static final String SPRITE_PATH = "assets/sprites/projectiles/";
    
    //private Type type;
    
    /** Create a projectile.
     * @param startx Starting x position
     * @param starty Starting y position
     * @param vec Velocity vector to begin moving with
     * @param type Projectile type as an enum, e.g. Projectile.Type.BUBBLE
     */
    public Projectile(float startx, float starty, Vector2f vec) {
        super(startx, starty, vec, null, 0);
    }
}
