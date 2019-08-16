package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/** On-screen object with an image. */
public class Sprite extends Entity {
    private Image im;
    private Color col = Color.white;

    /**
     * Create a sprite.
     * @param x Starting x-position
     * @param y Starting y-position
     * @param im Sprite image
     */
    public Sprite(float x, float y, Image im) {
        super(x, y, im.getWidth(), im.getHeight());
        this.im = im;
    }

    /** Draw the sprite. */
    public void render() {
        im.draw(getLeft(), getTop(), getScale(), col);
    }

    public Color getColor() { return col; }

    public void setColor(Color col) { this.col = col; }
}
