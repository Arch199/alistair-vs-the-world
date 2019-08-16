package game;

import control.App;
import org.newdawn.slick.Graphics;

/** On-screen object with a position, size, and scale. */
public abstract class Entity {
    private float x, y, w, h, scale = 1f;
    private boolean dead = false;

    public Entity(float x, float y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /** Update the entity's status.
     * @param delta Time in ms since the last update.
     */
    public abstract void update(int delta);

    /** Render the entity to the screen
     * @param g The current graphics context.
     */
    public abstract void render(Graphics g);

    /** Check if this entity is touching another (using rectangular collision boxes).
     * @param other Entity to check against
     * @return Returns true if touching
     */
    public boolean checkCollision(Entity other) {
        return getRight() >= other.getLeft() && other.getRight() >= getLeft()
            && getBottom() >= other.getTop() && other.getBottom() >= getTop();
    }
    
    /** Calculate the Euclidean distance to another entity.
     * @param other Other entity
     * @return Distance in pixels
     */
    public float distanceTo(Entity other) {
        return (float)Math.hypot(x - other.x, y - other.y);
    }
    
    /** Check whether the entity contains a given point.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     */
    public boolean contains(int x, int y) {
        return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
    }
    
    /** Move relative to the current position.
     * @param xDist Signed pixels in the x-plane to move
     * @param yDist Signed pixels in the y-plane to move
     */
    public void move(float xDist, float yDist) {
        x += xDist;
        y += yDist;
    }
    
    /** Move instantly to an arbitrary position.
     * @param destX New x-position
     * @param destY New y-position
     */
    public void teleport(float destX, float destY) {
        x = destX;
        y = destY;
    }

    /** Check the entity's position against the game boundaries.
     * @return Whether the entity is completely outside of the screen.
     */
    public boolean isOffScreen() {
        return getLeft() >= App.WINDOW_W || getRight() < 0 || getTop() >= App.WINDOW_H || getBottom() < 0;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return (int)w; }
    public int getHeight() { return (int)h; }
    public float getLeft() { return x - w / 2; }
    public float getRight() { return x + w / 2; }
    public float getTop() { return y - h / 2; }
    public float getBottom() { return y + h / 2; }
    public float getScale() { return scale; }
    public boolean isDead() { return dead; }
    
    protected void setWidth(int width) { w = width; }
    protected void setHeight(int height) { h = height; }
    protected void setScale(float scale) {
        w *= scale / this.scale;
        h *= scale / this.scale;
        this.scale = scale;
    }
    protected void kill() { dead = true; }
}
