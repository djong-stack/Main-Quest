package main;

import java.util.Random;

//Magic enemy that can shield itself or stun the player
public class WizardEnemy extends Enemy {

    public WizardEnemy(Random random, Character player) {
        super("Wizard",
                randomStat(random, 30, 40) + (player.getLevel() * 5),
                randomStat(random, 40, 60) + (player.getLevel() * 10),
                randomStat(random, 30, 40) + (player.getLevel() * 5),
                45,
                "wizard");
        setSkillPointState(player.getSkillPoints(), player.getMaxSkillPoints());
    }

    @Override
    public EnemyTurnResult takeTurn(Character target, Random random) {
        if (getSkillPoints() >= 2 && random.nextInt(100) < 35) {
            spendSkillPoint(2);
            target.stun();
            return new EnemyTurnResult("Wizard spends 2 SP and stuns you.", false, false, 0);
        }

        if (getSkillPoints() >= 1 && random.nextInt(100) < 45) {
            spendSkillPoint();
            prepareBlockNextDamage();
            return new EnemyTurnResult("Wizard spends 1 SP to shield the next hit.", false, false, 0);
        }

        int attemptedDamage = getAttack();
        int damage = target.applyDamage(attemptedDamage);
        return new EnemyTurnResult("Wizard blasts you for " + damage + " damage.", false, true, attemptedDamage);
    }

    @Override
    public Item getDrop(Random random) {
        return random.nextInt(100) < 45 ? Item.createSpPotion() : null;
    }

    private static int randomStat(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
