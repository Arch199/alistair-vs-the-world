package control;

import java.awt.Font;
import java.util.Arrays;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import ui.Button;
import ui.TextButton;

/** Main menu handler (One instance only) */
public class Menu {
    public enum Choice {
        START("Start", App::openLevel),
        OPTIONS("Options", () -> {}),
        QUIT("Quit", App::exit);
        public final String displayName;
        public final Runnable action;
        Choice(String displayName, Runnable action) {
            this.displayName = displayName;
            this.action = action;
        }
        public static Choice fromDisplayName(String s) {
            for (Choice c : Choice.values()) {
                if (c.displayName.equals(s)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("No matching choice for display name '" + s + "'");
        }
    }
    
    private String title;
    private int w, currentChoice = 0, oldMouseX = 0, oldMouseY = 0;
    private TextButton[] buttons;
    
    private static final Font
        OPTION_FONT = new Font("Verdana", Font.BOLD, 40),
        TITLE_FONT = new Font("Verdana", Font.BOLD, 45);
    private static final TrueTypeFont
        OPTION_TTF = new TrueTypeFont(OPTION_FONT, true),
        TITLE_TTF = new TrueTypeFont(TITLE_FONT, true);
    private static final Color
        NOT_CHOSEN_COL = new Color(153, 204, 255),
        CHOSEN_COL = Color.yellow,
        TITLE_COL = new Color(255, 69, 0);
    private static final int
        BUTTON_PADDING = 1,
        TOP_OFFSET = 250,
        BUTTON_SPACING = 65;
    
    Menu(String title, int w, int h) {
        this.title = title;
        this.w = w;
        
        // Create the buttons
        Choice[] choices = Choice.values();
        buttons = new TextButton[choices.length];
        for (int i = 0; i < choices.length; i++) {            
            float bnX = w/2, bnY = TOP_OFFSET + i*BUTTON_SPACING;
            if (bnY >= h) {
                throw new IllegalStateException("Buttons overflow vertical space in menu");
            }
            TextButton b = new TextButton(bnX, bnY, choices[i].displayName, OPTION_TTF, choices[i].action);
            b.setPadding(BUTTON_PADDING);
            b.setColors(NOT_CHOSEN_COL, null, CHOSEN_COL);
            int myIndex = i;
            b.highlightWhen(() -> {
                if (b.contains(App.getMouseX(), App.getMouseY())) {
                    currentChoice = myIndex;
                }
                return currentChoice == myIndex;
            });
            b.triggerWhen(() -> App.isLeftClicked() || App.isKeyPressed(Input.KEY_ENTER));
            buttons[i] = b;
        }
    }
    
    /** Updates menu based on player input.
     * @param delta Time in ms since last update.
     */
    public void update(int delta) {
        int mouseX = App.getMouseX(), mouseY = App.getMouseY();

        // No mouse movement; use keyboard input
        if (mouseX == oldMouseX || mouseY == oldMouseY) {
            if (App.isKeyPressed(Input.KEY_DOWN)) {
                if (currentChoice == buttons.length-1) {
                    currentChoice = 0;
                } else {
                    currentChoice++;
                }
            } else if (App.isKeyPressed(Input.KEY_UP)) {
                if (currentChoice == 0) {
                    currentChoice = buttons.length-1;
                } else {
                    currentChoice--;
                }
            }
        }
        oldMouseX = mouseX;
        oldMouseY = mouseY;

        Arrays.stream(buttons).forEach(b -> b.update(delta));
    }
    
    public void render(Graphics g) {
        // Title
        TITLE_TTF.drawString((w/2) - (TITLE_TTF.getWidth(title)/2), 125, title, TITLE_COL);
        
        // Options
        Arrays.stream(buttons).forEach(b -> b.render(g));
    }
}

