package control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import game.Enemy;
import game.Projectile;
import game.StaticEntity;
import game.Tower;
import ui.Button;
import ui.TextSprite;

/** Handles all the game logic for a level. Created by App. */
public class World {
    private static final Font
        SMALL_FONT = new Font("Verdana", Font.PLAIN, 11),
        MEDIUM_FONT = new Font("Verdana", Font.BOLD, 20);
    private static final TrueTypeFont
        SMALL_TTF = new TrueTypeFont(SMALL_FONT, true),
        MEDIUM_TTF = new TrueTypeFont(MEDIUM_FONT, true); 

    private int w, h, tileSize, gridW, gridH, sidebarW;
    private float startX, startY;
    private int health = 100, waveNum = 0;
    private long timer = 0;
    private Tile alistair;
    private Tower myTower = null,       // Tower currently being placed
                  selectedTower = null; // Placed tower that has been selected
    private Boolean waveComplete = true;
    private Button nextWave;
    
    private TiledMap map;
    private Tile[][] tiles;
    private List<Wave> waves;
    private List<Projectile> projectiles = new LinkedList<>();
    private List<Tower> towers = new LinkedList<>();
    private List<TextSprite> sidebarIcons = new ArrayList<TextSprite>();
    private List<Button> buttons = new ArrayList<Button>();
    /** Enemy path. 3D array for x-coord, y-coord and direction for enemy to move move */
    private int[][][] path;
    /** List of enemies in crder of creation (oldest first) */
    private List<Enemy> enemies = new LinkedList<>();

    /**
     * Creates the world.
     * @param w Map width
     * @param h Map height
     * @param map The tiled map to render
     * @param waves Data on waves and enemy spawn timing
     */
    public World(int w, int h, int sidebarW, TiledMap map, ArrayList<Wave> waves) {
        this.w = w;
        this.h = h;
        this.gridW = (w - sidebarW) / tileSize;
        this.gridH = h / tileSize;
        this.waves = waves;
        this.sidebarW = sidebarW;
        this.map = map;
        
        // Assert that the map has square tiles
        if (map.getTileWidth() != map.getTileHeight()) {
            throw new IllegalArgumentException("Given TiledMap must have square tiles");
        }
        this.tileSize = map.getTileWidth();
        
        // Get the enemy spawn location
        String[] pos = map.getMapProperty("startPos", "0,0").split(",");
        startX = Float.parseFloat(pos[0]);
        startY = Float.parseFloat(pos[1]);
        
        // TODO: get alistair's location
        
        // Extract the tile data into an array for easy access
        tiles = new Tile[map.getWidth()][map.getHeight()];
        TileSet tileSet = map.getTileSet(0);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int id = map.getTileId(x, y, 0);
                tiles[x][y] = new Tile(toPos(x), toPos(y), tileSize, tileSet.getProperties(id));
            }
        }

        // Traverse the enemy path and store direction values in a grid
        path = new int[gridW][gridH][2];
        int x = toGrid(startX), y = toGrid(startY);
        int i = defaultDir(x);
        int j = defaultDir(y);
        while (x < 0 || x >= gridW || y < 0 || y >= gridH) {
            x += i;
            y += j;
        }
        while (toPos(x) != alistair.getX() || toPos(y) != alistair.getY()) {
            // Move along the path
            // Check if we've hit a wall yet
            if (x + i < 0 || x + i >= gridW || y + j < 0 || y + j >= gridH || tiles[x+i][y+j].isWall()) {
                // OK, try turning left (anti-clockwise)
                int old_i = i;
                i = j;
                j = -old_i;

                // Check again
                
                if (tiles[x+i][y+j].isWall()) {
                    // Failed, turn right then (need to do a 180)
                    i = -i;
                    j = -j;
                }
            }
            // Update x, y and our direction
            path[x][y][0] = i;
            path[x][y][1] = j;
            x += i;
            y += j;
        }
        
        // Create sidebar icons for buying towers
        // TODO: add cost in currency
        float xPos = w - sidebarW/2, yPos = 100;
        for (Tower.Type t : Tower.Type.values()) {
            try {
                TextSprite icon = new TextSprite(xPos, yPos, t.getImage());
                icon.setText(TextSprite.Mode.BELOW, t.toString(), SMALL_TTF);
                sidebarIcons.add(icon);
                yPos += 65;
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

        // New wave button
        float btnXPos = w - sidebarW/2, btnYPos = 500;
        nextWave = new Button(btnXPos, btnYPos, "Next wave", MEDIUM_TTF, 5, true, Color.green);
        nextWave.setCols(Color.green, Color.black, Color.white);
        buttons.add(nextWave);
        
        // Play intro sound
        AudioController.play("intro");
    }
    
    /**
     * Deals with user input e.g. pressing Esc to return to main menu.
     * @param Obtained from App's GameContainer
     * @return A string for an action to take. (Empty string by default).
     */
    public String processInput(Boolean escape, Boolean rightClick) {
        // Deselect
        if (rightClick) {
            myTower = null;
            selectedTower = null;
        }

        // Leave the game
        if (escape) {
            return "Exit";
        }

        return "";
    }
    
    /**
     * Keeps track of the time (in ms) from the start of the wave. Spawns enemies
     * and projectiles accordingly.
     * @param delta ms from last tick
     */
    public void tick(int delta) throws SlickException {
        timer += delta;

        // Enemy spawning (based on the current wave)
        if (waveNum > 0 && waveNum - 1 < waves.size()) {
            Wave w = waves.get(waveNum - 1);
            // TODO: Make this return an array to spawn multiple enemies in the same tick
            Enemy.Type enemyType = w.trySpawn(timer);
            if (enemyType != null) {
                spawnEnemy(startX, startY, enemyType);
            }

            // Set wave status
            waveComplete = w.isFinished() && enemies.isEmpty();
        }

        // Tower counting down / shooting
        for (Tower t : towers) {
            t.update(delta);
        }
    }

    /** Call every time a new wave starts */
    private void newWave() {
        waveNum++;
        timer = 0;
        for (Tower t : towers) {
            t.waveReset();
        }
    }

    /** Create a new enemy at the given position. */
    private void spawnEnemy(float x, float y, Enemy.Type type) {
        Vector2f v = new Vector2f(defaultDir(x), defaultDir(y));
        enemies.add(new Enemy(x, y, v, type));
    }

    /** Update enemy positons. */
    public void processEnemies() {
        Iterator<Enemy> itr = enemies.iterator();
        while (itr.hasNext()) {
            Enemy e = itr.next();
            // Hitting alistair
            e.advance(e.getSpeed(), this);
            if (e.checkCollision(alistair)) {
                takeDamage(e.getDamage());
                itr.remove();
            }
        }
    }

    /** Update projectile positions. */
    void processProjectiles() {
        Iterator<Projectile> itr = projectiles.iterator();
        while (itr.hasNext()) {
            Projectile p = itr.next();
            p.advance();
            if (p.isDead()) {
                itr.remove();
                continue;
            }

            // Hitting enemies
            Iterator<Enemy> eItr = enemies.iterator();
            while (eItr.hasNext()) {
                Enemy e = eItr.next();
                if (p.checkCollision(e)) {
                    e.takeDamage(p.getDamage());
                    if (e.isDead()) {
                        eItr.remove();
                    }
                    itr.remove();
                    break;
                }
            }
        }
    }

    /** Handles selecting / placing towers. */
    public void processTowers(int mouseX, int mouseY, boolean clicked) throws SlickException {
        // Click on a tower to display its range
        if (!isPlacingTower() && clicked) {
            for (Tower t: towers) {
                if (t.isMouseOver(mouseX, mouseY) && t != myTower) {
                    selectedTower = t;
                }
            }
        }

        // Deselect a selected tower
        if (selectedTower != null) {
            if (isPlacingTower() || (clicked && !selectedTower.isMouseOver(mouseX, mouseY))) {
                selectedTower = null;
            }
        }

        // Process selecting towers from the sidebar
        if (!isPlacingTower() && clicked) {
            for (TextSprite s : sidebarIcons) {
                if (s.isMouseOver(mouseX, mouseY)) {
                    myTower = Tower.create(Tower.Type.fromTitle(s.getText()), mouseX, mouseY, this);
                    return;
                }
            }
        }

        // If we're placing a tower, move it to the mouse position
        if (isPlacingTower()) {
            myTower.setColor(Color.white);
            myTower.teleport((float)mouseX, (float)mouseY);

            // Red if out of game bounds
            if (!inGridBounds(mouseX / tileSize, mouseY / tileSize)) {
                myTower.setColor(Color.red);
                if (clicked) {
                    myTower = null;
                    return;
                }
            }

            // Set the tower to be red if it's touching a non-wall tile or tower
            // TODO: Replace with TiledMap Function
            if (tiles[mouseX][mouseY].isWall()) {
                myTower.setColor(Color.red);
            }
            
            /*
            outer:
            for (Tile[] column : tiles) {
                for (Tile tile : column) {
                    if (!tile.isWall() && tile.checkCollision(myTower)) {
                        myTower.setColor(Color.red);
                        break outer
                    }
                }
            }
            */
            outer:
            for (Tower t : towers) {
                if (t.checkCollision(myTower)) {
                    myTower.setColor(Color.red);
                    break outer;
                }
            }
                
            // If the user clicked and it's not colliding with anything, place it
            if (clicked && myTower.getColor() == Color.white) {
                myTower.place(toPos(toGrid(mouseX)), toPos(toGrid(mouseY)));
                towers.add(myTower);
                myTower = null;
            }
        }
    }

    /** Button updates and colour changes. */
    public void processButtons(int mousex, int mousey, boolean clicked) {
        for (Button b: buttons) {
            // Button disabling

            // New wave button disabling
            if (b == nextWave && !waveComplete) {
                nextWave.setDisabled(true);
            } else {
                nextWave.setDisabled(false);
            }

            // Button clicking
            if (b.contains(mousex, mousey)) {
                b.setHover(true);
                if (clicked && !b.getDisabled()) {
                    // New wave button
                    if (b == nextWave) {
                        newWave();
                    }
                }
            } else {
                b.setHover(false);
        }
        }
    }

    /** Draw game interface */
    public void drawGUI(Graphics g) {
        // Sidebar
        g.setColor(Color.darkGray);
        g.fillRect(w-sidebarW, 0, sidebarW, h);

        // Draw sidebar icons
        g.setColor(Color.white);
        for (TextSprite s : sidebarIcons) {
            s.drawSelf();
        }
        
        // Tower being placed
        if (myTower != null) {
            myTower.drawSelf();
            myTower.drawRange(g);
        }

        if (selectedTower != null) {
            selectedTower.drawRange(g);
        }

        // Draw all buttons
        g.setColor(Color.white);
        for (Button b: buttons) {
            b.drawSelf(g);
        }

        // Display wave number and Alistair's health
        g.setColor(Color.white);
        Util.writeCentered(g, "Wave: " + waveNum,w-(sidebarW/2), 20);
        Util.writeCentered(g, Integer.toString(health), alistair.getX(), alistair.getY());
    }
    
    // TODO: Replace with tiledmap functionality
    public void renderTiles() {
        map.render(0, 0);
    }

    public void renderEnemies() {
        for (Enemy e : enemies) {
            e.drawSelf();
        }
    }

    public void renderTowers(Graphics g) {
        for (Tower t : towers) {
            t.drawSelf();
        }
    }

    public void renderProjectiles() {
        for (Projectile p : projectiles) {
            p.drawSelf();
        }
    }

    /** Make alistair take damage
     * @param damage Health reduction, <=100
     * */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            System.out.println("we ded");
            AudioController.play("gameover");
            // TODO: add handling for game overs (SEGFAULTS!)
        }
    }
    
    /** Gets the tile data at a specific position. */
    public Tile getTile(float x, float y) {
        return tiles[toGrid(x)][toGrid(y)];
    }

    /** Converts from literal position to position on grid */
    public int toGrid(float pos) {
        // Choose closest grid position
        return Math.round((pos - tileSize / 2) / tileSize);
    }

    /** Converts from grid position to literal coordinates */
    public float toPos(int gridval) {
        return gridval * tileSize + tileSize / 2;
    }

    /** Check coordinate against game boundaries */
    public boolean inGridBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < gridW && y < gridH;
    }

    /** Returns a grid direction to point inwards from the current position */
    public int defaultDir(int gridval) {
        return gridval < 0 ? 1 : (gridval >= gridW ? -1 : 0);
    }

    /** Returns a literal direction to point inwards */
    public float defaultDir(float pos) {
        return pos < 0 ? 1 : (pos >= gridW * tileSize ? -1 : 0);
    }

    public boolean isPlacingTower() {
        return myTower != null;
    }
    
    /** Add a projectile to the list of monitored projectiles. */
    public void addProjectile(Projectile proj) {
        projectiles.add(proj);
    }
    
    /** Remove a projectile from the list. */
    public void removeProjectile(Projectile proj) {
        projectiles.remove(proj);
    }
    
    public int getWidth() { return w; }
    public int getHeight() { return h; }
    public int getGridWidth() { return gridW; }
    public int getGridHeight() { return gridH; }
    public int getTileSize() { return tileSize; }
    public int getPathXDir(int x, int y) { return path[x][y][0]; }
    public int getPathYDir(int x, int y) { return path[x][y][1]; }
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }
    public List<Projectile> getProjectiles() { return Collections.unmodifiableList(projectiles); }
    
    /** A data container for each tile on the map. */
    public static class Tile extends StaticEntity {
        private final boolean isWall;
        
        private Tile(float x, float y, int tileSize, Properties properties) {
            super(x, y, tileSize, tileSize);
            isWall = Boolean.valueOf(properties.getProperty("isWall"));
        }
        
        public boolean isWall() { return isWall; }
    }
}
