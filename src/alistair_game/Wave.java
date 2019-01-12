package alistair_game;

import java.util.ArrayList;

/** Waves contain a list of their enemy spawns. */
public class Wave {
    int waveNum;
    ArrayList<SpawnInstruction> instructions = new ArrayList<>();

    /** Create a wave
     * @param waveNum The number of this wave (>=1)
     */
    Wave(int waveNum) {
        this.waveNum = waveNum;
    }

    /**
     * Delete all instructions that have already been executed.
     */
    void clearOldInstructions() {
        instructions.removeIf(i -> i.getParsed());
    }

    void addInstruction(SpawnInstruction instruction) {
        instructions.add(instruction);
    }

    void removeInstrucion(SpawnInstruction instruction) {
        instructions.remove(instruction);
    }
    
    ArrayList<SpawnInstruction> getInstructions() { return instructions; }
    int getWaveNum() { return waveNum; }
}

/** Individual enemy spawn instructions, with enemy time and type. */
class SpawnInstruction {
    private String enemy;
    private float spawnTime;
    private boolean parsed = false;

    /**
     * Contains info for one enemy spawn.
     * @param enemy Enemy type
     * @param spawnTime Spawn time from the start of the wave (ms)
     */
    SpawnInstruction(String enemy, float spawnTime) {
        this.enemy = enemy;
        this.spawnTime = spawnTime;
    }

    String getEnemy() { return this.enemy; }
    float getSpawnTime() { return this.spawnTime; }
    boolean getParsed() { return this.parsed; }
    void setParsed(boolean b) { parsed = b; }
}