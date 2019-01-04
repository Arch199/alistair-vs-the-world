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
}
