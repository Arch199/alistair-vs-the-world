package control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import game.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import ui.Button;
import ui.TextButton;
import ui.SpriteButton;
import ui.TextUI;

/** Handles all the game logic for a level. Created by App. */
public class World {
    private static final Font
            TINY_FONT = new Font("Verdana", Font.PLAIN, 11),
            SMALL_FONT = new Font("Verdana", Font.PLAIN, 15),
            MEDIUM_FONT = new Font("Verdana", Font.BOLD, 20),
            LARGE_FONT = new Font("Verdana", Font.BOLD, 40);
    public static final TrueTypeFont
            TINY_TTF = new TrueTypeFont(TINY_FONT, true),
            SMALL_TTF = new TrueTypeFont(SMALL_FONT, true),
            MEDIUM_TTF = new TrueTypeFont(MEDIUM_FONT, true),
            LARGE_TTF =  new TrueTypeFont(LARGE_FONT, true);

    private float startX, startY;
    private int health = 1, waveNum = 0, money = 100;
    private long timer = 0;
    private Tile alistair;
    private Tower
        myTower = null,       // Tower currently being placed
        selectedTower = null; // Placed tower that has been selected
    private boolean waveComplete = true, gameOver = false;

    private TiledMap map;
    private Tile[][] tiles;
    private TextUI textUI = new TextUI();
    private List<Wave> waves;
    private List<Projectile> projectiles = new LinkedList<>();
    private List<Tower> towers = new LinkedList<>();
    private List<Button> buttons = new ArrayList<>();
    /** Enemy path. 3D array for x-coordinate, y-coordinate and direction for enemy to move in. */
    private int[][][] path; // TODO: add a path object for better aiming at moving enemies?
    /** List of enemies in order of creation (oldest first). */
    private List<Enemy> enemies = new LinkedList<>();

    /** Creates the world.
     * @param map The tiled map to render.
     * @param waves Data on waves and enemy spawn timing.
     */
    World(TiledMap map, List<Wave> waves) {
        // Assert that the map has square tiles
        if (map.getTileWidth() != map.getTileHeight()) {
            throw new IllegalArgumentException("Tiled map must have square tiles");
        }

        this.map = map;
        this.waves = waves;

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
            throw new IllegalArgumentException("Tiled map must have Alistair on it (with his position given)", e);
        }

        // Extract the tile data into an array for easy access
        tiles = new Tile[map.getWidth()][map.getHeight()];
        TileSet kenneyTileSet = map.getTileSet(0); // TODO: refactor this hard-coding
        TileSet customTileSet = map.getTileSet(1);

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int id = map.getTileId(x, y, 0);
                if (id < 300) {
                    tiles[x][y] = new Tile(x, y, kenneyTileSet.getProperties(id));
                } else {
                    tiles[x][y] = new Tile(x, y, customTileSet.getProperties(id));
                }
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
        final int centerX = App.WINDOW_W - App.SIDEBAR_W / 2;
        var towerTypes = Tower.Type.values();
        for (i = 0; i < towerTypes.length; i++) {
            var t = towerTypes[i];
            int yPos = (i + 1) * 100;
            var s = new Sprite(centerX, yPos, t.getImage());
            var icon = new SpriteButton(s, () -> myTower = Tower.create(t, centerX, yPos));
            icon.setColors(Color.white, Color.red, Color.lightGray);
            icon.disableWhen(() -> money < t.getCost());
            buttons.add(icon);
            textUI.add(() -> String.valueOf(t.getCost()), centerX + s.getWidth(), yPos - s.getHeight() / 4, SMALL_TTF, TextUI.Mode.LEFT);
            textUI.add(t::toString, centerX, yPos + s.getWidth() / 2, TINY_TTF, TextUI.Mode.CENTER);
        }

        // New wave button
        TextButton nextWave = new TextButton(centerX, 500, "Next wave", MEDIUM_TTF, this::newWave);
        nextWave.setColors(Color.green, Color.black, Color.white);
        nextWave.setBorder(true);
        nextWave.disableWhen(() -> !waveComplete);
        buttons.add(nextWave);

        // Play intro sound
        AudioController.play("intro");
    }

    /** Deselect the item being carried */
    public void deselect() {
        myTower = null;
        selectedTower = null;
    }

    /** Update the world state.
     * @param delta Time in ms since the last update.
     */
    public void update(int delta) throws SlickException {
        timer += delta;

        // Enemy spawning (based on the current wave)
        if (waveNum > 0 && waveNum - 1 < waves.size()) {
            Wave w = waves.get(waveNum - 1);
            // TODO: Make this return an array to spawn multiple enemies in the same tick
            Enemy.Type enemyType = w.trySpawn(timer);
            if (enemyType != null) {
                spawnEnemy(startX, startY, enemyType);
                // Play enemy sounds
                if (enemyType == Enemy.Type.PYTHON || enemyType == Enemy.Type.COMMERCE) {
                    AudioController.play(enemyType.toString(), false);
                }
            }

            // Set wave status
            waveComplete = w.isFinished() && enemies.isEmpty();
        }

        // Tower counting down / shooting
        for (Tower t : towers) {
            t.update(delta);
        }

        processEnemies();
        processProjectiles();
        processTowers();
        processButtons();
    }

    /** Update enemy positions. */
    private void processEnemies() {
        Iterator<Enemy> itr = enemies.iterator();
        while (itr.hasNext()) {
            Enemy e = itr.next();
            // Hitting alistair
            e.advance(e.getSpeed());
            if (e.checkCollision(alistair)) {
                takeDamage(e.getDamage());
                itr.remove();
            }
        }
    }

    /** Update projectile positions. */
    private void processProjectiles() {
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
                        money++;
                    }
                    p.pop();
                    itr.remove();
                    break;
                }
            }
        }
    }

    /** Handle selecting and placing towers. */
    private void processTowers() {
        int mouseX = App.getMouseX(), mouseY = App.getMouseY();
        boolean leftClicked = App.isLeftClicked();

        // If we're placing a tower, move it to the mouse position
        if (myTower != null) {
            myTower.setColor(Color.white);
            myTower.teleport(mouseX, mouseY);

            // Set color to red if out of game bounds, or if it cannot be afforded
            if (!inGridBounds(toGrid(mouseX), toGrid(mouseY))) {
                myTower.setColor(Color.red);
                if (leftClicked) {
                    // Cancel the placement
                    deselect();
                    return;
                }
            } else {
                // Also set color to red if touching a non-wall tile or tower, or if the player has insufficient funds
                if (!getTile(mouseX, mouseY).holdsDefence || money < myTower.getType().getCost()) {
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
            if (leftClicked/* && myTower.getColor() == Color.white*/) {
                myTower.place(toPos(toGrid(mouseX)), toPos(toGrid(mouseY)));
                towers.add(myTower);
                money -= myTower.getType().getCost();

                // Play a sound effect
                AudioController.play(myTower.getType().toString(), false);

                deselect();
            }
        } else if (leftClicked) {
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
            deselect();
        }
    }

    /** Update button status. */
    private void processButtons() {
        for (Button b : buttons) {
            b.update();
        }
    }

    /** Render the game world. */
    public void render(Graphics g) {
        // Tiles, enemies, towers, and projectiles
        float scale = (float)App.TILE_SIZE / map.getTileWidth();
        g.scale(scale, scale);
        map.render(0, 0);
        g.scale(1 / scale, 1 / scale);
        enemies.forEach(Sprite::render);
        towers.forEach(Sprite::render);
        projectiles.forEach(Sprite::render);

        // GUI elements
        drawGUI(g);
        textUI.render(g);
    }

    /** Draw game interface. */
    private void drawGUI(Graphics g){
        // Sidebar
        g.setColor(Color.darkGray);
        g.fillRect(App.WINDOW_W - App.SIDEBAR_W, 0, App.SIDEBAR_W, App.WINDOW_H);

        // Tower being placed
        if (myTower != null) {
            myTower.render();
            myTower.drawRange(g);
        }

        // Selected tower draws its range
        if (selectedTower != null) {
            selectedTower.drawRange(g);
        }

        // Buttons
        g.setColor(Color.white);
        buttons.forEach(b -> b.render(g));

        // Display wave number and Alistair's health
        // TODO: move to TextUI
        g.setColor(Color.white);
        g.setFont(SMALL_TTF);
        Util.writeCentered(g, "Wave: " + waveNum, App.WINDOW_W - App.SIDEBAR_W / 2, 20);
        Util.writeCentered(g, "Money: " + money, App.WINDOW_W - App.SIDEBAR_W / 2, 40);
        g.setFont(MEDIUM_TTF);
        Util.writeCentered(g, Integer.toString(health), alistair.getX(), alistair.getY());

        // Tower being placed
        if (myTower != null) {
            myTower.render();
            myTower.drawRange(g);
        }

        if (selectedTower != null) {
            selectedTower.drawRange(g);
        }

        // Game over splash
        if (gameOver) {
            g.setColor(Color.red);
            Util.writeCentered(LARGE_TTF, "Game Over!", App.WINDOW_W / 2, App.WINDOW_H / 2);
            g.setColor(Color.white);
        }
    }

    /**
     * Make alistair take damage
     * @param damage Health reduction, <=100
     */
    public void takeDamage(int damage) {
        if (!gameOver) { // TODO: consider writing a damageable entity type and making alistair one of them
            if (health - damage < 0) { health = 0; } else { health -= damage; }
            if (health == 0) {
                endGame();
            }
        }
    }

    /** Create the end game splash. */
    public void endGame() {
        deselect();
        AudioController.play("gameover");
        gameOver = true;
    }

    /** Get the tile data at a specific position. */
    public Tile getTile(float x, float y) {
        return tiles[toGrid(x)][toGrid(y)];
    }

    /** Convert from literal position to position on grid. */
    public int toGrid(float pos) {
        // Choose closest grid position
        return Math.round((pos - App.TILE_SIZE / 2) / App.TILE_SIZE);
    }

    /** Convert from grid position to literal coordinates. */
    public float toPos(int gridVal) {
        return gridVal * App.TILE_SIZE + App.TILE_SIZE / 2;
    }

    /** Check grid coordinates against game boundaries. */
    public boolean inGridBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < map.getWidth() && y < map.getHeight();
    }

    /** Calculate a horizontal grid direction to point inwards from the current position. */
    public int inwardDirX(int gridX) {
        return gridX < 0 ? 1 : (gridX >= map.getWidth() ? -1 : 0);
    }

    /** Calculate a horizontal literal direction to point inwards. */
    public float inwardDirX(float posX) {
        return posX < 0 ? 1 : (posX >= map.getWidth() * App.TILE_SIZE ? -1 : 0);
    }

    /** Calculate a vertical grid direction to point inwards from the current position. */
    public int inwardDirY(int gridY) {
        return gridY < 0 ? 1 : (gridY >= map.getHeight() ? -1 : 0);
    }

    /** Calculate a vertical literal direction to point inwards. */
    public float inwardDirY(float posY) {
        return posY < 0 ? 1 : (posY >= map.getHeight() * App.TILE_SIZE ? -1 : 0);
    }

    /** Add a projectile to the list of monitored projectiles. */
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    /** Increment the wave number and reset the timer. Called every time a new wave starts. */
    private void newWave() {
        waveNum++;
        timer = 0;
        towers.forEach(Tower::waveReset);
    }

    /** Create a new enemy at the given position. */
    private void spawnEnemy(float x, float y, Enemy.Type type) throws SlickException {
        enemies.add(new Enemy(x, y, new Vector2f(inwardDirX(x), inwardDirY(y)), type));
    }

    public int getPathXDir(int x, int y) { return path[x][y][0]; }
    public int getPathYDir(int x, int y) { return path[x][y][1]; }
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }

    /** A data container for each tile on the map. */
    public class Tile extends StaticEntity {
        private boolean isWall, holdsDefence;

        private Tile(int gridX, int gridY, Properties properties) {
            super(toPos(gridX), toPos(gridY), App.TILE_SIZE, App.TILE_SIZE);
            try {
                isWall = Boolean.parseBoolean(properties.getProperty("isWall"));
                holdsDefence = Boolean.parseBoolean(properties.getProperty("holdsDefence"));
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.err.println("Could not parse the properties of the tile at " + gridX + " " + gridY + "" +
                                   "assumed a wall that cannot hold a defence.");
                isWall = true;
                holdsDefence = false;
            }
        }

        public boolean isWall() { return isWall; }
        public boolean holdsDefence() { return holdsDefence; }
    }
}
