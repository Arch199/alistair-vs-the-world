package game;

import control.App;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/** On-screen object with an image. */
public class Sprite extends Entity {
    private Image im;
    private Color col = Color.white;
    private float scale = App.SCALE_FACTOR;

    /**
     * Create a sprite.
     * @param x Starting x-position
     * @param y Starting y-position
     * @param im Sprite image
     */
    public Sprite(float x, float y, Image im) {
        super(x, y, im.getWidth(), im.getHeight());
        super.setScale(scale);
        this.im = im;
    }
    
    public void drawSelf() {
        im.draw(getLeft(), getTop(), scale, col);
    }

    /**
     * Check the sprite's position against the game boundraries.
     * @param windowW Game width
     * @param windowH Game height
     * @return Returns true if off the screen
     */
    public boolean isOffScreen(int windowW, int windowH) {
        return getLeft() >= windowW || getRight() < 0 || getTop() >= windowH || getBottom() < 0;
    }

    public Color getColor() { return col; }
    public float getScale() { return scale; }

    public void setColor(Color col) { this.col = col; }
    public void setScale(float scale) {
        super.setScale(scale);
        this.scale = scale;
    }
}
