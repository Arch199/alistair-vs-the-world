package alistair_game;

import org.newdawn.slick.Image;

public class Tile extends Sprite {
    private String name;

    Tile(float x, float y, Image im, String name) {
        super(x, y, im);
        this.name = name;
    }

    public boolean isWall() {
        return name.equals("wall");
    }
}
