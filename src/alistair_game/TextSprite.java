package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;

/** 
 * Sprite that also draws some text.
 */
class TextSprite extends Sprite {
    enum Mode { INSIDE, BELOW }
    
    boolean textSet = false;
    float textX, textY;
    String text;
    TrueTypeFont ttf;
    
    TextSprite(float x, float y, Image im) {
        super(x, y, im);
    }
    
    @Override
    void drawSelf() {
        super.drawSelf();
        if (textSet) {
            Util.writeCentered(ttf, text, textX, textY);
        }
    }
    
    String getText() { return text; }
    
    void setText(Mode mode, String text, TrueTypeFont ttf) {
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
