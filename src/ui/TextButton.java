package ui;

import control.Util;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

/** Rectangular button with text. */
public class TextButton extends Button {
    private static final int DEFAULT_PADDING = 5;
    private String text;
    private TrueTypeFont ttf;

    /** Create a clickable text button.
     * @param x x-position to center around.
     * @param y y-position.
     * @param text String to display.
     * @param ttf String font.
     * @param action Action to perform when triggered (left-clicked by default).
     */
    public TextButton(float x, float y, String text, TrueTypeFont ttf, Runnable action) {
        super(x, y, ttf.getWidth(text), ttf.getHeight(text), DEFAULT_PADDING, action);
        this.text = text;
        this.ttf = ttf;
    }

    /** Draw the button. */
    public void render(Graphics g) {
        Util.writeCentered(ttf, text, getX(), getTop(), getCurrentColor());
        super.render(g);
    }

    @Override
    public void setPadding(int padding) {
        super.setPadding(padding);
        setWidth(ttf.getWidth(text) + padding * 2);
        setHeight(ttf.getHeight() + padding * 2);
    }
}
