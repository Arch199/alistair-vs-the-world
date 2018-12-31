package alistair_game;

import org.newdawn.slick.Image;

abstract class Enemy extends Sprite {
	private double dir;  // Direction: radians anti-clockwise from east
	private int damage = 5;
	
	Enemy(float startx, float starty, double dir, Image im) {
		super(startx, starty, im);
		this.dir = dir;
	}
	
	void advance(float speed, World world) {
		int tlen = world.getTileSize()/4;  // hmmm TODO: optimise this value
		Tile[][] tiles = world.getTiles();
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
		move(xdist, ydist);
	}

	boolean touchingWall(float xmove, float ymove, Tile[][] tiles, World world) {
		boolean result = false;
		move(xmove, ymove);
		for (Tile[] col : tiles) {
			for (Tile t : col) {  // ??? TODO: help
				if (t.isWall() && checkCollision(t)) {
					result = true;
					break;
				}
			}
		}
		//return checkCollision(world.tow());
		move(-xmove, -ymove);
		return result;
	}
	
	int getDamage() { return damage; }
}
