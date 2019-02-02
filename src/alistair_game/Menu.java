package alistair_game;

import java.awt.Font;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

/** Main menu handler (One instance only) */
public class Menu {
    enum Choice {
        START("Start"),
        OPTIONS("Options"),
        QUIT("Quit");
        final String displayName;
        Choice(String s) { displayName = s; }
        static Choice fromDisplayName(String s) {
            for (Choice c : Choice.values()) {
                if (c.displayName.equals(s)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("No matching choice for display name '" + s + "'");
        }
    }
    
    private String title;
    private int w, h, currentChoice = 0;
    private Button[] buttons;
    private int oldMouseX = 0, oldMouseY = 0;
    
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
        this.h = h;
        
        // Create the buttons
        Choice[] choices = Choice.values();
        buttons = new Button[choices.length];
        for (int i = 0; i < choices.length; i++) {            
            float bnX = w/2,
                  bnY = TOP_OFFSET + i*BUTTON_SPACING;
            buttons[i] = new Button(bnX, bnY, choices[i].displayName, OPTION_TTF, BUTTON_PADDING, false, Color.white);
            buttons[i].setCols(NOT_CHOSEN_COL, CHOSEN_COL);
        }
    }
    
    /** 
     * Updates menu based on player input.
     * @param input Obtained from App's GameContainer
     * @return The action taken as an enum (e.g. Menu.Choice.QUIT to exit game). Defaults to null.
     */
    Choice update(Input input) {
        int mouseX = input.getMouseX(), mouseY = input.getMouseY();
        boolean clicked = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
        
        // Decide whether to work with mouse or keyboard input
        if (mouseX != oldMouseX || mouseY != oldMouseY) {
            // Mouse was moved; use mouse input
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].contains(mouseX, mouseY)) {
                    currentChoice = i;
                    break;
                }
            }
        } else {
            // No mouse movement; use keyboard input
            if (input.isKeyPressed(Input.KEY_DOWN)) {
                if (currentChoice == buttons.length-1) {
                    currentChoice = 0;
                } else {
                    currentChoice++;
                }
            } else if (input.isKeyPressed(Input.KEY_UP)) {
                if (currentChoice == 0) {
                    currentChoice = buttons.length-1;
                } else {
                    currentChoice--;
                }
            }
        }
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        
        if (clicked || input.isKeyPressed(Input.KEY_ENTER)) {
            return Choice.fromDisplayName(buttons[currentChoice].getText());
        }
        return null; // default value for no action
    }
    
    void renderTitle() {
        TITLE_TTF.drawString((w/2) - (TITLE_TTF.getWidth(title)/2), 125, title, TITLE_COL);
    }
    
    void renderOptions(Graphics g) {
        for (int i = 0; i < buttons.length; i++) {                
            if (currentChoice == i) {
                buttons[i].setHover(true);
                buttons[i].drawSelf(g);
            } else {
                buttons[i].setHover(false);
                buttons[i].drawSelf(g);
            }
        }
    }
}

