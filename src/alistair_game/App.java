// Main App handler for Alistair-themed Tower Defence Game

package alistair_game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.*;

/**
 * Main handler for the game as a program.
 *
 * Creates a World to handle the gameplay itself.
 */
public class App extends BasicGame {
    private static final int
        WINDOW_W = 960, WINDOW_H = 672, TILE_SIZE = 48,
        GRID_W = WINDOW_W / TILE_SIZE, GRID_H = WINDOW_H / TILE_SIZE;
    
    private Menu menu = null;
    private World world = null;

    public static void main(String[] args) {
        try {
            App game = new App("Alistair vs The World");
            AppGameContainer appgc = new AppGameContainer(game);
            appgc.setDisplayMode(WINDOW_W, WINDOW_H, false);
            appgc.start();

            System.err.println("GAME STATE: Game forced exit");
        } catch (SlickException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public App(String title) {
        super(title);
    }

    /** Calls World to initialize all game objects before the game begins.
     * Sets game parameters and loads up files.
     */
    @Override
    public void init(GameContainer gc) throws SlickException {
        System.out.println("GAME STATE: Initialising game...");
        gc.setShowFPS(false);

        // Game update speed. 1 tick every 20 ms (50/sec)
        gc.setMaximumLogicUpdateInterval(20);
        gc.setMinimumLogicUpdateInterval(20);
        
        // Open Main Menu
        menu = new Menu(getTitle(), WINDOW_W, WINDOW_H);
    }

    /**
     * Should be called every 20ms. Executes a 'tick' operations.
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        if (menu != null) {
            String action = menu.update(input);
            if (action != null) {
                switch (action) {
                    case "Start":
                        openLevel("level1");
                        break;
                    case "Options":
                        // TODO: Add options (what settings would we have?) or just remove this
                        break;
                    case "Quit":
                        closeRequested();
                        break;
                }
            }
        }
        if (world != null) {
            world.tick(delta);
            world.moveEnemies();
            world.moveProjectiles();
            world.processTowers(input);
        }
    }

    /**
     * Responsible for drawing sprites. Called regularly automatically.
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (menu != null) {
            menu.renderTitle();
            menu.renderOptions();
        }
        if (world != null) {
            world.renderTiles();
            world.renderEnemies();
            world.renderTowers(g);
            world.renderProjectiles();
            world.drawGUI(g);
        }
    }
    
    /** Opens a new level and creates a World to manage it.
     * Also minimises the current menu and changes focus to the level.
     */
    private void openLevel(String levelName) {
        try {
            // 2D grid array
            int[][] level = new int[GRID_W][GRID_H];

            // Load map info file
            Scanner scanner = new Scanner(new File("assets\\levels\\" + levelName + ".txt"));
            for (int y = 0; y < GRID_H; y++) {
                assert (scanner.hasNext());
                char[] line = scanner.next().toCharArray();
                int x = 0;
                for (char c : line) {
                    if (x >= GRID_W)
                        break;
                    assert (Character.isDigit(c));
                    level[x++][y] = Character.getNumericValue(c);
                }
            }

            // Enemy spawn location
            float startx = (float) scanner.nextInt() * TILE_SIZE + TILE_SIZE / 2;
            float starty = (float) scanner.nextInt() * TILE_SIZE + TILE_SIZE / 2;
            scanner.close();


            // Load in wave info
            scanner = new Scanner(new File("assets\\waves\\game1.txt"));
            // Read line-by-line
            scanner.useDelimiter("[\\r\\n;]+");

            ArrayList<Wave> waves = new ArrayList<>();
            int wavenum = 1;

            // Wave-by-wave
            while (scanner.hasNext()) {
                String wave = scanner.next();
                Wave currWave = new Wave(wavenum);
                waves.add(currWave);

                // Split into spawn sequences - enemytype/enemynum/spawnrate/starttime
                String[] spawnSequences = wave.split(" ");
                int seqs = spawnSequences.length;

                for (int i = seqs-1; i >= 0; i--) {
                    String seq = spawnSequences[i];

                    // Extract info
                    String[] seqInfo = seq.split("/");
                    String enemy = seqInfo[0];
                    int enemyNum = Integer.parseInt(seqInfo[1]);
                    float spawnRate = Float.parseFloat(seqInfo[2]), spawnTime = Float.parseFloat(seqInfo[3]);

                    // Generate and add spawn individual instructions
                    for (int j = enemyNum; j >= 1; j--) {
                        currWave.addInstruction(new SpawnInstruction(enemy, spawnTime*1000));
                        spawnTime += spawnRate;
                    }
                }
                wavenum++;
            }
            scanner.close();
            
            // Create World
            world = new World(WINDOW_W, WINDOW_H, TILE_SIZE, startx, starty, level, waves);
            
            // Get rid of menu
            // TODO: there's probably a better way to do this
            menu = null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the game.
     */
    @Override
    public boolean closeRequested() {
        System.out.println("GAME STATE: Exiting game");
        System.exit(0);
        return false; // only here to placate the compiler
    }
}