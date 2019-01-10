// Main App handler for Alistair-themed Tower Defence Game

package alistair_game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.*;

public class App extends BasicGame {
    /**
     * Main handler for the game as a program. Creates a World to handle the
     * gameplay itself. Will later create a Menu first (when we have a main menu).
     */

    private static final int
    // OLD vals: 960/672/48/20/15
    WINDOW_W = 960, WINDOW_H = 672, TILE_SIZE = 48, GRID_W = WINDOW_W / TILE_SIZE, GRID_H = WINDOW_H / TILE_SIZE;

    private World world;

    public static void main(String[] args) {
        try {
            System.out.println("Yeet, starting main");

            App game = new App("Alistair vs The World");
            AppGameContainer appgc = new AppGameContainer(game);
            appgc.setDisplayMode(WINDOW_W, WINDOW_H, false);
            appgc.start();

            System.err.println("GAME STATE: Game forced exit");
        } catch (SlickException e) {
            // Log exception
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public App(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        System.out.println("GAME STATE: Initialising game...");
        gc.setShowFPS(false);

        // Game update speed. 1 tick every 20 ms (50/sec)
        gc.setMaximumLogicUpdateInterval(20);
        gc.setMinimumLogicUpdateInterval(20);

        // Initialise level from file and create world object
        try {
            // 2D grid array
            int[][] level = new int[GRID_W][GRID_H];

            // Load map info file
            Scanner scanner = new Scanner(new File("assets\\levels\\level1.txt"));
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
            Scanner scanner2 = new Scanner(new File("assets\\waves\\game1.txt"));
            // Read line-by-line
            scanner2.useDelimiter("[\\r\\n;]+");
            Object[][] waves;
            int wavenum = 1;

            while(scanner2.hasNext()) {
                String wave = scanner2.next();

                // Split into spawn sequences - enemytype/enemynum/spawnrate/starttime
                String[] spawnSequences = wave.split(" ");

                for(String seq: spawnSequences) {
                    // Split into in
                    String[] seqInfo = seq.split("/");
                    String enemy = seqInfo[0];
                    int enemyNum = Integer.parseInt(seqInfo[1]);
                    float spawnRate = Float.parseFloat(seqInfo[2]), spawnTime = Float.parseFloat(seqInfo[3]);
                }

                System.out.println("got a line");
                try {
                    System.out.println(spawnSequences[1]);
                } catch (Exception e) {
                }
                wavenum++;
            }


            world = new World(WINDOW_W, WINDOW_H, TILE_SIZE, startx, starty, level);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        // something about speed -- increment it given the delta I guess
        world.tick(delta);
        world.moveEnemies();
        world.moveProjectiles();

        Input input = gc.getInput();
        world.processTowers(input);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        // Draw all the sprites
        world.renderTiles();
        world.renderEnemies();
        world.renderTowers(g);
        world.renderProjectiles();

        world.drawGUI(g);
    }

    @Override
    public boolean closeRequested() {
        System.out.println("GAME STATE: Exiting game");
        System.exit(0);
        return false; // only here to placate the compiler
    }
}