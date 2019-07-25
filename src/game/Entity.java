package game;

/** On-screen object with a position and size. */
public abstract class Entity {
    private float x, y;
    private float w, h;
    private float scale = 1;
    
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
    }
    
    /**
     * Calculate the Euclidian distance to another entity.
     * @param other Other entity
     * @return Distance in pixels
     */
    public float distanceTo(Entity other) {
        return (float)Math.hypot(x - other.x, y - other.y);
    }
    
    /**
     * Check whether the entity contains a given point.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     */
    public boolean contains(int x, int y) {
        return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
    }
    
    /**
     * Move relative to the current position.
     * @param xDist Signed pixels in the x-plane to move
     * @param yDist Signed pixels in the y-plane to move
     */
    public void move(float xDist, float yDist) {
        x += xDist;
        y += yDist;
    }
    
    /**
     * Move instantly to an arbitrary position.
     * @param destX New x-position
     * @param destY New y-position
     */
    public void teleport(float destX, float destY) {
        x = destX;
        y = destY;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return (int)w; }
    public int getHeight() { return (int)h; }
    public float getLeft() { return x - w / 2; }
    public float getRight() { return x + w / 2; }
    public float getTop() { return y - h / 2; }
    public float getBottom() { return y + h / 2; }
    
    protected void setWidth(int width) { w = width; }
    protected void setHeight(int height) { h = height; }

    protected void setScale(float scale) {
        // Reset width and height to default
        w = w/this.scale;
        h = h/this.scale;

        // Find new width and height under new scale
        this.scale = scale;
        w = (w*scale);
        h = (h*scale);
    }
}
