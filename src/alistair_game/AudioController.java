package alistair_game;

import org.newdawn.slick.*;

public class AudioController  {
    /** Handles the game audio.
     * Initialized in App.init.
     * To play a sound, call play(event) */

    private static Sound[] intro = new Sound[1];
    private static Sound[] gameover = new Sound[1];
    AudioController() {
        // Initialize sounds
        try {
            intro[0] = new Sound("assets\\Audio\\Intro.ogg");

            gameover[0] = new Sound("assets\\Audio\\GameOver.ogg");

            // TODO: Add the rest of the audio files
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    public static void play(String event) {
        // TODO: Add pitch and volume control
        // Intro sound
        switch (event) {
            // TODO: Add a randomizer
            case "intro":
                intro[0].play();
                break;
            case "gameover":
                gameover[0].play();
                break;
        }
    }

    // TODO: Add an audio-state checker
    // TODO: Add a music handler
}
