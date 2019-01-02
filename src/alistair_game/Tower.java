package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;

class Tower extends Sprite {
	boolean placed = false;
	// Range is radius from center
	float range = 150;

	Tower(float startx, float starty, Image im) {
		super(startx, starty, im);
	}
	
	void place(float x, float y) {
		teleport(x, y);
		placed = true;
	}

	void drawRange(Graphics g) {
		/* Draws a range circle around towers */
		// Top-left corner of the circle
		float xcorner = this.getX() - this.range, ycorner = this.getY() - this.range;
		g.drawOval(xcorner, ycorner, this.range*2, range*2);
	}

	boolean isPlaced() {
		return placed;
	}

	// TODO: add extra functionality to this
}
