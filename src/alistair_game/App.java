// Main App handler for Alistair-themed Tower Defence Game

package alistair_game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.*;

public class App extends BasicGame {
	private static final int
		// OLD vals: 960/672/48/20/15
		WINDOW_W = 960,
		WINDOW_H = 672,
		TILE_SIZE = 48,
		GRID_W = WINDOW_W/TILE_SIZE,
		GRID_H = WINDOW_H/TILE_SIZE;
	
	private World world;
	
    public static void main(String[] args) {
        try {
            Print.print("Yeet, starting main");

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
        Print.print("GAME STATE: Initialising game...");
        gc.setShowFPS(false);

        // Initialise sound controller
        AudioController ac = new AudioController();

        // Initialise level from file and create world object
        try {
        	// 2D grid array
        	int[][] level = new int[GRID_W][GRID_H];

        	// Load map info file
			Scanner scanner = new Scanner(new File("assets\\levels\\level1.txt"));
			for (int y = 0; y < GRID_H; y++) {
				assert(scanner.hasNext());
				char[] line = scanner.next().toCharArray();
				int x = 0;
				for (char c : line) {
					if (x >= GRID_W) break;
					assert(Character.isDigit(c));
					level[x++][y] = Character.getNumericValue(c);
				}
			}

			// Enemy spawn location
			float startx = (float)scanner.nextInt()*TILE_SIZE+TILE_SIZE/2;
			float starty = (float)scanner.nextInt()*TILE_SIZE+TILE_SIZE/2;
			scanner.close();

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
    	
    	Input input = gc.getInput();
    	world.processTowers(input.getMouseX(), input.getMouseY());
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {    	
    	// Draw all the sprites
    	world.renderTiles();
    	world.renderEnemies();
    	world.renderTowers();
    	
    	world.drawGUI(g);
    }

    @Override
    public boolean closeRequested() {  
    	Print.print("GAME STATE: Exiting game");
    	System.exit(0);
    	return false;
    }
}