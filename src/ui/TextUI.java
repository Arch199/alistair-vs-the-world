package ui;

import game.Entity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/** A class for writing text to the screen, either permanently or temporarily. */
public class TextUI {
    /** The horizontal text alignment type for the text. */
    public enum Mode { LEFT, CENTER, RIGHT }

    // TODO: add vertical alignment mode (?)

    private List<Text> permText = new LinkedList<>();
    private List<Text> tempText = new ArrayList<>();

    /**
     * Draw all the text in the UI.
     * @param g The Slick graphics object.
     */
    public void render(Graphics g) {
        g.setColor(Color.white); // TODO: add text color customization (currently defaults to white)
        permText.forEach(Text::render);
        tempText.forEach(Text::render);
        tempText.clear();
    }

    // TODO: consider refactoring dynamic/entity text to TextPosition / TextContent components

    /**
     * Add a text object to the UI.
     * @param supplier A function supplying the text to draw.
     * @param x Text x-position.
     * @param y Text y-position.
     * @param ttf A True Type Font.
     * @param mode The horizontal alignment mode for the text.
     */
    public void add(Supplier<String> supplier, int x, int y, TrueTypeFont ttf, Mode mode) {
        permText.add(new DynamicText(supplier, x, y, ttf, mode));
    }

    /**
     * Add a text object bound to an entity to the UI.
     * @param supplier A function supplying the text to draw.
     * @param entity The entity to bind to.
     * @param ttf A True Type Font.
     * @param mode The horizontal alignment mode for the text.
     */
    public void add(Supplier<String> supplier, Entity entity, TrueTypeFont ttf, Mode mode) {
        permText.add(new EntityText(supplier, entity, ttf, mode));
    }

    /**
     * Draw a string the next time the UI is rendered.
     * @param text The string to draw.
     * @param x Text x-position.
     * @param y Text y-position.
     * @param ttf A True Type Font.
     * @param mode The horizontal alignment mode for the text.
     */
    public void drawOnce(String text, int x, int y, TrueTypeFont ttf, Mode mode) {
        tempText.add(new Text(text, x, y, ttf, mode));
    }

    /** Some static text with an on-screen position. */
    private static class Text {
        private String text;
        private int x, y;
        private TrueTypeFont ttf;
        private Mode mode;

        /**
         * Create a text object.
         * @param text The string to draw.
         * @param x    x-position.
         * @param y    y-position.
         * @param ttf A True Type Font.
         * @param mode The horizontal alignment mode for the text.
         */
        public Text(String text, int x, int y, TrueTypeFont ttf, Mode mode) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.ttf = ttf;
            this.mode = mode;
        }

        /** Draw this text object. */
        public void render() {
            int offset = 0;
            switch (mode) {
                case LEFT: break;
                case CENTER: offset = ttf.getWidth(text) / 2; break;
                case RIGHT: offset = ttf.getWidth(text); break;
            }
            ttf.drawString(x - offset, y, text);
        }

        /**
         * Set the text the text object will draw.
         * @param text The new text, as a string.
         */
        public void setText(String text) { this.text = text; }

        /**
         * Set the x-position to draw to.
         * @param x The new x-position.
         */
        public void setX(int x) { this.x = x; }

        /**
         * Set the y-position to draw to.
         * @param y The new y-position.
         */
        public void setY(int y) { this.y = y; }
    }

    /** Text that is self-updating. */
    private static class DynamicText extends Text {
        private Supplier<String> text;

        /**
         * Create a text object that dynamically updates itself to match the output of a given function.
         * @param text A function to produce the text to draw.
         * @param x    x-position.
         * @param y    y-position
         * @param ttf A True Type Font.
         * @param mode The horizontal alignment mode for the text.
         */
        public DynamicText(Supplier<String> text, int x, int y, TrueTypeFont ttf, Mode mode) {
            super(text.get(), x, y, ttf, mode);
            this.text = text;
        }

        @Override
        public void render() {
            setText(text.get());
            super.render();
        }
    }

    // TODO: add the ability to match the tracked entity's color and scale
    /** Dynamic text bound to an entity. */
    private static class EntityText extends DynamicText {
        private Entity entity;

        /**
         * Create a text object that dynamically updates itself to a given position and text display.
         * @param text   A function to produce the text to draw.
         * @param entity An entity for the text to attach to.
         * @param ttf A True Type Font.
         * @param mode   The horizontal alignment mode for the text.
         */
        public EntityText(Supplier<String> text, Entity entity, TrueTypeFont ttf, Mode mode) {
            super(text, (int)entity.getX(), (int)entity.getY(), ttf, mode);
            this.entity = entity;
        }

        @Override
        public void render() {
            setX((int)entity.getX());
            setY((int)entity.getY());
            super.render();
        }
    }
}
