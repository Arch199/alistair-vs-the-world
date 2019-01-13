package alistair_game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Waves contain a list of their enemy spawns. */
public class Wave {
    private List<SpawnInstruction> instructions = new LinkedList<>();
    
    /** Individual enemy spawn instructions, with enemy time and type. */
    private class SpawnInstruction {
        String enemy;
        float spawnTime;

        SpawnInstruction(String enemy, float spawnTime) {
            this.enemy = enemy;
            this.spawnTime = spawnTime;
        }
    }
    
    /**
     * Check if enemies are due to be spawned.
     * @param timer The time since the start of the wave
     * @return The name of the enemy to be spawned as a String or ""
     */
    String trySpawn(long timer) {
        Iterator<SpawnInstruction> itr = instructions.iterator();
        while (itr.hasNext()) {
            SpawnInstruction si = itr.next();
            if (timer >= si.spawnTime) {
                itr.remove();
                return si.enemy;
            }
        }
        return ""; // default failure value
    }
    
    void addInstruction(String enemy, float spawnTime) {
        instructions.add(new SpawnInstruction(enemy, spawnTime));
    }
    
    boolean isFinished() { return instructions.isEmpty(); }
}