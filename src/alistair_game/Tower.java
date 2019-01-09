package alistair_game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.Graphics;

import java.util.LinkedList;

import org.newdawn.slick.Color;

class Tower extends Sprite {   
    private boolean placed = false;
    private float range = 150f; // Range is radius from center
    private int fireRate = 0; // In ms
    private int nextShot; // Time of next fire (in ms from start of wave)
    private long spawnTime; // Reset every wave
    private float projSpeed = 4f;

    Tower(float startx, float starty, Image im, int fireRate) {
        super(startx, starty, im);
        this.fireRate = fireRate;
        nextShot = 0;
    }

    /** Makes the shot. Generates a projectile and sets a new time. */
    void shoot(World world) {
        try {
            // Target the next enemy in range
            Vector2f vec = targetNext(world.getEnemies());

            // Create projectile
            Image im = new Image("assets\\sprites\\defaultproj.png"); // TODO: move this reference elsewhere
            Projectile newProj = new Projectile(getX(), getY(), vec, im);
            world.getProjectiles().add(newProj);

            // Determine the time of the next shot
            nextShot += fireRate;
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /** Returns a velocity vector to hit the first enemy in range. */
    private Vector2f targetNext(LinkedList<Enemy> enemies) {
        // Target the first enemy in range
        Enemy target = null;
        for (Enemy e : enemies) {
            if (distanceTo(e) <= range) {
                target = e;
                break;
            }
        }
        // If there is none, fire in an arbitrary direction
        if (target == null) {
            return new Vector2f(projSpeed, 0);
        }
        
        float tx = target.getX(), ty = target.getY();
        Vector2f vec = new Vector2f(tx-getX(), ty-getY());
        vec.normalise().scale(projSpeed);
        
        // Assume it keeps moving in a straight line
        vec.add(target.getV());
        
        return vec;
    }
    
    /** Places the tower. */
    void place(float x, float y) {
        teleport(x, y);
        placed = true;
    }

    /** Returns true if enough time has passed to shoot. */
    boolean readyToShoot(long time) {
        long timeAlive = time - spawnTime;
        return timeAlive >= nextShot;
    }

    /** Draws a range circle around towers. */
    void drawRange(Graphics g) {
        Color oldcol = g.getColor();

        // Top-left corner of the circle
        float xcorner = getX() - range, ycorner = getY() - range;

        // Draw circumference
        g.setColor(new Color(110, 110, 110, 110));
        g.drawOval(xcorner, ycorner, range * 2, range * 2);
        // Fill with a shade of grey (can change vals depending on contrast w/ textures)
        g.setColor(new Color(80, 80, 80, 80));
        g.fillOval(xcorner, ycorner, range * 2, range * 2);

        // Reset color
        // MATT: How do colours work, and why does not resetting it break other graphics
        // operations?
        g.setColor(oldcol);
    }

    void waveReset() {
        nextShot = 0;
        spawnTime = 0;
    }

    boolean isPlaced() { return placed; }

    void setSpawnTime(long time) { spawnTime = time; }
}
