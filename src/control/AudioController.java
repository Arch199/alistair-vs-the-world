package control;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.newdawn.slick.*;

/**
 * Stores all sound files, and provides a method to play them.
 */
public final class AudioController {
    private static HashMap<String, Sound> singleSounds = new HashMap<String, Sound>();
    private static HashMap<String, Sound[]> multiSounds = new HashMap<String, Sound[]>();
    private static HashMap<String, Boolean> hasPlayed = new HashMap<>();

    static {
        // Load audio files
        File folder = new File("assets/audio");
        try {
            for (File f : folder.listFiles()) {
                if (f.isFile()) {
                    // Get file name without extension
                    String name = f.getName().replaceFirst("[.][^.]+$", "");
                    // Add the sound to the hash map
                    singleSounds.put(name.toLowerCase(), new Sound(f.getPath()));
                    hasPlayed.put(name.toLowerCase(), false);
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
                    hasPlayed.put(f.getName().toLowerCase(), false);
                }
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // TODO: Find some music? Can add an array of songs to a musicLoop() method.
    }
    
    private AudioController() {} // prevents instantiation from outside the class

    /**
     * Plays a sound.
     *
     * @param event In lowercase, the name of the game event/sound asset to play. Can specify a folder.
     * @param allowRepeat Lets this sound be played again until the next reset()
     */
    public static void play(String event, boolean allowRepeat) {
        // TODO: Add pitch and volume control
        // Play the sound
        Sound[] list = multiSounds.get(event);
        if (list != null) {
            list[Util.rand(list.length)].play();
        } else {
            Sound single = singleSounds.get(event);
            if (single != null) {
                // See if the sound has already been played
                if(hasPlayed.get(event)) { return; }

                single.play();

                // Flag that this event has been played if repeats are not allowed
                if (!allowRepeat) { hasPlayed.put(event, true); }
            } else {
                System.err.printf("ERROR: Could not find sound '%s'%n", event);
            }
        }
    }

    /**
     * Plays a sound, by default allowing repeats.
     * @param event In lowercase, the name of the game event/sound asset to play. Can specify a folder.
     */
    public static void play(String event) {
        play(event, true);
    }

    /**
     * Stops all currently playing sounds.
     */
    public static void stopAll() {
        for (Sound s : singleSounds.values()) {
            s.stop();
        }
        for (Sound[] list : multiSounds.values()) {
            for (Sound s : list) {
                s.stop();
            }
        }
    }

    /**
     * Resets the audio controller by allowing all sounds to be played again.
     */
    public static void reset() {
        for(String sound: hasPlayed.keySet()) {
            hasPlayed.put(sound, false);
        }
    }
}
