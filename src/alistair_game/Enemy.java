package alistair_game;

import org.newdawn.slick.Image;

abstract class Enemy extends Sprite {	
	//private double dir;  // Direction: radians anti-clockwise from east TODO: remove
	private int damage = 5;
	float hsp = 0, vsp = 0;
	
	Enemy(float startx, float starty, float hsp, float vsp, Image im) {
		super(startx, starty, im);
		//this.dir = dir;
		this.hsp = hsp;
		this.vsp = vsp;
	}
	
	void advance(float speed, World world) {
		int[][][] path = world.getPath();
		
		// Follow the pre-calculated path
		// If we're about to hit a wall, change direction
		int nextx = world.toGrid(getX()+world.getTileSize()/2*Math.signum(hsp));
		int nexty = world.toGrid(getY()+world.getTileSize()/2*Math.signum(vsp));
		if (!world.inGridBounds(nextx, nexty) || world.getTiles()[nextx][nexty].isWall()) {
			int gridx = world.toGrid(getX()), gridy = world.toGrid(getY());
			if (world.inGridBounds(gridx, gridy)) {
				hsp = speed * path[gridx][gridy][0];
				vsp = speed * path[gridx][gridy][1];
			} else {
				hsp = speed * world.defaultDir(gridx);
				vsp = speed * world.defaultDir(gridy);
			}
		}
		move(hsp, vsp);
				
		// Note: here is the original method for pathfinding, not in use rn
		/*int tlen = world.getTileSize()/4;
		float xdist = (float)(tlen * Math.cos(dir));
		float ydist = (float)(tlen * -Math.sin(dir));
		while (touchingWall(0, 0, tiles, world)) {
			move(-Math.signum(xdist), -Math.signum(ydist));
		}
		// ^ NOTE: this causes it to phase through walls if it somehow backs into one
		
		// Try anti-clockwise, then clockwise, then just do a 180 as a last resort
		double[] attempts = {dir-Math.PI/2, dir+Math.PI/2, dir+Math.PI};
		for (int i = 0; i < attempts.length && touchingWall(xdist, ydist, tiles, world); i++) {
			dir = attempts[i];
			xdist = (float)(tlen * Math.cos(dir));
			ydist = (float)(tlen * -Math.sin(dir));
			
		}
		xdist = (float)(speed * Math.cos(dir));
		ydist = (float)(speed * -Math.sin(dir));
		move(xdist, ydist);*/
	}
	
	// Note: this is no longer in use but may be helpful later
	boolean touchingWall(float xmove, float ymove, World world) {
		boolean result = false;
		move(xmove, ymove);
		for (Tile[] col : world.getTiles()) {
			for (Tile t : col) {
				if (t.isWall() && checkCollision(t)) {
					result = true;
					break;
				}
			}
		}
		move(-xmove, -ymove);
		return result;
	}
	
	int getDamage() { return damage; }
}
