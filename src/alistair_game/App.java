// Main App handler for Alistair-themed Tower Defence Game

package alistair_game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class App extends BasicGame {
	private static final int
		WINDOW_W = 960,  // was 640
		WINDOW_H = 672,  // was 480, then 720
		TILE_SIZE = 48,  // 48 for tiles, 32 for towers, enemies, etc.
		GRID_W = WINDOW_W/TILE_SIZE,  // 20
		GRID_H = WINDOW_H/TILE_SIZE;  // 14, was 15
	
	World world; // hmmmmmmmmmmmmm -- privacy level? TODO: consider privacy levels more
	
    public static void main(String[] args) {
        try {
            System.err.println("Yeet, starting main");

            App game = new App("Alistair vs The World");
            AppGameContainer appgc = new AppGameContainer(game);
            appgc.setDisplayMode(WINDOW_W, WINDOW_H, false);
			appgc.start();
			
            System.err.println("Yeet, game exited");  // never reaches this line?
        } catch (SlickException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, e);
            // ^ copied this code from somewhere, not sure what it does tbh -James
        }
    }

    public App(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        System.err.println("Initialising game...");
        gc.setShowFPS(false);
        
        // Initialise level from file and create world object
        try {
        	int[][] level = new int[GRID_W][GRID_H];
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
    	System.err.println("Exiting game...");
    	System.exit(0);
    	return false;
    }
}