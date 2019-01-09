package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

class EnemyPython extends Enemy {
    private static Image im;
    static {
        try {
            im = new Image("assets\\sprites\\enemies\\python-icon.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    EnemyPython(float startx, float starty, float hsp, float vsp) {
        super(startx, starty, hsp, vsp, im);
    }
}

// Note: if the enemies don't end up having very different behaviour from each other,
// we should probably delete this class and just give the Enemy class a 'type' field.
