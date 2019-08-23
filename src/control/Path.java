package control;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

/** Represents a path enemies take through a map. */
public class Path {
    /** Unit vectors for the direction to move in at each position along the path. */
    private Vector2f[][] path;

    Path(World world, TiledMap map, int startX, int startY) {
        // Traverse the enemy path and store direction values in a grid
        path = new Vector2f[map.getWidth()][map.getHeight()];
        int x = startX, y = startY;
        Vector2f dir = world.inwardDir(x, y);
        int i = (int)dir.x, j = (int)dir.y;
        if (i == 0 && j == 0) {
            throw new IllegalArgumentException("Starting position must be outside grid");
        }
        while (!world.inGridBounds(x, y)) {
            System.out.println("while 1");
            x += i;
            y += j;
        }
        while (x != world.getAlistair().getX() || y != world.getAlistair().getY()) {
            System.out.println("while 2");
            // Move along the path
            // Check if we've hit a wall yet
            if (!world.inGridBounds(x + i, y + j) || world.getGridTile(x + i, y + j).isWall()) {
                // OK, try turning left (anti-clockwise)
                int old_i = i;
                i = j;
                j = -old_i;

                // Check again
                if (!world.inGridBounds(x + i, y + j) || world.getGridTile(x + i, y + j).isWall()) {
                    // Failed, turn right then (need to do a 180)
                    i = -i;
                    j = -j;
                }
            }
            // Update x, y and our direction
            path[x][y] = new Vector2f(i, j);
            x += i;
            y += j;
        }
    }

    public Vector2f getDir(int gridX, int gridY) { return path[gridX][gridY].copy(); }
}
