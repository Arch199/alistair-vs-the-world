package alistair_game;

import java.util.ArrayList;

/** Waves contain a list of their enemy spawns */
public class Wave {
    int waveNum;
    ArrayList<SpawnInstruction> instructions = new ArrayList<>();

    Wave(int waveNum) {
        this.waveNum = waveNum;
    }

    void clearOldInstructions() {
        instructions.removeIf(i -> i.getParsed());
    }

    ArrayList<SpawnInstruction> getInstructions() {
        return instructions;
    }

    int getWaveNum() {
        return waveNum;
    }

    void addInstruction(SpawnInstruction instruction) {
        instructions.add(instruction);
    }

    void removeInstrucion(SpawnInstruction instruction) {
        instructions.remove(instruction);
    }
}

/** Individual enemy spawn instructions, with enemy time and type */
class SpawnInstruction {
    private String enemy;
    private float spawnTime;
    private boolean parsed = false;

    SpawnInstruction(String enemy, float spawnTime) {
        this.enemy = enemy;
        this.spawnTime = spawnTime;
    }

    String getEnemy() { return this.enemy; }
    float getSpawnTime() { return this.spawnTime; }
    boolean getParsed() { return this.parsed; }
    void setParsed(boolean b) {parsed = b; }
}