package alistair_game;

import java.awt.Font;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

/** Main menu handler (One instance only) */
public class Menu {
    private String title;
    private int w, h, currentChoice = 0;
    private Button[] buttons;
    
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
        String[] choices = {"Start", "Options", "Quit"};
        buttons = new Button[choices.length];
        for (int i = 0; i < choices.length; i++) {            
            float bnX = w/2,
                  bnY = TOP_OFFSET + i*BUTTON_SPACING;
            buttons[i] = new Button(bnX, bnY, choices[i], OPTION_TTF, BUTTON_PADDING, false, Color.white);
            buttons[i].setCols(NOT_CHOSEN_COL, CHOSEN_COL);
        }
    }
    
    /** 
     * Updates menu based on player input.
     * @param input Obtained from App's GameContainer
     * @return The action taken (e.g. "Quit" to exit game). Defaults to the empty string.
     */
    String update(Input input) {
        int mouseX = input.getMouseX(), mouseY = input.getMouseY();
        boolean clicked = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
        
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
        } else if (buttons[0].contains(mouseX, mouseY)) {
            currentChoice = 0;
        } else if (buttons[1].contains(mouseX, mouseY)) {
            currentChoice = 1;
        } else if (buttons[2].contains(mouseX, mouseY)) {
            currentChoice = 2;
        }
        
        if (clicked || input.isKeyPressed(Input.KEY_ENTER)) {
            return buttons[currentChoice].getText();
        }
        return ""; // default value for no action
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

