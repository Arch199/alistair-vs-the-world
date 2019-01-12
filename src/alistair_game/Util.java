package alistair_game;

import java.util.Random;

import org.newdawn.slick.Graphics;

/** Contains utility methods for use throughout the project. */
public class Util {

    /** Random number generator
     * @param num Closed maximum value
     * @return random value 0-num
     * */
    public static int rand(int num) {
        Random r = new Random(System.nanoTime());
        return r.nextInt(num);
    }

    /** Writes horizontally centered text.
     * @param str String to write
     * */
    public static void writeCentered(Graphics g, String str, float x, float y) {
        int offset = g.getFont().getWidth(str) / 2;
        g.drawString(str, x - offset, y);
    }

    /** Calculates the distance between two x-y coordinates. */
    public static float dist(float x1, float y1, float x2, float y2) {
        // Euclidean distance (COMP20008 method)
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}