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
     * @throws UnsupportedOperationException
     */
    public void move(float xDist, float yDist) {
        throw new UnsupportedOperationException("Static entities cannot move");
    }
    
    /**
     * Move instantly to an arbritary co-ordinate.
     * Disallowed for static entities.
     * @param destX New x-positon
     * @param destY New y-position
     * @throws UnsupportedOperationException
     */
    public void teleport(float destX, float destY) {
        throw new UnsupportedOperationException("Static entities cannot teleport");
    }
}
