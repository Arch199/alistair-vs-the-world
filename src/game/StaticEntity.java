package game;

/** An immovable entity. */
public abstract class StaticEntity extends Entity {
    public StaticEntity(float x, float y, int w, int h) {
        super(x, y, w, h);
    }
    
    /**
     * Move relative to the current position.
     * Disallowed for static entities.
     * @param xDist signed pixels in the x-plane to move
     * @param yDist signed pixels in the y-play to move
     * @throws IllegalStateException
     */
    public void move(float xDist, float yDist) {
        throw new IllegalStateException("Static entities cannot move");
    }
    
    /**
     * Move instantly to an arbritary co-ordinate.
     * Disallowed for static entities.
     * @param destx new x-positon
     * @param desty new y-position
     * @throws IllegalStateException
     */
    public void teleport(float destX, float destY) {
        throw new IllegalStateException("Static entities cannot teleport");
    }
}
