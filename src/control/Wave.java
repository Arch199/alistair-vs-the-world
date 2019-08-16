package control;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import game.Enemy;

/** Waves contain a list of their enemy spawns. */
public class Wave {
    private List<SpawnInstruction> instructions = new LinkedList<>();
    
    /** Check if enemies are due to be spawned.
     * @param timer The time since the start of the wave.
     * @return The type of enemy to be spawned as an enum.
     */
    Enemy.Type trySpawn(long timer) {
        Iterator<SpawnInstruction> itr = instructions.iterator();
        while (itr.hasNext()) {
            SpawnInstruction si = itr.next();
            if (timer >= si.spawnTime) {
                itr.remove();
                return si.enemy;
            }
        }
        return null; // default failure value
    }
    
    void addInstruction(String enemy, float spawnTime) {
        Enemy.Type type = Enemy.Type.valueOf(enemy.toUpperCase());
        instructions.add(new SpawnInstruction(type, spawnTime));
    }
    
    boolean isFinished() { return instructions.isEmpty(); }

    /** Individual enemy spawn instructions, with enemy time and type. */
    private static class SpawnInstruction {
        Enemy.Type enemy;
        float spawnTime;

        SpawnInstruction(Enemy.Type enemy, float spawnTime) {
            this.enemy = enemy;
            this.spawnTime = spawnTime;
        }
    }
}