package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/** 
 * Moving item fired by a tower.
 */
public class Projectile extends Movable {
    Projectile(float startx, float starty, Vector2f vec, Image im) {
        super(startx, starty, vec, im, 1);
    }
}
