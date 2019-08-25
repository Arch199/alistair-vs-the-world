package game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/** On-screen object with an image. */
public class Sprite extends Entity {
    private Image im;

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

    @Override
    public void render(Graphics g) {
        im.draw(getLeft(), getTop(), getScale(), getColor());
        super.render(g);
    }

    /** Dummy update method that does nothing. */
    @Override
    public void update(int delta) {}

    public float getAngle() { return im.getRotation(); }

    protected void setAngle(float angle) { im.setRotation(angle); }
    protected void rotate(float angle) { im.rotate(angle); }
}
