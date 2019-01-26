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
    private Color defaultCol, disabledCol, hoverCol;
    private Boolean disabled = false , hovered = false;

    /**
     * Draws a simple clickable button in a single colour.
     *
     * @param x x-position to center around
     * @param y y-position
     * @param text String to display
     * @param ttf String font
     * @param padding Spacing between text and border on all sides
     * @param hasBorder Display the border
     * @param col The default colour for all states (call setCols to expand)
     */
    Button(float x, float y, String text, TrueTypeFont ttf, int padding, boolean hasBorder, Color col) {
        super(x - ttf.getWidth(text)/2-padding, y, ttf.getWidth(text)+padding*2,
                ttf.getHeight()+padding*2);
        setCols(col, col, col);
        this.text = text;
        this.ttf = ttf;
        this.padding = padding;
        this.hasBorder = hasBorder;
    }

    /** Draw the button with the current colour */
    void drawSelf(Graphics g) {

        Color currentCol;
        // Find the draw colour
        if (disabled) {
            currentCol = disabledCol;
        } else if (hovered){
            currentCol = hoverCol;
        } else {
            currentCol = defaultCol;
        }

        ttf.drawString(x+padding, y+padding, text, currentCol);
        if (hasBorder) {
            g.setColor(currentCol);
            g.drawRect(getX(), getY(), getWidth(), getHeight());
        }
    }

    /**
     * Set the colours of a button that can be hovered and disabled.
     *
     * Simple buttons do not need to call this.
     *
     * @param defaultCol Default colour
     * @param disabledCol Colour when disabled
     * @param hoverCol Colour on hover
     */
    void setCols(Color defaultCol, Color disabledCol, Color hoverCol) {
        this.defaultCol = defaultCol;
        this.disabledCol = disabledCol;
        this.hoverCol = hoverCol;
    }

    void setCols(Color defaultCol, Color hoverCol) {
        setCols(defaultCol, defaultCol, hoverCol);
    }


    /** Set the disabled state */
    void setDisabled(Boolean state) {
        disabled = state;
    }

    Boolean getDisabled() {
        return disabled;
    }

    /** Set the hover status */
    void setHover(Boolean hover) {
        hovered = hover;
    }

    String getText() { return text; }
}
