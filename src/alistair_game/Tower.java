package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

import org.newdawn.slick.Color;

class Tower extends Sprite {
	private boolean placed = false;
	private float range = 150; // Range is radius from center
	private float fireRate = 50; // 50 = 1 sec
	private int lastShot = 0; // Updates since last fire

	Tower(float startx, float starty, Image im) {
		super(startx, starty, im);
	}
	
	/** Makes the shot. Generates a projectile. */
	void shoot(ArrayList<Projectile> projectiles) {
		try {
			Projectile new_proj;
			Image im = new Image("assets\\othersprites\\towerDefense_tile272.png");
			// TODO: A targeting function should be called here. Returns hsp/vsp to pass in below.
			new_proj = new Projectile((int)getX(), (int)getY(), 0.1f, 0.1f, im);
			projectiles.add(new_proj);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	void place(float x, float y) {
		teleport(x, y);
		placed = true;
	}

	/** Returns true if enough updates have passed to shoot. */
	boolean readyToShoot() {
		lastShot++;
		if(fireRate == lastShot) {
			lastShot = 0;
			return true;
		}
		return false;
	}

	/** Draws a range circle around towers. */
	void drawRange(Graphics g) {
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
	
	boolean isPlaced() { return placed; }
	
	void setLastShot(int lastShot) { this.lastShot = lastShot; }
}
