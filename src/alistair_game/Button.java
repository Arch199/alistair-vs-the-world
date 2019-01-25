package alistair_game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

/**
 * Rectangular button with text.
 */
class Button extends Rectangle {
    private static final long serialVersionUID = 1L; // necessary as Rectangle implements java.io.serializable
    private String text;
    private TrueTypeFont ttf;
    private int padding;
    private boolean hasBorder;

    /**
     * Draws a clickable button.
     * @param x x-position to center around
     * @param y y-position
     * @param text String to display
     * @param ttf String font
     * @param padding Spacing between text and border on all sides
     * @param hasBorder Display the border
     */
    Button(float x, float y, String text, TrueTypeFont ttf, int padding, boolean hasBorder) {
        super(x - ttf.getWidth(text)/2-padding, y, ttf.getWidth(text)+padding*2,
                ttf.getHeight()+padding*2);
        this.text = text;
        this.ttf = ttf;
        this.padding = padding;
        this.hasBorder = hasBorder;
    }
    
    void drawSelf(Graphics g, Color col) {
        ttf.drawString(x+padding, y+padding, text, col);
        if (hasBorder) {
            g.setColor(col);
            g.drawRect(getX(), getY(), getWidth(), getHeight());
        }
    }
    
    String getText() { return text; }
}
