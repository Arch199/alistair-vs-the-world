package alistair_game;

import java.util.Random;

import org.newdawn.slick.Graphics;

public class Util {
	/** Contains utility methods for use throughout the project. */
	
	/**
	 * Returns a random int from 0 to num-1 (inclusive).
	 */
	public static int rand(int num) {
		Random r = new Random(System.nanoTime());
        return r.nextInt(num);
	}
	
	/**
	 * Writes horizontally centered text.
	 */
	public static void writeCentered(Graphics g, String str, float x, float y) {
    	int offset = g.getFont().getWidth(str)/2;
    	g.drawString(str, x-offset, y);
    }

	/** Calculates the distance between two x-y coordinates. */
	public static float dist(float x1, float y1, float x2, float y2) {
		// Euclidean distance (COMP20008 method)
		return (float) Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
	}
}

class Vector {
	/** Holds projectile speeds and orientation */
	float vsp;
	float hsp;
	Vector(float vsp, float hsp) {
		this.vsp = vsp;
		this.hsp = hsp;
	}
}
