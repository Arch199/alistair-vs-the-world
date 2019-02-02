package alistair_game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/** On-screen object */
class Sprite {
    private float x, y;
    private Image im;
    private Color col = Color.white;
    private int w, h; // Half of the image's width and height

    /**
     * Create a sprite (stationary for the frame)
     * @param x x-pos
     * @param y y-pos
     * @param im sprite image
     */
    Sprite(float x, float y, Image im) {
        this.x = x;
        this.y = y;
        this.im = im;

        if (im != null) {
            w = im.getWidth() / 2;
            h = im.getHeight() / 2;
        }
    }
    
    /** Checks whether the mouse position intersects the Sprite. */
    boolean isMouseOver(int mouseX, int mouseY) {
        return (mouseX >= x-w && mouseX <= x+w && mouseY >= y-h && mouseY <= y+h);
    }
    
    void drawSelf() {
        im.draw(x-w, y-h, col);
    }

    /**
     * Check if this sprite is touching another (using rectangular collision boxes).
     * @param other Sprite to check against
     * @return Returns true if touching
     */
    boolean checkCollision(Sprite other) {
        Image im2 = other.getImage();
        int w2 = im2.getWidth() / 2, h2 = im2.getHeight() / 2;
        float x2 = other.x, y2 = other.y;

        return ((x + w >= x2 - w2) && (x2 + w2 >= x - w) && (y + h >= y2 - h2) && (y2 + h2 >= y - h));
        // I'm honestly amazed this works -James
    }

    /**
     * Calculate the Euclidian distance to another sprite.
     * @param other Other sprite
     * @return Pixel distance
     */
    float distanceTo(Sprite other) {
        return Util.dist(x, y, other.getX(), other.getY());
    }

    /**
     * Checks the sprite's position against the game boundraries.
     * @param windowW Game width
     * @param windowH Game height
     * @return Returns true if off the screen
     */
    boolean isOffScreen(int windowW, int windowH) {
        int w = im.getWidth() / 2, h = im.getHeight() / 2;
        return x - w >= windowW || x + w < 0 || y - h >= windowH || y + h < 0;
    }

    /**
     * Moves relative to the current position.
     * @param xdist signed pixels in the x-plane to move
     * @param ydist signed pixels in teh y-play to move
     */
    void move(float xDist, float yDist) {
        x += xDist;
        y += yDist;
    }

    /**
     * Move instantly to an arbritary coordinate.
     * @param destx new x-positon
     * @param desty new y-position
     */
    void teleport(float destX, float destY) {
        x = destX;
        y = destY;
    }

    float getX() { return x; }
    float getY() { return y; }
    Color getColor() { return col; }
    Image getImage() { return im; }

    void setColor(Color col) { this.col = col; }
    void setImage(Image im) {
        this.im = im;
        w = im.getWidth() / 2;
        h = im.getHeight() / 2;
    }
}
