package alistair_game;

import org.newdawn.slick.Image;

class Tower extends Sprite {
	boolean placed = false;
	
	Tower(float startx, float starty, Image im) {
		super(startx, starty, im);
	}
	
	void place(float x, float y) {
		teleport(x, y);
		placed = true;
	}
	
	// TODO: add extra functionality to this
}
