package alistair_game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

class Button extends Rectangle {
    /**
     * Not quite sure what this serial ID does, was copied.
     */
    private static final long serialVersionUID = 1L;
    private String title;
    private int w, h, fontX, fontY;
    private TrueTypeFont ttf;
    
    //** im will be null
    Button(float bnX, float bnY, int w, int h, String title) {
        super(bnX, bnY, w, h);
        this.title = title;
        this.w = w;
        this.h = h;
    }
    
    Boolean isClicked(int mouseX, int mouseY, Boolean clicked) {
        return (this.contains(mouseX, mouseY) && clicked) ;
    }
    
    void drawButton(Graphics g, Color col) {
        ttf.drawString(fontX, fontY, title, col);
        g.setColor(col);
        g.drawRect(super.getX(), super.getY(), w, h);
    }
    
    void setText(TrueTypeFont ttf, int fontX, int fontY) { 
        this.ttf = ttf;
        this.fontX = fontX;
        this.fontY = fontY;
    }
   
}
