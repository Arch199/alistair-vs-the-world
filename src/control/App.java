// Main App handler for Alistair-themed Tower Defence Game

package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.*;
import org.newdawn.slick.tiled.TiledMap;

import ui.Menu;

/**
 * Main handler for the game as a program.
 * Creates a World to handle the gameplay itself.
 */
public class App extends BasicGame {    
    public static final int
        WINDOW_W = 960, WINDOW_H = 512, TILE_SIZE = 32, SIDEBAR_W = TILE_SIZE*3,
        GRID_W = (WINDOW_W-SIDEBAR_W) / TILE_SIZE, GRID_H = WINDOW_H / TILE_SIZE;
    
    private Menu menu;
    private World world;

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

    /** Sets game parameters, loads up files, and starts the menu. */
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
     * Should be called every 20ms. Executes a 'tick' operation.
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        
        if (menu != null) {
            Menu.Choice action = menu.update(input);
            if (action == null) {
                return;
            }
            switch (action) {
                case START:
                    openLevel("level_declan");
                    break;
                case OPTIONS:
                    // TODO: Add options (what settings would we have?) or just remove this
                    break;
                case QUIT:
                    closeRequested();
                    break;
            }
        } else if (world != null) {
            // Can only call inputs once
            boolean rightClick = input.isMousePressed(Input.MOUSE_RIGHT_BUTTON),
                    leftClick = input.isMousePressed(Input.MOUSE_LEFT_BUTTON),
                    escape = input.isKeyPressed(Input.KEY_ESCAPE);
            int mouseX = input.getMouseX(), mouseY = input.getMouseY();

            String action = world.processInput(escape, rightClick);
            switch (action) {
                case "Exit":
                    // TODO: put this in a function or something? (processInput() probs shouldn't return a string too)
                    AudioController.stopAll();
                    world = null;
                    menu = new Menu(getTitle(), WINDOW_W, WINDOW_H);
                    return; // Terminate the update at this point
            }
            
            world.tick(delta);
            world.processEnemies();
            world.processProjectiles();
            world.processTowers(mouseX, mouseY, leftClick);
            world.processButtons(mouseX, mouseY,leftClick);
        }
    }

    /**
     * Responsible for drawing sprites. Called regularly, automatically.
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (menu != null) {
            menu.renderTitle();
            menu.renderOptions(g);
        }
        if (world != null) {
            // Draw the map in half scale
            g.scale(0.5f, 0.5f);
            world.renderTiles(g);
            g.scale(2, 2);

            world.renderEnemies();
            world.renderTowers(g);
            world.renderProjectiles();
            world.drawGUI(g);
        }
    }
    
    
    
    /** Opens a new level and creates a World to manage it.
     * Also minimises the current menu and changes focus to the level.
     */
    private void openLevel(String levelName) throws SlickException{
        // Initialize the tiled map for the level
        TiledMap tiledMap = new TiledMap("assets/levels/" + levelName + ".tmx");
        
        // Load in wave info
        try (Scanner scanner = new Scanner(new File("assets/waves/game1.txt"))) {            
            // Read line-by-line
            scanner.useDelimiter("[\\r\\n;]+");

            ArrayList<Wave> waves = new ArrayList<Wave>();

            // Wave-by-wave
            while (scanner.hasNext()) {
                String wave = scanner.next();
                Wave currWave = new Wave();
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
                        currWave.addInstruction(enemy, spawnTime*1000);
                        spawnTime += spawnRate;
                    }
                }
            }
            
            // Create World and get rid of Menu
            world = new World(WINDOW_W, WINDOW_H, SIDEBAR_W, tiledMap, waves);
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