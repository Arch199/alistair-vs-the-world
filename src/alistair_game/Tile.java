package alistair_game;

import org.newdawn.slick.Image;

/**
 * Tile sprites to make the terrain
 */
public class Tile extends Sprite {
    private String name;

    /** Create a tile
     * @param x Start x-coord
     * @param y Start y-coord
     * @param im Sprite image
     * @param name Tile name, e.g. grass
     */
    Tile(float x, float y, Image im, String name) {
        super(x, y, im);
        this.name = name;
    }

    public boolean isWall() {
        return name.equals("wall");
    }
}
