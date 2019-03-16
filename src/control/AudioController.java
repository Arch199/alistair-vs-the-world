package control;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.newdawn.slick.*;

/**
 * Stores all sound files, and provides a method to play them.
 */
public class AudioController {
    private static HashMap<String, Sound> singleSounds = new HashMap<String, Sound>();
    private static HashMap<String, Sound[]> multiSounds = new HashMap<String, Sound[]>();
    static {
        // Load audio files
        File folder = new File("assets\\audio");
        try {
            for (File f : folder.listFiles()) {
                if (f.isFile()) {
                    // Get file name without extension
                    String name = f.getName().replaceFirst("[.][^.]+$", "");
                    // Add the sound to the hash map
                    singleSounds.put(name.toLowerCase(), new Sound(f.getPath()));
                } else {
                    // Add all the sounds in the subfolder to an array
                    Sound[] new_sounds = Arrays.stream(f.listFiles()).filter(file -> file.isFile()).map(file -> {
                        try {
                            return new Sound(file.getPath());
                        } catch (SlickException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).toArray(Sound[]::new);
                    multiSounds.put(f.getName().toLowerCase(), new_sounds);
                }
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // TODO: Find some music? Can add an array of songs to a musicLoop() method.
    }

    /**
     * Plays a sound.
     *
     * @param event The name of the game event, and the sound in assets/audio to play. Can specify a folder.
     */
    public static void play(String event) {
        // TODO: Add pitch and volume control
        Sound[] list = multiSounds.get(event);
        if (list != null) {
            list[Util.rand(list.length)].play();
        } else {
            Sound single = singleSounds.get(event);
            if (single != null) {
                single.play();
            } else {
                System.err.printf("ERROR: Could not find sound '%s'%n", event);
            }
        }
    }
    
    /**
     * Stops all currently playing sounds.
     */
    static void stopAll() {
        for (Sound s : singleSounds.values()) {
            s.stop();
        }
        for (Sound[] list : multiSounds.values()) {
            for (Sound s : list) {
                s.stop();
            }
        }
    }
}
