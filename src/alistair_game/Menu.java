package alistair_game;

import java.awt.Font;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

public class Menu {
    private int w, h, currentChoice = 0;
    private String[] choices = {"Start", "Options", "Quit"};
    private String title;
    
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
    
    Menu(String title, int w, int h) {
        this.title = title;
        this.w = w;
        this.h = h;
    }
    
    /** 
     * Updates menu based on player input.
     * @param input Obtained from App's GameContainer
     * @return The action taken (e.g. "Quit" to exit game). Defaults to the empty string.
     */
    String update(Input input) {
        int xpos = input.getMouseX(), ypos = input.getMouseY();
        
        if (input.isKeyPressed(Input.KEY_DOWN)) {
            if (currentChoice == choices.length-1) {
                currentChoice = 0;
            } else {
                currentChoice++;
            }
        }
        else if (input.isKeyPressed(Input.KEY_UP)) {
            if (currentChoice == 0) {
                currentChoice = choices.length-1;
            } else {
                currentChoice--;
            }
        }
        else if (input.isKeyPressed(Input.KEY_ENTER)) {
            return choices[currentChoice];
        }
        /*else if () { // TODO: Add Keyhover and press function
            
        } */
        return ""; // default value for no action
    }
    
    void renderTitle() {
        TITLE_TTF.drawString((w/2) - (TITLE_TTF.getWidth(title)/2), 125, title, TITLE_COL);
    }
    
    void renderOptions(Graphics g) {
        for (int i = 0; i < choices.length; i++) {
            if (currentChoice == i) {
                OPTION_TTF.drawString((w/2) - OPTION_TTF.getWidth(choices[i])/2, i*56+252, choices[i], CHOSEN_COL);
                g.setColor(CHOSEN_COL);
                g.drawRect((w/2) - (OPTION_TTF.getWidth(choices[i])/2)-2, i*56 + 250, OPTION_TTF.getWidth(choices[i]) + 4, OPTION_TTF.getHeight() + 4);
            } else {
                OPTION_TTF.drawString((w/2) - OPTION_TTF.getWidth(choices[i])/2, i*56+252, choices[i], NOT_CHOSEN_COL);
                g.setColor(NOT_CHOSEN_COL);
                g.drawRect((w/2) - (OPTION_TTF.getWidth(choices[i])/2)-2, i*56 + 250, OPTION_TTF.getWidth(choices[i]) + 4, OPTION_TTF.getHeight() + 4);
                //TODO: Replace magic numbers with Constants
            }
        }
    }
}
