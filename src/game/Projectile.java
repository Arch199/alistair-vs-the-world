package game;

import control.App;
import control.AudioController;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Iterator;

/** Moving item fired by a tower. */
public class Projectile extends DynamicSprite {
    static final String SPRITE_PATH = "assets/sprites/projectiles/";

    /** Create a projectile.
     * @param x Starting x position.
     * @param y Starting y position.
     * @param vec Velocity vector to begin moving with.
     * @param im Projectile image.
     * @param damage Damage it deals.
     * @param health Its health total.
     */
    public Projectile(float x, float y, Vector2f vec, Image im, int damage, int health) {
        super(x, y, vec, im, damage, health);
    }

    @Override
    public void update(int delta) {
        if (isOffScreen()) {
            kill();
        } else {
            // Hitting enemies
            App.getWorld().getAll(Enemy.class).filter(this::checkCollision).findAny().ifPresent(other -> {
                other.takeDamage(getDamage());
                takeDamage(other.getDamage());
            });
            super.update(delta);
        }
    }

    // TODO: standardize sound effect methods
    public void pop() {
        AudioController.play("lowpop");
    }
}
