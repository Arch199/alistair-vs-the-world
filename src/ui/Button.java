package ui;

import control.App;
import game.Entity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.function.Supplier;

public abstract class Button extends Entity {
    private boolean border = false;
    private int padding;
    private Color defaultColor = Color.white, disabledColor = Color.gray, highlightedColor = Color.lightGray;
    private Supplier<Boolean>
        disabled = () -> false,
        highlighted = () -> contains(App.getMouseX(), App.getMouseY()),
        triggered = App::isLeftClicked;
    private Runnable action;

    /** Create a clickable button. */
    public Button(float x, float y, int w, int h, int padding, Runnable action) {
        super(x, y, w, h);
        this.padding = padding;
        this.action = action;
    }

    /** Have the button check for triggers. */
    public void update() {
        if (!disabled.get() && highlighted.get() && triggered.get()) {
            action.run();
        }
    }

    /** Draw the button. */
    public void render(Graphics g) {
        if (border) {
            // Consider moving this to a drawing object to avoid having to pass in graphics?
            g.setColor(getCurrentColor());
            g.drawRect(getLeft() - padding, getTop() - padding, getWidth() + 2 * padding, getHeight() + 2 * padding);
        }
    }

    /** Set the colours of a button that can be highlighted and disabled.
     * If null is passed as an argument, that color is left unchanged.
     * @param defaultColor Default color.
     * @param disabledColor Color when disabled.
     * @param highlightedColor Color on highlight.
     */
    public void setColors(Color defaultColor, Color disabledColor, Color highlightedColor) {
        if (defaultColor != null) this.defaultColor = defaultColor;
        if (disabledColor != null) this.disabledColor = disabledColor;
        if (highlightedColor != null) this.highlightedColor = highlightedColor;
    }

    protected Color getCurrentColor() {
        if (disabled.get()) {
            return disabledColor;
        } else if (highlighted.get()) {
            return highlightedColor;
        }
        return defaultColor;
    }

    public void disableWhen(Supplier<Boolean> condition) { disabled = condition; }
    public void highlightWhen(Supplier<Boolean> condition) { highlighted = condition; }
    public void triggerWhen(Supplier<Boolean> condition) { triggered = condition; }
    public void setPadding(int padding) { this.padding = padding; }
    public void setBorder(boolean border) { this.border = border; }
}
