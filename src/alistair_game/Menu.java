package alistair_game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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
    private List<Button> buttons = new ArrayList<Button>();
    
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
        int mouseX = input.getMouseX(), mouseY = input.getMouseY();
        
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
        else if (buttons.get(0).contains(mouseX, mouseY)) {
            currentChoice = 0;
            if (buttons.get(0).isClicked(mouseX, mouseY, input.isMousePressed(0))) {
                return choices[currentChoice];
            }
        } 
        else if (buttons.get(1).contains(mouseX, mouseY)) {
            currentChoice = 1;
            if (buttons.get(1).isClicked(mouseX, mouseY, input.isMousePressed(0))) {
                return choices[currentChoice];
            }
        }
        else if (buttons.get(2).contains(mouseX, mouseY)) {
            currentChoice = 2;
            if (buttons.get(2).isClicked(mouseX, mouseY, input.isMousePressed(0))) {
                return choices[currentChoice];
            }
        }
        else if (input.isKeyPressed(Input.KEY_ENTER)) {
            return choices[currentChoice];
        } 
        return ""; // default value for no action
    }
    
    void renderTitle() {
        TITLE_TTF.drawString((w/2) - (TITLE_TTF.getWidth(title)/2), 125, title, TITLE_COL);
    }
    
    void renderOptions(Graphics g) {
        for (int i = 0; i < choices.length; i++) {
            int fontX = (w/2) - OPTION_TTF.getWidth(choices[i])/2, 
                fontY = i*56+252, 
                bnW = OPTION_TTF.getWidth(choices[i]) + 4,
                bnH = OPTION_TTF.getHeight() + 4;
            
            float bnX = (w/2) - (OPTION_TTF.getWidth(choices[i])/2)-2, 
                  bnY = i*56 + 250;
            
            // Generates the instances of the buttons.
            buttons.add(new Button(bnX, bnY, bnW, bnH, choices[i]));
            buttons.get(i).setText(OPTION_TTF, fontX, fontY);
                
            if (currentChoice == i) {
                buttons.get(i).drawButton(g, CHOSEN_COL);
            } else {
                buttons.get(i).drawButton(g, NOT_CHOSEN_COL);
            }
        }
    }
}

