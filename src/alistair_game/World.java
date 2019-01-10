package alistair_game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

class World {
    /**
     * Handles all the game logic for a level. Created by App.
     */

    private int w, h, tSize, gridW, gridH;
    private float startx, starty, enemy_speed = 1f;
    private Tile[][] tiles;
    private Object[][] waves;
    private int[][][] path; // Directions to move for each tile
    private LinkedList<Enemy> enemies = new LinkedList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private int spawnTime = 2000, nextSpawn = spawnTime, health = 100, waveNum = 1;
    private long timer = 0;
    private Tile alistair;
    private Tower myTower; // Tower currently being placed

    private static Image[] tileset;
    private static String[] tile_names;
    private static final int ALISTAIR_INDEX = 2;
    static {
        // Initialise tileset and tile names
        // Meaning of integers in level file
        tile_names = new String[3]; // TODO: add all this to a file (?)
        tile_names[0] = "wall";
        tile_names[1] = "path";
        tile_names[2] = "alistair";

        // Assign ints to images
        tileset = new Image[tile_names.length];
        try {
            for (int i = 0; i < tileset.length; i++) {
                tileset[i] = new Image("assets\\sprites\\tiles\\" + tile_names[i] + ".png");
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    World(int w, int h, int tSize, float startx, float starty, int[][] level) {
        this.w = w;
        this.h = h;
        this.tSize = tSize;
        this.gridW = w / tSize;
        this.gridH = h / tSize;
        this.startx = startx;
        this.starty = starty;

        // Initialise tile sprites from level + tileset
        tiles = new Tile[gridW][gridH];
        for (int x = 0; x < level.length; x++) {
            for (int y = 0; y < level[x].length; y++) {
                int i = level[x][y];
                tiles[x][y] = new Tile((x + 0.5f) * tSize, (y + 0.5f) * tSize, tileset[i], tile_names[i]);
                if (i == ALISTAIR_INDEX)
                    alistair = tiles[x][y];
            }
        }

        // Traverse the path and store direction values in a grid
        path = new int[gridW][gridH][2];
        int x = toGrid(startx), y = toGrid(starty);
        int i = defaultDir(x);
        int j = defaultDir(y);
        while (x < 0 || x >= gridW || y < 0 || y >= gridH) {
            x += i;
            y += j;
        }
        while (toPos(x) != alistair.getX() || toPos(y) != alistair.getY()) {
            // Move along the path
            // Check if we've hit a wall yet
            if (x + i < 0 || x + i >= gridW || y + j < 0 || y + j >= gridH || tiles[x + i][y + j].isWall()) {
                // OK, try turning left (anti-clockwise)
                int old_i = i;
                i = j;
                j = -old_i;

                // Check again
                if (tiles[x + i][y + j].isWall()) {
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

        // Tower in hand to start TODO: Add a sidebar?
        newTower(w / 2, h / 2);

        // Intro sound
        AudioController.play("intro");
    }

    /**
     * Keeps track of the time (in ms) from the start of the wave. Spawns enemies
     * and projectiles accordingly.
     */
    void tick(int delta) {
        timer += delta;

        // Enemy spawning
        /*
        while (timer >= next_spawn) {
            next_spawn += spawn_time;
            spawnEnemy(startx, starty);
        }*/

        for (int i = 0; waves[waveNum][i] != null; i++) {
            SpawnInstruction instruction = (SpawnInstruction) waves[waveNum][i];
            if (timer == instruction.getSpawnTime()) {
                spawnEnemy(startx, starty);
            }
        }

        // TODO: increase speed and decrease spawn time over time (or per wave?)

        // Tower shots
        for (Tower t : towers) {
            if (t.isPlaced() && t.countDown(delta)) {
                t.shoot(this);
            }
        }
    }

    /** Currently unused. Call every time a new wave starts */
    void newWave() {
        timer = 0;
        for (Tower t : towers) {
            if (t.isPlaced()) {
                t.waveReset();
            }
        }
    }

    void spawnEnemy(float x, float y) {
        Vector2f vec = new Vector2f(defaultDir(x), defaultDir(y)).scale(enemy_speed);
        enemies.add(new EnemyPython(x, y, vec));
    }

    void moveEnemies() {
        Iterator<Enemy> itr = enemies.iterator();
        while (itr.hasNext()) {
            Enemy e = itr.next();
            e.advance(enemy_speed, this);
            if (e.checkCollision(alistair)) {
                takeDamage(e.getDamage());
                itr.remove();
            }
        }
    }

    void moveProjectiles() {
        Iterator<Projectile> itr = projectiles.iterator();
        while (itr.hasNext()) {
            Projectile p = itr.next();
            p.advance();
            if (p.isOffScreen(w, h)) {
                itr.remove();
            }

            // Hitting enemies
            Iterator<Enemy> eItr = enemies.iterator();
            while (eItr.hasNext()) {
                Enemy e = eItr.next();
                if (p.checkCollision(e)) {
                    e.takeDamage(p.getDamage(), eItr);
                    itr.remove();
                    break;
                }
            }
        }
    }

    /** Handles placing towers */
    void processTowers(Input input) {
        int mousex = input.getMouseX(), mousey = input.getMouseY();
        boolean clicked = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);

        // If we're placing a tower, move it to the mouse position
        if (isPlacingTower()) {
            myTower.teleport((float) mousex, (float) mousey);

            // Set the tower to be red if it's touching a non-wall tile or tower
            myTower.setColor(Color.white);
            outer:
            for (Tile[] column : tiles) {
                for (Tile tile : column) {
                    if (!tile.isWall() && tile.checkCollision(myTower)) {
                        myTower.setColor(Color.red);
                        break outer;
                    }
                }
            }
            outer:
            for (Tower t : towers) {
                if (t.isPlaced() && t.checkCollision(myTower)) {
                    myTower.setColor(Color.red);
                    break outer;
                }
            }
                
            // If the user clicked and it's not colliding with anything, place it
            if (clicked && myTower.getColor() == Color.white) {
                myTower.place(toPos(toGrid(mousex)), toPos(toGrid(mousey)));
                myTower = null;
                newTower(mousex, mousey);
            }
        }
    }

    void newTower(float xpos, float ypos) {
        try {
            myTower = new Tower(xpos, ypos, new Image("assets\\sprites\\alistair32.png"), 3000);
            towers.add(myTower);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    void drawGUI(Graphics g) {
        // Display Alistair's health
        Util.writeCentered(g, Integer.toString(health), alistair.getX(), alistair.getY());
    }

    void renderTiles() {
        for (Tile[] column : tiles) {
            for (Tile t : column) {
                t.drawSelf();
            }
        }
    }

    void renderEnemies() {
        for (Enemy e : enemies) {
            e.drawSelf();
        }
    }

    void renderTowers(Graphics g) {
        for (Tower t : towers) {
            t.drawSelf();
            if (!t.isPlaced()) {
                // In hand
                t.drawRange(g);
            }
        }
    }

    void renderProjectiles() {
        for (Projectile p : projectiles) {
            p.drawSelf();
        }
    }

    void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            System.out.println("we ded");
            AudioController.play("gameover");
            // TODO: add handling for game overs (SEGFAULTS!)
        }
    }

    /** Converts from literal position to position on grid */
    int toGrid(float pos) {
        // Choose closest grid position
        return Math.round((pos - tSize / 2) / tSize);
    }

    /** Converts from grid position to literal coordinates */
    float toPos(int gridval) {
        return gridval * tSize + tSize / 2;
    }

    boolean inGridBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < gridW && y < gridH;
    }

    /** Returns a grid direction to point inwards from the current position */
    int defaultDir(int gridval) {
        return gridval < 0 ? 1 : (gridval >= gridW ? -1 : 0);
    }

    /** Returns a literal direction to point inwards */
    float defaultDir(float pos) {
        return pos < 0 ? 1 : (pos >= gridW * tSize ? -1 : 0);
    }

    boolean isPlacingTower() {
        return myTower != null;
    }
    
    void newProjectile(float x, float y, Vector2f vec, Image im) {
        projectiles.add(new Projectile(x, y, vec, im));
    }

    void setWaves(Object[][] waves) {
        this.waves = waves;
    }

    int getGridWidth() { return gridW; }
    int getGridHeight() { return gridH; }
    int getTileSize() { return tSize; }
    Tile getTile(int x, int y) { return tiles[x][y]; }
    int getPathXDir(int x, int y) { return path[x][y][0]; }
    int getPathYDir(int x, int y) { return path[x][y][1]; }
    List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }
    List<Projectile> getProjectiles() { return Collections.unmodifiableList(projectiles); }
}
