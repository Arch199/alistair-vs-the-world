package alistair_game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

class World {
	/** Handles all the game logic for a level.
	 * Created by App.
	 */
	
	private int tsize, gridw, gridh;
	private float startx, starty, enemy_speed = 0.1f;
	private Tile[][] tiles;
	private int[][][] path;  // Directions to move for each tile
	private LinkedList<Enemy> enemies = new LinkedList<Enemy>();
	private ArrayList<Tower> towers = new ArrayList<Tower>();
	private int timer = 0, spawn_time = 2000, health = 100;
	private Tile alistair;
	private Tower new_tower;
	
	private static Image[] tileset;
	private static String[] tile_names;
	private static final int ALISTAIR_INDEX = 2;
	static {
		// Initialise tileset and tile names
		// Meaning of integers in level file
		tile_names = new String[3];  // TODO: add all this to a file (?)
		tile_names[0] = "wall";
		tile_names[1] = "path";
		tile_names[2] = "alistair";

		// Assign ints to images
		tileset = new Image[tile_names.length];
		try {
			for (int i = 0; i < tileset.length; i++) {
				tileset[i] = new Image("assets\\tiles\\" + tile_names[i] + ".png");
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	World(int w, int h, int tsize, float startx, float starty, int[][] level) {
		this.tsize = tsize;
		this.gridw = w/tsize;
		this.gridh = h/tsize;
		this.startx = startx;
		this.starty = starty;
		
		// Initialise tile sprites from level + tileset
		tiles = new Tile[gridw][gridh];
		for (int x = 0; x < level.length; x++) {
			for (int y = 0; y < level[x].length; y++) {
				int i = level[x][y];
				tiles[x][y] = new Tile((x+0.5f)*tsize, (y+0.5f)*tsize,
				                       tileset[i], tile_names[i]);
				if (i == ALISTAIR_INDEX) alistair = tiles[x][y];
			}
		}
		
		// Traverse the path and store direction values in a grid
		path = new int[gridw][gridh][2];
		int x = toGrid(startx), y = toGrid(starty);
		int i = defaultDir(x);
		int j = defaultDir(y);
		while (x < 0 || x >= gridw || y < 0 || y >= gridh) {
			x += i;
			y += j;
		}
		while (toPos(x) != alistair.getX() || toPos(y) != alistair.getY()) {
			// Move along the path			
			// Check if we've hit a wall yet
			if (x+i < 0 || x+i >= gridw || y+j < 0 || y+j >= gridh || tiles[x+i][y+j].isWall()) {
				// OK, try turning left (anti-clockwise)
				int old_i = i;
				i = j;
				j = -old_i;
				
				// Check again
				if (tiles[x+i][y+j].isWall()) {
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
		
		// Temp tower mouse hover test -- TODO: remove/replace with working towers
		towers = new ArrayList<Tower>();
		try {
			new_tower = new Tower(w/2, h/2, new Image("assets\\alistair32.png"));
			towers.add(new_tower);
		} catch (SlickException e) {
			e.printStackTrace();
		}

		// Intro sound
		AudioController.play("intro");
	}
	
	void tick(int delta) {
		// Handle advancing the timer / spawning enemies
		timer += delta;
		while (timer >= spawn_time) {
			timer -= spawn_time;
			spawnEnemy(startx, starty);
		}
		// TODO: increase speed and decrease spawn time over time (or per wave?)
	}
	
	void spawnEnemy(float x, float y) {
		// Note: currently not using dir with Enemies, here for reference
		/*double dir;
		if (y < 0) {
			// Above top of screen, go down
			dir = Math.PI*3/2;
		} else if (y > h) {
			// Below bottom of screen, go up
			dir = Math.PI/2;
		} else if (x > w) {
			// Right of screen, go left
			dir = Math.PI;
		} else {
			// Default, go right
			dir = 0;
		}*/
		enemies.add(new EnemyPython(x, y, enemy_speed*defaultDir(x), enemy_speed*defaultDir(y)));
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

	void processTowers(Input input) {
		int mousex = input.getMouseX(), mousey = input.getMouseY();
		boolean clicked = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		
		// If we're placing a tower, move it to the mouse position
		if (isPlacingTower()) {		
			new_tower.teleport((float)mousex, (float)mousey);
			
			// Set the tower to be red if it's touching a non-wall tile
			new_tower.setColor(Color.white);
			outer:
			for (Tile[] column : tiles) {
				for (Tile tile : column) {
					if (!tile.isWall() && tile.checkCollision(new_tower)) {						
						new_tower.setColor(Color.red);
						break outer;
					}
				}
			}
			// If the user clicked and it's not colliding with anything, place it
			if (clicked && new_tower.getColor() == Color.white) {
				new_tower.place(toPos(toGrid(mousex)), toPos(toGrid(mousey)));
				new_tower = null;
			}
		}
	}
	
	void drawGUI(Graphics g) {
		// Display Alistair's health
		writeCentered(g, Integer.toString(health), alistair.getX(), alistair.getY());
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
	
	void renderTowers() {
		for (Tower t : towers) {
			t.drawSelf();
		}
	}
	
	static void writeCentered(Graphics g, String str, float x, float y) {
    	int offset = g.getFont().getWidth(str)/2;
    	g.drawString(str, x-offset, y);
    }
	
	void takeDamage(int damage) {
		health -= damage;
		if (health <= 0) {
			System.out.println("we ded");
			AudioController.play("gameover");
			// TODO: add handling for game overs (SEGFAULTS!)
		}
	}
	
	// Convert from literal position to position on grid
	int toGrid(float pos) {
		// Choose closest grid position
		return Math.round((pos-tsize/2)/tsize);
	}
	
	// Convert from grid position to literal coordinates
	float toPos(int gridval) {
		return gridval*tsize + tsize/2;
	}
	
	boolean inGridBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < gridw && y < gridh;
	}
	
	// Returns a grid direction to point inwards from the current position
	int defaultDir(int gridval) {
		return gridval < 0 ? 1 : (gridval >= gridw ? -1 : 0);
	}
	
	// Returns a literal direction to point inwards
	float defaultDir(float pos) {
		return pos < 0 ? 1 : (pos >= gridw*tsize ? -1 : 0);
	}
	
	boolean isPlacingTower() { return new_tower != null; }
	int getTileSize() { return tsize; }
	Tile[][] getTiles() { return tiles; }
	int[][][] getPath() { return path; }
}
