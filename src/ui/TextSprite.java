package ui;

import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;

import control.Util;
import game.Sprite;

/** 
 * Sprite that also draws some text.
 */
public class TextSprite extends Sprite {
    public enum Mode { INSIDE, BELOW }
    
    private boolean textSet = false;
    private float textX, textY;
    private String text;
    private TrueTypeFont ttf;
    
    public TextSprite(float x, float y, Image im) {
        super(x, y, im);
    }
    
    @Override
    public void drawSelf() {
        super.drawSelf();
        if (textSet) {
            Util.writeCentered(ttf, text, textX, textY);
        }
    }
    
    public String getText() { return text; }
    
    public void setText(Mode mode, String text, TrueTypeFont ttf) {
        textSet = true;
        this.text = text;
        this.ttf = ttf;
        
        switch (mode) {
            case INSIDE:
                textX = getX();
                textY = getY();
                break;
            case BELOW:
                textX = getX();
                textY = getY() + getImage().getHeight()/2 + ttf.getLineHeight()/2;
                break;
        }
    }
}
