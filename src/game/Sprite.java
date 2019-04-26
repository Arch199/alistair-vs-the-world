package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import control.Util;

/** On-screen object */
public class Sprite {
    private float x, y;
    private Image im;
    private Color col = Color.white;
    private float scale = 1f;

    /**
     * Create a sprite (stationary for the frame)
     * @param x x-pos
     * @param y y-pos
     * @param im sprite image
     */
    public Sprite(float x, float y, Image im) {
        this.x = x;
        this.y = y;
        this.im = im;
    }
    
    /** Checks whether the mouse position intersects the Sprite. */
    public boolean isMouseOver(int mouseX, int mouseY) {
        int w = getWidth() / 2, h = getHeight() / 2;
        return (mouseX >= x - w && mouseX <= x + w && mouseY >= y - h && mouseY <= y + h);
    }
    
    public void drawSelf() {
        im.draw(x - getWidth() / 2, y - getHeight() / 2, scale, col);
    }

    /**
     * Check if this sprite is touching another (using rectangular collision boxes).
     * @param other Sprite to check against
     * @return Returns true if touching
     */
    public boolean checkCollision(Sprite other) {
        int w = getWidth() / 2, h = getHeight() / 2, w2 = other.getWidth() / 2, h2 = other.getHeight() / 2;
        float x2 = other.x, y2 = other.y;

        return ((x + w >= x2 - w2) && (x2 + w2 >= x - w) && (y + h >= y2 - h2) && (y2 + h2 >= y - h));
        // I'm honestly amazed this works -James
    }

    /**
     * Calculate the Euclidian distance to another sprite.
     * @param other Other sprite
     * @return Pixel distance
     */
    public float distanceTo(Sprite other) {
        return Util.dist(x, y, other.getX(), other.getY());
    }

    /**
     * Checks the sprite's position against the game boundraries.
     * @param windowW Game width
     * @param windowH Game height
     * @return Returns true if off the screen
     */
    public boolean isOffScreen(int windowW, int windowH) {
        int w = getWidth() / 2, h = getHeight() / 2;
        return x - w >= windowW || x + w < 0 || y - h >= windowH || y + h < 0;
    }

    /**
     * Moves relative to the current position.
     * @param xdist signed pixels in the x-plane to move
     * @param ydist signed pixels in teh y-play to move
     */
    public void move(float xDist, float yDist) {
        x += xDist;
        y += yDist;
    }

    /**
     * Move instantly to an arbritary coordinate.
     * @param destx new x-positon
     * @param desty new y-position
     */
    public void teleport(float destX, float destY) {
        x = destX;
        y = destY;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public Color getColor() { return col; }
    public float getScale() { return scale; }
    public int getWidth() { return (int)(im.getWidth() * scale); }
    public int getHeight() { return (int)(im.getHeight() * scale); }

    public void setImage(Image im) { this.im = im; }
    public void setColor(Color col) { this.col = col; }
    public void setScale(float scale) { this.scale = scale; }
}
