package control;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

/** Contains utility methods for use throughout the project. */
public final class Util {
    private static final Random RANDOM = new Random(System.nanoTime());
    
    private Util() {} // prevents instantiation from outside the class
    
    /** Generate a random number.
     * @param num Closed maximum value
     * @return Random value between 0 (inclusive) and the number (exclusive)
     * */
    public static int rand(int num) {
        return RANDOM.nextInt(num);
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
}