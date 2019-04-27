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

    private int w, h, tileSize, sidebarW;
    private float startX, startY;
    private int health = 100, waveNum = 0;
    private long timer = 0;
    private Tile alistair;
    private Tower myTower = null,       // Tower currently being placed
                  selectedTower = null; // Placed tower that has been selected
    private boolean waveComplete = true;
    private Button nextWave;
    
    private TiledMap map;
    private Tile[][] tiles;
    private List<Wave> waves;
    private List<Projectile> projectiles = new LinkedList<>();
    private List<Tower> towers = new LinkedList<>();
    private List<TextSprite> sidebarIcons = new ArrayList<TextSprite>();
    private List<Button> buttons = new ArrayList<Button>();
    /** Enemy path. 3D array for x-coord, y-coord and direction for enemy to move in. */
    private int[][][] path;
    /** List of enemies in order of creation (oldest first). */
    private List<Enemy> enemies = new LinkedList<>();

    /**
     * Creates the world.
     * @param w Map width
     * @param h Map height
     * @param map The tiled map to render
     * @param waves Data on waves and enemy spawn timing
     */
    public World(int w, int h, int sidebarW, TiledMap map, List<Wave> waves) {
        // Assert that the map has square tiles
        if (map.getTileWidth() != map.getTileHeight()) {
            throw new IllegalArgumentException("Tiled map must have square tiles");
        }
        this.tileSize = map.getTileWidth();
        
        this.w = w;
        this.h = h;
        this.waves = waves;
        this.sidebarW = sidebarW;
        this.map = map;
        
        // Get the enemy spawn location
        String[] pos = map.getMapProperty("startPos", "").split(",");
        try {
            startX = toPos(Integer.parseInt(pos[0]));
            startY = toPos(Integer.parseInt(pos[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Tiled map must have an enemy starting position");
        }
        
        // Get Alistair's location
        String[] alistairPos = map.getMapProperty("alistairPos", "").split(",");
        int alistairX, alistairY;
        try {
            alistairX = Integer.parseInt(alistairPos[0]);
            alistairY = Integer.parseInt(alistairPos[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Tiled map must have Alistair on it (with his position given)");
        }
        
        // Extract the tile data into an array for easy access
        tiles = new Tile[map.getWidth()][map.getHeight()];
        TileSet tileSet = map.getTileSet(0);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int id = map.getTileId(x, y, 0);
                tiles[x][y] = new Tile(x, y, tileSet.getProperties(id));
            }
        }
        alistair = tiles[alistairX][alistairY];

        // Traverse the enemy path and store direction values in a grid
        path = new int[map.getWidth()][map.getHeight()][2];
        int x = toGrid(startX), y = toGrid(startY);
        int i = inwardDirX(x);
        int j = inwardDirY(y);
        if (i == 0 && j == 0) {
            throw new IllegalArgumentException("Starting position must be outside grid");
        }
        while (!inGridBounds(x, y)) {
            x += i;
            y += j;
        }
        while (x != alistairX || y != alistairY) {
            // Move along the path
            // Check if we've hit a wall yet
            if (!inGridBounds(x + i, y + j) || tiles[x + i][y + j].isWall()) {
                // OK, try turning left (anti-clockwise)
                int old_i = i;
                i = j;
                j = -old_i;

                // Check again
                if (!inGridBounds(x + i, y + j) || tiles[x + i][y + j].isWall()) {
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
     * Deal with user input e.g. pressing Esc to return to main menu.
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
     * Keep track of the time (in ms) from the start of the wave.
     * Spawn enemies and projectiles accordingly.
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
        Vector2f v = new Vector2f(inwardDirX(x), inwardDirY(y));
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

    /** Handle selecting / placing towers. */
    public void processTowers(int mouseX, int mouseY, boolean clicked) throws SlickException {
        // If we're placing a tower, move it to the mouse position
        if (isPlacingTower()) {
            myTower.setColor(Color.white);
            myTower.teleport(mouseX, mouseY);

            // Set color to red if out of game bounds
            if (!inGridBounds(toGrid(mouseX), toGrid(mouseY))) {
                myTower.setColor(Color.red);
                if (clicked) {
                    myTower = null;
                    return;
                }
            } else {
                // Also set color to red if touching a non-wall tile or tower
                if (getTile(mouseX, mouseY).isWall()) {
                    myTower.setColor(Color.red);
                    return;
                }
                
                for (Tower t : towers) {
                    if (t.checkCollision(myTower)) {
                        myTower.setColor(Color.red);
                        return;
                    }
                }
            }
                
            // If the user clicked and it's not colliding with anything, place it
            if (clicked/* && myTower.getColor() == Color.white*/) {
                myTower.place(toPos(toGrid(mouseX)), toPos(toGrid(mouseY)));
                towers.add(myTower);
                myTower = null;
            }
        } else if (clicked) {            
            // Click on a tower to display its range
            for (Tower t: towers) {
                if (t.contains(mouseX, mouseY) && t != myTower) {
                    if (t == selectedTower) {
                        selectedTower = null;
                    } else {
                        selectedTower = t;
                    }
                    return;
                }
            }
            
            // Deselect a selected tower
            selectedTower = null;
                 
            // Process selecting towers from the sidebar
            for (TextSprite s : sidebarIcons) {
                if (s.contains(mouseX, mouseY)) {
                    myTower = Tower.create(Tower.Type.fromTitle(s.getText()), mouseX, mouseY, this);
                    return;
                }
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
    
    /** Get the tile data at a specific position. */
    public Tile getTile(float x, float y) {
        return tiles[toGrid(x)][toGrid(y)];
    }
    
    /** Convert from literal position to position on grid. */
    public int toGrid(float pos) {
        // Choose closest grid position
        return Math.round((pos - tileSize / 2) / tileSize);
    }

    /** Convert from grid position to literal coordinates. */
    public float toPos(int gridval) {
        return gridval * tileSize + tileSize / 2;
    }

    /** Check grid coordinates against game boundaries. */
    public boolean inGridBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < map.getWidth() && y < map.getHeight();
    }
    
    /** Check literal coordinates against game boundaries. */
    public boolean inGridBounds(float x, float y) {
        return x >= 0 && y >= 0 && x < map.getWidth() * tileSize && y < map.getHeight() * tileSize;
    }

    /** Calculate a horizontal grid direction to point inwards from the current position. */
    public int inwardDirX(int gridX) {
        return gridX < 0 ? 1 : (gridX >= map.getWidth() ? -1 : 0);
    }
    
    /** Calculate a vertical grid direction to point inwards from the current position. */
    public int inwardDirY(int gridY) {
        return gridY < 0 ? 1 : (gridY >= map.getHeight() ? -1 : 0);
    }

    /** Calculate a horizontal literal direction to point inwards */
    public float inwardDirX(float posX) {
        return posX < 0 ? 1 : (posX >= map.getWidth() * tileSize ? -1 : 0);
    }
    
    /** Calculate a vertical literal direction to point inwards */
    public float inwardDirY(float posY) {
        return posY < 0 ? 1 : (posY >= map.getHeight() * tileSize ? -1 : 0);
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
    public int getGridWidth() { return map.getWidth(); }
    public int getGridHeight() { return map.getHeight(); }
    public int getTileSize() { return tileSize; }
    public int getPathXDir(int x, int y) { return path[x][y][0]; }
    public int getPathYDir(int x, int y) { return path[x][y][1]; }
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }
    public List<Projectile> getProjectiles() { return Collections.unmodifiableList(projectiles); }
    
    /** A data container for each tile on the map. */
    public class Tile extends StaticEntity {
        private final boolean isWall;
        
        private Tile(int gridX, int gridY, Properties properties) {
            super(toPos(gridX) + tileSize / 2, toPos(gridY) + tileSize / 2, tileSize, tileSize);
            isWall = Boolean.parseBoolean(properties.getProperty("isWall"));
        }
        
        public boolean isWall() { return isWall; }
    }
}
