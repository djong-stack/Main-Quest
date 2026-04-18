package main;

import java.util.Random;

//Base enemy type shared by all enemy subclasses
public class Enemy extends Character {

    private final int expReward;
    private final String imageKey;

    public Enemy(String name, int health, int attack, int speed, int expReward, String imageKey) {
        super(name, health, attack, speed, 0, 0, 0);
        this.expReward = expReward;
        this.imageKey = imageKey;
    }

    public int getExpReward() {
        return expReward;
    }

    public String getImageKey() {
        return imageKey;
    }

    public EnemyTurnResult takeTurn(Character target, Random random) {
        //Default enemy behavior is a single basic attack
        int attemptedDamage = getAttack();
        int damage = target.applyDamage(attemptedDamage);
        return new EnemyTurnResult(getName() + " attacks for " + damage + " damage.", false, true, attemptedDamage);
    }

    public Item getDrop(Random random) {
        return null;
    }
}
