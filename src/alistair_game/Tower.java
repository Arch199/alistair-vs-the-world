package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

class Tower extends Sprite {
	boolean placed = false;
	// Range is radius from center
	float range = 150;
	// 50 = 1 sec
	float fireRate = 50;
	// Updates since last fire
	int lastShot = 0;

	Tower(float startx, float starty, Image im) {
		super(startx, starty, im);
	}
	
	void place(float x, float y) {
		teleport(x, y);
		placed = true;
	}

	/* Returns true if enough updates have passed to shoot */
	boolean readyToShoot() {
		lastShot++;
		if(fireRate == lastShot) {
			lastShot = 0;
			return true;
		}
		return false;
	}

	void drawRange(Graphics g) {
		/* Draws a range circle around towers */
		Color oldcol = g.getColor();

		// Top-left corner of the circle
		float xcorner = getX() - range, ycorner = getY() - range;

		// Draw circumference
		g.setColor(new Color(110, 110, 110,110));
		g.drawOval(xcorner, ycorner, range*2, range*2
		);
		// Fill with a shade of grey (can change vals depending on contrast w/ textures)
		g.setColor(new Color(80, 80, 80, 80));
		g.fillOval(xcorner, ycorner, range*2, range*2);

		// Reset color
		// MATT: How do colours work, and why does not resetting it break other graphics operations?
		g.setColor(oldcol);
	}

	void updateLastShot(int lastShot) {
		this.lastShot = lastShot;
	}
	boolean isPlaced() {
		return placed;
	}

	// TODO: add extra functionality to this
}
