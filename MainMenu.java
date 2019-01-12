package alistair_game;

import java.awt.Font;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.*;

public class MainMenu extends BasicGame {

    private int playersChoice = 0; 
    private static final int WINDOW_W = 960, WINDOW_H = 672;
    private static final int NOCHOICES = 3;
    private static final int START = 0;
    private static final int OPTIONS = 1;
    private static final int QUIT = 2;
    private String[] playersOptions = new String[NOCHOICES];
    private boolean exit = false;
    private Font option_font, title_font;
    private TrueTypeFont playersOptionsTTF, titleTTF;
    private Color notChosen = new Color(153, 204, 255),
                  Chosen = Color.yellow,
                  Title = new Color(255, 69, 0); 

    public MainMenu() {
        super("Main Menu");
    }

    
    public void init(GameContainer gc) throws SlickException {
        option_font = new Font("Verdana", Font.BOLD, 40);
        playersOptionsTTF = new TrueTypeFont(option_font, true);
        
        title_font = new Font("Verdana", Font.BOLD, 45);
        titleTTF = new TrueTypeFont(title_font, true);
        
        playersOptions[0] = "Start";
        playersOptions[1] = "Options";
        playersOptions[2] = "Quit";
    }

    
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        if (input.isKeyPressed(Input.KEY_DOWN)) {
            if (playersChoice == (NOCHOICES - 1)) {
                playersChoice = 0;
            } else {
                playersChoice++;
            }
        }
        if (input.isKeyPressed(Input.KEY_UP)) {
            if (playersChoice == 0) {
                playersChoice = NOCHOICES - 1;
            } else {
                playersChoice--;
            }
        }
        if (input.isKeyPressed(Input.KEY_ENTER)) {
            if (playersChoice == QUIT) {
                exit = true;
            }
        }
    }

    
    public void render(GameContainer gc, Graphics g) throws SlickException {
        renderTitle();
        renderPlayersOptions();
        if (exit) {
            gc.exit();
        }
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer((Game) new MainMenu());
        app.setDisplayMode(WINDOW_W, WINDOW_H, false);
        app.start();
    }
    
    private void renderTitle() {
        String title = "Alistair Vs The World";
        titleTTF.drawString((WINDOW_W/2) - (titleTTF.getWidth(title)/2), 125, title, Title);
        
    }
    
    private void renderPlayersOptions() {
        for (int i = 0; i < NOCHOICES; i++) {
            if (playersChoice == i) {
                playersOptionsTTF.drawString((WINDOW_W/2) - (playersOptionsTTF.getWidth(playersOptions[i])/2), i * 50 + 250, playersOptions[i], Chosen);
            } else {
                playersOptionsTTF.drawString((WINDOW_W/2) - (playersOptionsTTF.getWidth(playersOptions[i])/2), i * 50 + 250, playersOptions[i], notChosen);
            }
        }
    }
}
