package alistair_game;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

/** Contains utility methods for use throughout the project. */
public class Util {

    /** Random number generator.
     * @param num Closed maximum value
     * @return Random value 0-num
     * */
    public static int rand(int num) {
        Random r = new Random(System.nanoTime());
        return r.nextInt(num);
    }

    /** Write horizontally centered text.
     * @param g Graphics handler
     * @param str String to write
     * @param x X position
     * @param y Y position
     */
    public static void writeCentered(Graphics g, String str, float x, float y) {
        int offset = g.getFont().getWidth(str) / 2;
        g.drawString(str, x - offset, y);
    }
    
    /** Write horizontally centered text.
     * @param ttf True Type Font
     * @param str String to write
     * @param x X position
     * @param y Y position
     */
    public static void writeCentered(TrueTypeFont ttf, String str, float x, float y) {
        int offset = ttf.getWidth(str) / 2;
        ttf.drawString(x - offset, y, str);
    }

    /** Calculates the distance between two x-y coordinates. */
    public static float dist(float x1, float y1, float x2, float y2) {
        // Euclidean distance (COMP20008 method)
        return (float) Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }
}