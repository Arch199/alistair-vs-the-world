package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

import org.newdawn.slick.Color;

class Tower extends Sprite {
	private boolean placed = false;
	private float range = 150; // Range is radius from center
	private int fireRate = 0; // In ms
	private int nextShot = 0; // Time of next fire (in ms from start of wave)
	private long spawnTime = 0; // Reset every wave

	Tower(float startx, float starty, Image im, int fireRate) {
		super(startx, starty, im);
		this.fireRate = fireRate;
		nextShot = 0;
	}

	/** Makes the shot. Generates a projectile and sets a new time. */
	void shoot(ArrayList<Projectile> projectiles) {
		try {
			// Make a new projectile
			Projectile new_proj;
			Image im = new Image("assets\\sprites\\defaultproj.png"); // TODO: move this reference elsewhere
			// TODO: A targeting function should be called here. Returns hsp/vsp to pass in below.
			new_proj = new Projectile((int)getX(), (int)getY(), 1f, 1f, im);
			projectiles.add(new_proj);

			// Determine the time of the next shot
			nextShot += fireRate;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	void place(float x, float y) {
		teleport(x, y);
		placed = true;
	}

	/** Returns true if enough time has passed to shoot. */
	boolean readyToShoot(long time) {
		long timeAlive = time - spawnTime;
		if (timeAlive >= nextShot) {
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

	void setSpawnTime(long time) {
		spawnTime = time;
	}

	void waveReset() {
		nextShot = 0;
		spawnTime = 0;
	}

	boolean isPlaced() { return placed; }
}
