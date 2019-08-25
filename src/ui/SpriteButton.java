package ui;

import org.newdawn.slick.Graphics;

import game.Sprite;

/** A button with an accompanying sprite. */
public class SpriteButton extends Button {
    private Sprite sprite;

    public SpriteButton(Sprite sprite, Runnable action) {
        super(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight(), 0, action);
        this.sprite = sprite;
    }

    @Override
    public void render(Graphics g) {
        sprite.setColor(getColor());
        sprite.render(g);
        super.render(g);
    }
}
