package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

class EnemyPython extends Enemy {
    private static Image im; // shadowing Sprite's im (is this okay?)

    static {
        try {
            im = new Image("assets\\sprites\\enemies\\python-icon.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    EnemyPython(float startx, float starty, Vector2f vec) {
        super(startx, starty, vec, im);
    }
}

// Note: if the enemies don't end up having very different behaviour from each other,
// we should probably delete this class and just give the Enemy class a 'type' field.
