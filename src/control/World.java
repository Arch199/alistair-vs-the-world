package control;

import game.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import ui.SpriteButton;
import ui.TextButton;
import ui.TextUI;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

/** Handles all the game logic for a level. Created by App. */
public class World {
    private static final Font
        TINY_FONT = new Font("Verdana", Font.PLAIN, 11),
        SMALL_FONT = new Font("Verdana", Font.PLAIN, 15),
        MEDIUM_FONT = new Font("Verdana", Font.BOLD, 20),
        LARGE_FONT = new Font("Verdana", Font.BOLD, 40),
        LARGE_FONT_MONO = new Font("Courier", Font.BOLD, 40);
    private static final TrueTypeFont
        TINY_TTF = new TrueTypeFont(TINY_FONT, true),
        SMALL_TTF = new TrueTypeFont(SMALL_FONT, true),
        MEDIUM_TTF = new TrueTypeFont(MEDIUM_FONT, true),
        LARGE_TTF =  new TrueTypeFont(LARGE_FONT, true),
        LARGE_TTF_MONO = new TrueTypeFont(LARGE_FONT_MONO, true);

    private float startX, startY;
    private int health = 1, waveNum = 0, money = 100;
    private long timer = 0;
    private Tile alistair;
    private Tower selectedTower = null;
    private boolean waveComplete = true, gameOver = false;

    private TiledMap map;
    private Tile[][] tiles;
    private TextUI textUI = new TextUI();
    private List<Wave> waves;
    private List<Entity> entities = new LinkedList<>(), entityBuffer = new ArrayList<>();

    /** Create the world.
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

        // Link up the tiles along the path
        int x = toGrid(startX), y = toGrid(startY);
        Vector2f dir = inwardDir(x, y);
        int i = (int)dir.x, j = (int)dir.y;
        if (i == 0 && j == 0) {
            throw new IllegalArgumentException("Starting position must be outside grid");
        }
        while (!inGridBounds(x, y)) {
            x += i;
            y += j;
        }
        BiFunction<Integer,Integer,Boolean> hitWall = (x1, y1) -> !inGridBounds(x1, y1) || tiles[x1][y1].isWall();
        while (x != alistairX || y != alistairY) {
            // Move along the path
            // Check if we've hit a wall yet
            if (hitWall.apply(x + i, y + j)) {
                // OK, try turning left (anti-clockwise)
                int old_i = i;
                i = j;
                j = -old_i;

                // Check again
                if (hitWall.apply(x + i, y + j)) {
                    // Failed, turn right then (need to do a 180)
                    i = -i;
                    j = -j;
                }
            }
            // Update x, y and our direction
            tiles[x][y].next = tiles[x + i][y + j];
            x += i;
            y += j;
        }

        // Create sidebar icons for buying towers
        // TODO: consider delegating this to another object e.g. TextUI
        final int centerX = App.WINDOW_W - App.SIDEBAR_W / 2;
        var towerTypes = Tower.Type.values();
        for (i = 0; i < towerTypes.length; i++) {
            var t = towerTypes[i];
            int yPos = (i + 1) * 100;
            var s = Tower.create(t, centerX, yPos);
            var icon = new SpriteButton(s, () -> selectedTower = Tower.create(t, centerX, yPos));
            icon.setColors(Color.white, Color.red, Color.lightGray);
            icon.disableWhen(() -> money < t.getCost());
            entities.add(icon);
            textUI.add(() -> String.valueOf(t.getCost()), centerX + s.getWidth(), yPos - s.getHeight() / 3,
                SMALL_TTF, TextUI.Mode.LEFT);
            textUI.add(t::toString, centerX, yPos + s.getWidth() / 2, TINY_TTF, TextUI.Mode.CENTER);
        }

        // Display wave number, money, and Alistair's health
        textUI.add(() -> "Wave: " + waveNum, App.WINDOW_W - App.SIDEBAR_W / 2, 20, SMALL_TTF, TextUI.Mode.CENTER);
        textUI.add(() -> "Money: " + money, App.WINDOW_W - App.SIDEBAR_W / 2, 40, SMALL_TTF, TextUI.Mode.CENTER);
        textUI.add(() -> Integer.toString(health), alistair, MEDIUM_TTF, TextUI.Mode.CENTER);

        // New wave button
        TextButton nextWave = new TextButton(centerX, 500, "Next wave", MEDIUM_TTF, this::newWave);
        nextWave.setColors(Color.green, Color.black, Color.white);
        nextWave.setBorder(true);
        nextWave.disableWhen(() -> !waveComplete);
        entities.add(nextWave);

        // Play intro sound
        AudioController.play("intro");
    }

    /** Update the world state.
     * @param delta Time in ms since the last update.
     */
    public void update(int delta) {
        timer += delta;
        entityBuffer.clear();

        // Update all entities and delete dead ones
        // TODO: check for bugs involving entities not being destroyed until all entities have been updated
        entities.forEach(e -> e.update(delta));
        entities.removeIf(Entity::isDead);

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
            waveComplete = w.isFinished() && entities.stream().noneMatch(e -> e instanceof Enemy);
        }

        // Process towers
        int mouseX = App.getMouseX(), mouseY = App.getMouseY();
        boolean leftClicked = App.isLeftClicked();

        if (App.isRightClicked()) {
            selectedTower = null;
        } else if (selectedTower != null && !selectedTower.isPlaced()) {
            // Handle placing a tower
            // TODO: consider refactoring tower placing to creating a kind of SpriteButton
            selectedTower.teleport(mouseX, mouseY);
            if (!inGridBounds(toGrid(mouseX), toGrid(mouseY))
                || !getTile(mouseX, mouseY).canBuild || money < selectedTower.getType().getCost()
                || entities.stream().anyMatch(e -> e instanceof Tower && e.checkCollision(selectedTower))) {
                if (leftClicked && selectedTower.getColor() == Color.red) {
                    selectedTower = null;
                } else {
                    selectedTower.setColor(Color.red);
                }
            } else {
                selectedTower.setColor(Color.white);
                if (leftClicked) {
                    // Since the user clicked and the tower isn't colliding with anything, place it
                    selectedTower.place(toPos(toGrid(mouseX)), toPos(toGrid(mouseY)));
                    entities.add(selectedTower);
                    money -= selectedTower.getType().getCost();
                    AudioController.play(selectedTower.getType().toString(), false);
                    selectedTower = null;
                }
            }
        } else if (leftClicked) {
            // Click on a tower to display its range
            getAll(Tower.class).filter(t -> t.contains(mouseX, mouseY)).findAny().ifPresentOrElse(t -> {
                if (t == selectedTower) {
                    selectedTower = null;
                } else {
                    selectedTower = t;
                }
            }, () -> selectedTower = null);
        }

        // Add any created enemies from the buffer
        entities.addAll(entityBuffer);
    }

    /** Render the game world. */
    public void render(Graphics g) {
        // Render the map, scaled to fit the tile size
        float scale = (float)App.TILE_SIZE / map.getTileWidth();
        g.scale(scale, scale);
        map.render(0, 0);
        g.scale(1 / scale, 1 / scale);

        // Render the sidebar
        g.setColor(Color.darkGray);
        g.fillRect(App.WINDOW_W - App.SIDEBAR_W, 0, App.SIDEBAR_W, App.WINDOW_H);

        // Render each entity
        entities.forEach(e -> e.render(g));

        // Selected tower draws its range, and if being held, has to be rendered separately
        if (selectedTower != null) {
            if (!selectedTower.isPlaced()) {
                selectedTower.render(g);
            }
            selectedTower.drawRange(g);
        }

        // Game over splash
        if (gameOver) {
            // TODO: replace with going to another screen or something (also use TextUI for this)
            g.setColor(Color.red);
            Util.writeCentered(LARGE_TTF_MONO, "Segmentation fault (core dumped)", App.WINDOW_W / 2, App.WINDOW_H / 2);
            g.setColor(Color.white);
        }

        // Any remaining UI text, to be written at the top
        textUI.render(g);
    }

    /** Deal damage to alistair.
     * @param damage Health reduction
     */
    public void damageAlistair(int damage) {
        if (!gameOver) { // TODO: consider writing a damageable entity type and making alistair one of them
            if (health - damage < 0) { health = 0; } else { health -= damage; }
            if (health == 0) {
                endGame();
            }
        }
    }

    /** Get the unit direction to move in along the path. */
    public Vector2f pathDir(int gridX, int gridY) {
        Tile current = tiles[gridX][gridY], next = current.getNext();
        return new Vector2f(next.getX() - current.getX(), next.getY() - current.getY()).normalise();
    }

    /** Convert from literal position to position on grid, choosing the closest grid position. */
    public int toGrid(float pos) {
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

    /** Calculate a unit grid direction to point inwards from the current position. */
    public Vector2f inwardDir(int gridX, int gridY) {
        return new Vector2f(
            gridX < 0 ? 1 : (gridX >= map.getWidth() ? -1 : 0),
            gridY < 0 ? 1 : (gridY >= map.getHeight() ? -1 : 0)
        );
    }

    /** Add an entity to the monitored list. */
    public void addEntity(Entity e) { entityBuffer.add(e); }

    /** Increment the wave number and reset the timer. Called every time a new wave starts. */
    private void newWave() {
        waveNum++;
        timer = 0;
        getAll(Tower.class).forEach(Tower::waveReset);
    }

    /** Create the end game splash. */
    private void endGame() {
        selectedTower = null;
        AudioController.play("gameover");
        gameOver = true;
    }

    /** Create a new enemy at the given position. */
    private void spawnEnemy(float x, float y, Enemy.Type type) {
        entities.add(new Enemy(x, y, inwardDir(toGrid(x), toGrid(y)), type));
    }

    /** Get the tile data at a specific literal position. */
    public Tile getTile(float x, float y) {
        return tiles[toGrid(x)][toGrid(y)];
    }

    public Tile getAlistair() { return alistair; }
    public <T extends Entity> Stream<T> getAll(Class<T> type) {
        return entities.stream().filter(type::isInstance).map(type::cast);
    }

    public void addMoney(int amount) { money += amount; }

    /** A data container for each tile on the map. */
    public class Tile extends StaticEntity {
        private final boolean wall, canBuild;
        private Tile next;

        private Tile(int gridX, int gridY, Properties properties) {
            super(toPos(gridX), toPos(gridY), App.TILE_SIZE, App.TILE_SIZE);
            boolean wall, canBuild;
            try {
                wall = Boolean.parseBoolean(properties.getProperty("isWall"));
                canBuild = Boolean.parseBoolean(properties.getProperty("canBuild"));
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.err.println("Could not parse the properties of the tile at " + gridX + " " + gridY + "" +
                                   "assumed a wall that cannot hold a defence.");
                wall = true;
                canBuild = false;
            }
            this.wall = wall;
            this.canBuild = canBuild;
        }

        public boolean isWall() { return wall; }
        public boolean canBuild() { return canBuild; }
        public Tile getNext() { return next; }
    }
}
