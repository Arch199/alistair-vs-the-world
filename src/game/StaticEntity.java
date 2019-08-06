package game;

/** An immovable entity. */
public abstract class StaticEntity extends Entity {
    public StaticEntity(float x, float y, int w, int h) {
        super(x, y, w, h);
    }
    
    /**
     * Move relative to the current position.
     * Disallowed for static entities.
     * @param xDist Signed pixels in the x-plane to move
     * @param yDist Signed pixels in the y-play to move
     */
    @Override
    public void move(float xDist, float yDist) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot move");
    }
    
    /**
     * Move instantly to an arbitrary co-ordinate.
     * Disallowed for static entities.
     * @param destX New x-position
     * @param destY New y-position
     */
    @Override
    public void teleport(float destX, float destY) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Static entities cannot teleport");
    }
}
