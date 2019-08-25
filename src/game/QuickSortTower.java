package game;

import control.App;
import control.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class QuickSortTower extends Tower {
    public QuickSortTower(float x, float y, Type type) {
        super(x, y, type);
    }

    @Override
    protected void shoot(Enemy target) {
        App.getWorld().addEntity(new Lightning(target, Lightning.ARC_CHANCE_DEFAULT));
    }

    private static class Lightning extends Entity {
        private static int ARC_TIME = 400, DAMAGE_TIME = 1000, DAMAGE = 1;
        private static float ARC_DIST = Tower.Type.QUICK.getRange();
        private static double ARC_CHANCE_DIFF = 0.15, ARC_CHANCE_DEFAULT = 1;
        private Enemy target;
        private int arcTimer = 0, damageTimer = 0;
        private double arcChance;

        private Lightning(Enemy target, double arcChance) {
            // TODO: add parent field to track drawing lightning particles back to the source
            super(target.getX(), target.getY(), target.getWidth(), target.getHeight());
            this.target = target;
            this.arcChance = arcChance;
        }

        /** Periodically arc to other nearby enemies. */
        @Override
        public void update(int delta) {
            if (target.isDead()) {
                kill();
            } else {
                teleport(target.getX(), target.getY());
                arcTimer += delta;
                while (arcTimer >= ARC_TIME) {
                    arcTimer -= ARC_TIME;
                    // Randomly either die or look for a nearby enemy and arc to it
                    if (Math.random() > arcChance) {
                        kill();
                    } else {
                        World world = App.getWorld();
                        App.getWorld().getAll(Enemy.class)
                            .filter(e -> distanceTo(e) < ARC_DIST && e != target)
                            .min((e1, e2) -> (int) (distanceTo(e2) - distanceTo(e1)))
                            .ifPresent(e -> world.addEntity(new Lightning(e, arcChance - ARC_CHANCE_DIFF)));
                    }
                }
                damageTimer += delta;
                while (damageTimer >= DAMAGE_TIME) {
                    damageTimer -= DAMAGE_TIME;
                    // Damage our target
                    target.takeDamage(DAMAGE);
                }
            }
        }

        /** Draw the lightning effect. */
        @Override
        public void render(Graphics g) {
            // TODO: implement this properly, either drawing for each target or making a new Lightning for each one
            g.setColor(Color.yellow);
            g.drawOval(getLeft(), getTop(), getWidth(), getHeight());
        }
    }
}
