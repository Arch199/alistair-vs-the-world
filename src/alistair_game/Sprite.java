package alistair_game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

class Sprite {
    private float x, y;
    private Image im;
    private Color col = Color.white;

    Sprite(float x, float y, Image im) {
        this.x = x;
        this.y = y;
        this.im = im;
    }

    void drawSelf() { // Public or package-private or protected? (TODO: we should talk about this)
        im.draw(x - im.getWidth() / 2, y - im.getHeight() / 2, col);
    }

    boolean checkCollision(Sprite other) {
        int w = im.getWidth() / 2, h = im.getHeight() / 2;
        Image im2 = other.getImage();
        int w2 = im2.getWidth() / 2, h2 = im2.getHeight() / 2;
        float x2 = other.x, y2 = other.y;

        return (x + w >= x2 - w2 && x2 + w2 >= x - w && y + h >= y2 - h2 && y2 + h2 >= y - h);
        // I'm honestly amazed this works -James
    }
    
    float distanceTo(Sprite other) {
        return Util.dist(x, y, other.getX(), other.getY());
    }

    boolean isOffScreen(int window_w, int window_h) {
        int w = im.getWidth() / 2, h = im.getHeight() / 2;
        return x - w >= window_w || x + w < 0 || y - h >= window_h || y + h < 0;
    }

    void move(float xdist, float ydist) {
        x += xdist;
        y += ydist;
    }

    void teleport(float destx, float desty) {
        x = destx;
        y = desty;
    }

    float getX() { return x; }
    float getY() { return y; }
    Image getImage() { return im; }
    Color getColor() { return col; }

    void setColor(Color col) { this.col = col; }
    void setImage(Image im) { this.im = im; }
}
