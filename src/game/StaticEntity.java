package game;

import org.newdawn.slick.Graphics;

/** An immovable entity. */
public abstract class StaticEntity extends Entity {
    public StaticEntity(float x, float y, int w, int h) {
        super(x, y, w, h);
    }

    /** Update the entity's status.
     * Disallowed for static entities.
     */
    @Override
    public void update(int delta) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot be updated");
    }

    /** Render the entity.
     * Disallowed for static entities.
     */
    @Override
    public void render(Graphics g) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot be rendered individually");
    }

    /** Move relative to the current position.
     * Disallowed for static entities.
     */
    @Override
    public void move(float xDist, float yDist) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot move");
    }
    
    /** Move instantly to an arbitrary co-ordinate.
     * Disallowed for static entities.
     */
    @Override
    public void teleport(float destX, float destY) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot teleport");
    }
}
