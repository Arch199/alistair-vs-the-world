package control;

import java.util.Random;

import org.newdawn.slick.*;

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

    /** Write horizontally centered text.
     * @param ttf True Type Font.
     * @param str String to write.
     * @param x X position.
     * @param y Y position.
     * @param col Color to write in.
     */
    public static void writeCentered(TrueTypeFont ttf, String str, float x, float y, Color col) {
        int offset = ttf.getWidth(str) / 2;
        ttf.drawString(x - offset, y, str, col);
    }

    /** Create a new image, ignoring the SlickException. */
    public static Image newImage(String path) {
        try {
            return new Image(path);
        } catch (SlickException e) {
            e.printStackTrace();
            App.exit();
        }
        return null; // to soothe the compiler
    }
}