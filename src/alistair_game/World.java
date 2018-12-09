package alistair_game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

class World {
	private int w, h, tsize, gridw, gridh;
	private float startx, starty, enemy_speed = 0.1f;
	private Tile[][] tiles;
	private LinkedList<Enemy> enemies = new LinkedList<Enemy>();
	private ArrayList<Tower> towers = new ArrayList<Tower>();
	private int timer = 0, spawn_time = 2000, health = 100;
	private Tile alistair;
	
	private static Image[] tileset;
	private static String[] tile_names;
	private static final int ALISTAIR_INDEX = 2;
	static {
		// Initialise tileset and tile names
		tile_names = new String[3];  // TODO: add all this to a file (?)
		tile_names[0] = "wall";
		tile_names[1] = "path";
		tile_names[2] = "alistair";
		
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
		this.w = w;
		this.h = h;
		this.tsize = tsize;  // TODO: decide if we want this field or not
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
		
		// Temp tower mouse hover test
		towers = new ArrayList<Tower>();
		try {
			towers.add(new Tower(w/2, h/2, new Image("assets\\alistair32.png")));
		} catch (SlickException e) {
			e.printStackTrace();
		}
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
		double dir;
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
		}
		enemies.add(new EnemyPython(x, y, dir));
	}
	
	void moveEnemies() {
		Iterator<Enemy> itr = enemies.iterator();
		while (itr.hasNext()) {
			Enemy e = itr.next();
			e.advance(enemy_speed, tiles, this);
			if (e.checkCollision(alistair)) {
				takeDamage(e.getDamage());
				itr.remove();
			}
		}
	}

	void processTowers(int mousex, int mousey) {
		// Temporarily just move the single tower to the mouse position
		// (for the purposes of collision testing)
		// TODO: update this when towers are actually implemented
		Tower tow = towers.get(0);
		tow.teleport((float)mousex, (float)mousey);
		
		// Set the tower to be red if it's touching a non-wall tile
		// to test the collision system
		tow.setColor(Color.white);
		outer:
		for (Tile[] column : tiles) {
			for (Tile tile : column) {
				if (!tile.isWall() && tile.checkCollision(tow)) {
					tow.setColor(Color.red);
					break outer;
				}
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
	
	
	Tower tow() {  // TODO: remove
		return towers.get(0);
	}
	
	void takeDamage(int damage) {
		health -= damage;
		if (health <= 0) {
			System.err.println("we ded");
			// TODO: add handling for game overs (SEGFAULTS!)
		}
	}
	
	int getTileSize() { return tsize; }
}
