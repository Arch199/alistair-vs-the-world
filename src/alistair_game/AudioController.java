package alistair_game;

import org.newdawn.slick.*;
import java.util.*;

public class AudioController  {
    /** Handles the game audio.
     * Initialized in App.init().
     * To play a sound, call play(event) */

    private static Sound[] intro = new Sound[2];
    private static Sound[] gameover = new Sound[2];
    private static Sound victory, quickSort, python, mergeSort, insertionSort, commerceStudent, alcohol;

    static void init() {
        try {
            String path = "assets\\Audio\\";

            // Sounds
            intro[0] = new Sound(path+"Intro.ogg");
            intro[1] = new Sound(path+"Intro2.ogg");
            gameover[0] = new Sound(path+"GameOver.ogg");
            gameover[1] = new Sound(path+"Gameover2.ogg");

            Sound victory = new Sound(path + "Victory.ogg");

            Sound alcohol = new Sound(path + "Alcohol.ogg");
            Sound commerceStudent = new Sound(path + "CommerceStudent.ogg");
            Sound python = new Sound(path + "Python.ogg");

            Sound insertionSort = new Sound(path + "InsertionSort.ogg");
            Sound mergeSort = new Sound(path + "MergeSort.ogg");
            Sound quickSort = new Sound(path + "QuickSort.ogg");

            // Music
            // TODO: Find some music? Can add an array of songs to a musicLoop() method.
            /*
            Music bgmusic = new Music(Path + "");
            bgmusic.loop();
            */
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    public static void play(String event) {
        // TODO: Add pitch and volume control
        // Intro sound
        switch (event) {
            case "intro":
                intro[rand(2)].play();
                break;
            case "gameover":
                gameover[rand(2)].play();
                break;
            case "victory":
                victory.play();
                break;
            case "quicksort":
                quickSort.play();
                break;
            case "insertionsort":
                insertionSort.play();
                break;
            case "mergesort":
                mergeSort.play();
                break;
            case "python":
                python.play();
        }
    }

    private static int rand(int sounds) {
        /* Generates an int 0- (sounds-1) */
        Random gen = new Random(System.nanoTime());
        int choice = gen.nextInt(sounds);
        return choice;
    }
}
