package main;

import java.util.Random;

//Heavy enemy with strong damage and stun chances
public class Ogre extends Enemy {

    public Ogre(Random random, Character player) {
        super("Ogre",
                randomStat(random, 65, 85) + (player.getLevel() * 10),
                randomStat(random, 60, 75) + (player.getLevel() * 5),
                randomStat(random, 10, 20) + (player.getLevel() * 5),
                75,
                "ogre");
        setSkillPointState(player.getSkillPoints(), player.getMaxSkillPoints());
    }

    @Override
    public EnemyTurnResult takeTurn(Character target, Random random) {
        if (getSkillPoints() >= 2 && random.nextInt(100) < 35) {
            spendSkillPoint(2);
            int attemptedDamage = getAttack();
            int damage = target.applyDamage(attemptedDamage);
            boolean stunned = random.nextInt(100) < 70;
            if (stunned) {
                target.stun();
            }
            return new EnemyTurnResult("Ogre spends 2 SP, deals " + damage + " damage, and "
                    + (stunned ? "stuns you." : "fails to stun you."), false, true, attemptedDamage);
        }

        if (getSkillPoints() >= 1 && random.nextInt(100) < 45) {
            spendSkillPoint();
            int attemptedDamage = getAttack() * 2;
            int damage = target.applyDamage(attemptedDamage);
            boolean stunned = random.nextInt(100) < 20;
            if (stunned) {
                target.stun();
            }
            return new EnemyTurnResult("Ogre spends 1 SP, crushes you for " + damage + " damage, and "
                    + (stunned ? "stuns you." : "does not stun you."), false, true, attemptedDamage);
        }

        int attemptedDamage = getAttack();
        int damage = target.applyDamage(attemptedDamage);
        return new EnemyTurnResult("Ogre clubs you for " + damage + " damage.", false, true, attemptedDamage);
    }

    @Override
    public Item getDrop(Random random) {
        return random.nextInt(100) < 70 ? Item.createHpPotion() : null;
    }

    private static int randomStat(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
