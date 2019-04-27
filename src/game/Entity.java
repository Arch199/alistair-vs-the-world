package game;

/** On-screen object with a position and size. */
public abstract class Entity {
    private float x, y;
    private int w, h;
    
    public Entity(float x, float y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    /**
     * Check if this entity is touching another (using rectangular collision boxes).
     * @param other Entity to check against
     * @return Returns true if touching
     */
    public boolean checkCollision(Entity other) {
        int w = getWidth() / 2, h = getHeight() / 2, w2 = other.getWidth() / 2, h2 = other.getHeight() / 2;
        float x2 = other.x, y2 = other.y;

        return ((x + w >= x2 - w2) && (x2 + w2 >= x - w) && (y + h >= y2 - h2) && (y2 + h2 >= y - h));
        // I'm honestly amazed this works -James
    }
    
    /**
     * Calculate the Euclidian distance to another entity.
     * @param other Other entity
     * @return Pixel distance
     */
    public float distanceTo(Entity other) {
        return (float)Math.hypot(x - other.x, y - other.y);
    }
    
    /**
     * Move relative to the current position.
     * @param xDist signed pixels in the x-plane to move
     * @param yDist signed pixels in the y-play to move
     */
    public void move(float xDist, float yDist) {
        x += xDist;
        y += yDist;
    }
    
    /**
     * Move instantly to an arbritary co-ordinate.
     * @param destx new x-positon
     * @param desty new y-position
     */
    public void teleport(float destX, float destY) {
        x = destX;
        y = destY;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return w; }
    public int getHeight() { return h; }
    public float getLeft() { return x - w / 2; }
    public float getRight() { return x + w / 2; }
    public float getTop() { return y - h / 2; }
    public float getBottom() { return y + h / 2; }
}
