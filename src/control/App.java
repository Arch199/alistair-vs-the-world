// Main App handler for Alistair-themed Tower Defence Game

package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Main handler for the game as a program.
 * Creates a World to handle the gameplay itself.
 */
public final class App extends BasicGame {
    public static final int TILE_SIZE = 48, WINDOW_W = 1488, WINDOW_H = 1008;
    static final int SIDEBAR_W = TILE_SIZE*3;
    private static Menu menu;
    private static World world;
    private static Input input;
    private static Boolean leftClicked, rightClicked;
    private static Map<Integer,Boolean> keyPresses = new HashMap<>();

    public static void main(String[] args) throws SlickException {
        App instance = new App("Alistair vs The World");
        AppGameContainer container = new AppGameContainer(instance);
        container.setDisplayMode(WINDOW_W, WINDOW_H, false);
        container.start();
    }

    private App(String title) {
        super(title);
    }

    /** Sets game parameters, loads up files, and starts the menu. */
    @Override
    public void init(GameContainer gc) {
        System.out.println("GAME STATE: Initialising game...");
        gc.setShowFPS(false);

        // Game update speed. 1 tick every 20 ms (50/sec)
        // TODO: consider un-setting this and adding delta calculations to DynamicSprite, etc.
        gc.setMaximumLogicUpdateInterval(20);
        gc.setMinimumLogicUpdateInterval(20);

        // Set input
        input = gc.getInput();

        // Open Main Menu
        menu = new Menu(getTitle(), WINDOW_W, WINDOW_H);
    }

    /** Should be called every 20ms. Executes a 'tick' operation. */
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        input = gc.getInput();
        leftClicked = rightClicked = null;
        keyPresses = new HashMap<>();

        if (menu != null) {
            menu.update();
        } else if (world != null) {
            if (isKeyPressed(Input.KEY_ESCAPE)) {
                AudioController.stopAll();
                world = null;
                menu = new Menu(getTitle(), WINDOW_W, WINDOW_H);
            } else {
                if (isRightClicked()) {
                    world.deselect(); // may want to move this
                }
                world.update(delta);
            }
        }
    }

    /** Responsible for drawing sprites. Called regularly, automatically. */
    @Override
    public void render(GameContainer gc, Graphics g) {
        if (menu != null) {
            menu.render(g);
        }
        if (world != null) {
            world.render(g);
        }
    }

    static void openLevel() {
        openLevel("fourbythree2");
    }

    /** Opens a new level and creates a World to manage it.
     * Also minimises the current menu and changes focus to the level.
     */
    static void openLevel(String levelName) {
        // Initialize the tiled map for the level
        TiledMap tiledMap = null;
        try {
            tiledMap = new TiledMap("assets/levels/" + levelName + ".tmx");
        } catch (SlickException e) {
            e.printStackTrace();
            exit();
        }
        
        // Load in wave info
        try (Scanner scanner = new Scanner(new File("assets/waves/game1.txt"))) {            
            // Read line-by-line
            scanner.useDelimiter("[\\r\\n;]+");

            ArrayList<Wave> waves = new ArrayList<>();

            // Wave-by-wave
            while (scanner.hasNext()) {
                String wave = scanner.next();
                Wave currWave = new Wave();
                waves.add(currWave);

                // Split into spawn sequences (format is enemyType/enemyNum/spawnRate/startTime)
                String[] spawnSequences = wave.split(" ");
                for (int i = spawnSequences.length - 1; i >= 0; i--) {
                    String seq = spawnSequences[i];

                    // Extract info
                    String[] seqInfo = seq.split("/");
                    String enemy = seqInfo[0];
                    int enemyNum = Integer.parseInt(seqInfo[1]);
                    float spawnRate = Float.parseFloat(seqInfo[2]), spawnTime = Float.parseFloat(seqInfo[3]);

                    // Generate and add spawn individual instructions
                    for (int j = enemyNum; j >= 1; j--) {
                        currWave.addInstruction(enemy, spawnTime*1000);
                        spawnTime += spawnRate;
                    }
                }
            }

            // Create World and get rid of Menu
            world = new World(tiledMap, waves);
            menu = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Exit the game. */
    static void exit() {
        System.out.println("GAME STATE: Exiting game");
        System.exit(0);
    }

    public static World getWorld() { return world; }

    // TODO: figure out if there should be a SCALE_FACTOR here (no, I think?)
    // Don't really understand how the scaling works: the main menu works without the factor, but the world breaks completely with it
    public static int getMouseX() { return input.getMouseX(); }
    public static int getMouseY() { return input.getMouseY(); }
    public static boolean isLeftClicked() {
        if (leftClicked == null) leftClicked = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
        return leftClicked;
    }
    public static boolean isRightClicked() {
        if (rightClicked == null) rightClicked = input.isMousePressed(Input.MOUSE_RIGHT_BUTTON);
        return rightClicked;
    }
    public static boolean isKeyPressed(int code) {
        if (!keyPresses.containsKey(code)) keyPresses.put(code, input.isKeyPressed(code));
        return keyPresses.get(code);
    }
}