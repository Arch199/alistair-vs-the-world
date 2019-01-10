package alistair_game;

import java.util.Random;

import org.newdawn.slick.Graphics;

public class Util {
    /** Contains utility methods for use throughout the project. */

    /** Returns a random int from 0 to num-1 (inclusive). */
    public static int rand(int num) {
        Random r = new Random(System.nanoTime());
        return r.nextInt(num);
    }

    /** Writes horizontally centered text. */
    public static void writeCentered(Graphics g, String str, float x, float y) {
        int offset = g.getFont().getWidth(str) / 2;
        g.drawString(str, x - offset, y);
    }

    /** Calculates the distance between two x-y coordinates. */
    public static float dist(float x1, float y1, float x2, float y2) {
        // Euclidean distance (COMP20008 method)
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}

class SpawnInstruction {
    private String enemy;
    private float spawnTime;

    SpawnInstruction(String enemy, float spawnTime) {
        this.enemy = enemy;
        this.spawnTime = spawnTime;
    }

    String getEnemy() { return this.enemy; }
    float getSpawnTime() { return this.spawnTime; }
}