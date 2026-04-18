package main;

// Holds the stat changes produced by a level-up.
public class LevelUpInfo {

    private final int newLevel;
    private final int healthGain;
    private final int attackGain;
    private final int speedGain;
    private final int skillPointCapGain;
    private final int maxSkillPoints;

    public LevelUpInfo(int newLevel, int healthGain, int attackGain, int speedGain, int skillPointCapGain, int maxSkillPoints) {
        this.newLevel = newLevel;
        this.healthGain = healthGain;
        this.attackGain = attackGain;
        this.speedGain = speedGain;
        this.skillPointCapGain = skillPointCapGain;
        this.maxSkillPoints = maxSkillPoints;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getHealthGain() {
        return healthGain;
    }

    public int getAttackGain() {
        return attackGain;
    }

    public int getSpeedGain() {
        return speedGain;
    }

    public int getSkillPointCapGain() {
        return skillPointCapGain;
    }

    public int getMaxSkillPoints() {
        return maxSkillPoints;
    }
}
