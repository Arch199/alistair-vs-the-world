package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

class EnemyPython extends Enemy {
	private static Image im;  // shadowing Sprite's im
	
	static {
		try {
			im = new Image("assets\\enemies\\python-icon.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	EnemyPython(float startx, float starty, double dir) {
		super(startx, starty, dir, im);
	}	
}

// Note: if the enemies don't end up having very different behaviour from each other,
// we should probably delete this class and just give the Enemy class a 'type' field.
