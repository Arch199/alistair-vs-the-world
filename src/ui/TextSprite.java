package ui;

import control.World;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;

import control.Util;
import game.Sprite;

import java.util.HashMap;

/** 
 * Sprite that also draws some text.
 */
public class TextSprite extends Sprite {
    public enum Mode {INSIDE, BELOW, HOVER}
    
    private boolean hovered = false;
    private float textX, textY;
    private HashMap<Mode, Line> textItems = new HashMap<>();

    public TextSprite(float x, float y, Image im) {
        super(x, y, im);
    }
    
    @Override
    public void drawSelf() {
        super.drawSelf();

        for (Mode mode: textItems.keySet()) {
            Line line = textItems.get(mode);
            switch (mode) {
                case INSIDE:
                    Util.writeCentered(line.ttf, line.text, getX(), getY());
                    break;
                case BELOW:
                    Util.writeCentered(line.ttf, line.text, getX(),getY() + getHeight() / 2 + line.ttf.getLineHeight() / 2);
                    break;
                case HOVER:
                    if (hovered) {
                        Util.writeCentered(line.ttf, line.text, getX(), getY());
                    }
            }

        }
    }

    /** Get the text being displayed in a particular mode */
    public String getText(Mode mode) {
        if (textItems.containsKey(mode)) {
            return textItems.get(mode).text;
        } else {
            return "";
        }
    }

    /**
     * Set the text to display in a given position.
     * @param mode Position specifier enum
     * @param text Text to display in this position
     * @param ttf Font to use
     */
    public void setText(Mode mode, String text, TrueTypeFont ttf) {
        textItems.put(mode, new Line(text, ttf));
    }

    public void setHovered(Boolean hovered) {
        this.hovered = hovered;
    }

    /** A line of text and how to style it */
    private static class Line {
        String text;
        TrueTypeFont ttf = World.SMALL_TTF;

        Line(String text, TrueTypeFont ttf) {
            this.text = text;
            this.ttf = ttf;
        }

        Line (String text) {
            this.text = text;
        }
    }
}
