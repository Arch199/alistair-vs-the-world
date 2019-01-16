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
    
    Button(float x, float y, String text, TrueTypeFont ttf, int padding, boolean hasBorder) {
        super(x, y, ttf.getWidth(text)+padding, ttf.getHeight()+padding);
        this.text = text;
        this.ttf = ttf;
        this.padding = padding;
        this.hasBorder = hasBorder;
    }
    
    void drawSelf(Graphics g, Color col) {
        ttf.drawString(x+padding/2, y+padding/2, text, col);
        if (hasBorder) {
            g.setColor(col);
            g.drawRect(getX(), getY(), getWidth(), getHeight());
        }
    }
    
    String getText() { return text; }
}
