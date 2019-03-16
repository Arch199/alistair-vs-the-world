package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/** 
 * Moving item fired by a tower.
 */
class Projectile extends Movable {
    enum Type {
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
    }
    static final String SPRITE_PATH = "assets\\sprites\\projectiles\\";
    
    private Type type;
    
    /** Create a projectile.
     * @param startx Starting x position
     * @param starty Starting y position
     * @param vec Velocity vector to begin moving with
     * @param type Projectile type as an enum, e.g. Projectile.Type.BUBBLE 
     */
    Projectile(float startx, float starty, Vector2f vec, Type type) {
        super(startx, starty, vec, null, 0);
        this.type = type;
        setDamage(type.damage);
        try {
            setImage(new Image(SPRITE_PATH + type.imPath));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    Type getType() { return type; }
}
