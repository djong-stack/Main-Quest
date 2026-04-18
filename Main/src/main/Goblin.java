package main;

import java.util.Random;

//Fast enemy that can chain turns or spend SP for stronger hits
public class Goblin extends Enemy {

    public Goblin(Random random, Character player) {
        super("Goblin",
                randomStat(random, 25, 30) + (player.getLevel() * 5),
                randomStat(random, 20, 30) + (player.getLevel() * 5),
                randomStat(random, 50, 70) + (player.getLevel() * 10),
                30,
                "goblin");
        setSkillPointState(player.getSkillPoints(), player.getMaxSkillPoints());
    }

    @Override
    public EnemyTurnResult takeTurn(Character target, Random random) {
        if (getSkillPoints() >= 2 && random.nextInt(100) < 35) {
            spendSkillPoint(2);
            int attemptedDamage = getAttack() * 2;
            int damage = target.applyDamage(attemptedDamage);
            return new EnemyTurnResult("Goblin spends 2 SP and strikes for " + damage + " damage.", false, true, attemptedDamage);
        }

        int attemptedDamage = getAttack();
        int damage = target.applyDamage(attemptedDamage);
        if (getSkillPoints() >= 1 && random.nextInt(100) < 40) {
            spendSkillPoint();
            return new EnemyTurnResult("Goblin spends 1 SP, deals " + damage + " damage, and steals another turn.", true, true, attemptedDamage);
        }

        return new EnemyTurnResult("Goblin stabs for " + damage + " damage.", false, true, attemptedDamage);
    }

    @Override
    public Item getDrop(Random random) {
        return random.nextInt(100) < 55 ? Item.createLesserHpPotion() : null;
    }

    private static int randomStat(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
