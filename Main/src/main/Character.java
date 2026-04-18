package main;

import java.util.ArrayList;
import java.util.List;

//Abstract parent class for every playable character and enemy
public abstract class Character {

    private final String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int speed;
    private final int healthGrowth;
    private final int attackGrowth;
    private final int speedGrowth;
    private int level;
    private int exp;
    private int skillPoints;
    private int maxSkillPoints;
    private boolean negateNextDamage;
    private double nextDamageMultiplier;
    private boolean counterReady;
    private double counterMultiplier;
    private boolean stunned;

    public Character(String name, int health, int attack, int speed, int healthGrowth, int attackGrowth, int speedGrowth) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.speed = speed;
        this.healthGrowth = healthGrowth;
        this.attackGrowth = attackGrowth;
        this.speedGrowth = speedGrowth;
        this.level = 1;
        this.exp = 0;
        this.maxSkillPoints = calculateMaxSkillPoints(level);
        this.skillPoints = maxSkillPoints;
        this.negateNextDamage = false;
        this.nextDamageMultiplier = 1.0;
        this.counterReady = false;
        this.counterMultiplier = 0.0;
        this.stunned = false;
    }

    public void attack(Character target) {
        //Attack behavior uses the current attack stat
        target.applyDamage(getAttack());
    }

    public List<LevelUpInfo> gainExp(int amount) {
        ArrayList<LevelUpInfo> levelUps = new ArrayList<>();
        exp += amount;

        //A single battle can cause multiple level-ups if enough EXP is earned
        while (exp >= expToNextLevel()) {
            exp -= expToNextLevel();
            levelUps.add(levelUp());
        }

        return levelUps;
    }

    private int expToNextLevel() {
        return level * 50;
    }

    protected LevelUpInfo levelUp() {
        int previousMaxSkillPoints = maxSkillPoints;

        //Each subclass uses growth values passed through its constructor
        level++;
        maxHealth += healthGrowth;
        health = Math.min(maxHealth, health + healthGrowth);
        attack += attackGrowth;
        speed += speedGrowth;
        maxSkillPoints = calculateMaxSkillPoints(level);
        skillPoints = Math.min(skillPoints, maxSkillPoints);

        return new LevelUpInfo(level, healthGrowth, attackGrowth, speedGrowth, maxSkillPoints - previousMaxSkillPoints, maxSkillPoints);
    }

    private int calculateMaxSkillPoints(int currentLevel) {
        return 1 + (currentLevel / 4);
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttack() {
        return attack;
    }

    public int getSpeed() {
        return speed;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToNextLevel() {
        return level * 50;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getMaxSkillPoints() {
        return maxSkillPoints;
    }

    public void setSkillPointState(int currentSkillPoints, int maxSkillPoints) {
        //Used to sync enemy SP with the player's current/max SP
        this.maxSkillPoints = Math.max(1, maxSkillPoints);
        this.skillPoints = Math.max(0, Math.min(currentSkillPoints, this.maxSkillPoints));
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public int applyDamage(int damage) {
        if (damage <= 0) {
            return 0;
        }

        //Some abilities completely cancel the next incoming hit
        if (negateNextDamage) {
            negateNextDamage = false;
            nextDamageMultiplier = 1.0;
            return 0;
        }

        int adjustedDamage = Math.max(0, (int) Math.ceil(damage * nextDamageMultiplier));
        nextDamageMultiplier = 1.0;
        int actualDamage = Math.min(adjustedDamage, health);
        setHealth(health - actualDamage);
        return actualDamage;
    }

    public void restoreHealth(int amount) {
        if (amount > 0) {
            setHealth(health + amount);
        }
    }

    public void restoreHealthPercent(double percent) {
        //For abilities that heal based on % max HP
        int amount = (int) Math.ceil(maxHealth * percent);
        restoreHealth(amount);
    }

    public void restoreFullHealth() {
        health = maxHealth;
    }

    public boolean spendSkillPoint() {
        return spendSkillPoint(1);
    }

    public boolean spendSkillPoint(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (skillPoints < amount) {
            return false;
        }
        skillPoints -= amount;
        return true;
    }

    public void restoreSkillPoints(int amount) {
        if (amount > 0) {
            skillPoints = Math.min(maxSkillPoints, skillPoints + amount);
        }
    }

    public void restoreAllSkillPoints() {
        skillPoints = maxSkillPoints;
    }

    public void prepareHalfDamage() {
        //Marks the next incoming hit to be reduced by half
        nextDamageMultiplier = Math.min(nextDamageMultiplier, 0.5);
    }

    public void prepareBlockNextDamage() {
        //Marks the next incoming hit to be fully negated
        negateNextDamage = true;
        nextDamageMultiplier = 1.0;
    }

    public void prepareCounter(double multiplier) {
        counterReady = true;
        counterMultiplier = multiplier;
    }

    public boolean hasCounterReady() {
        return counterReady;
    }

    public int consumeCounterDamage() {
        if (!counterReady) {
            return 0;
        }
        counterReady = false;
        return Math.max(1, (int) Math.ceil(getAttack() * counterMultiplier));
    }

    public void stun() {
        //Stunned targets lose their next action
        stunned = true;
    }

    public boolean consumeStun() {
        boolean wasStunned = stunned;
        stunned = false;
        return wasStunned;
    }

    public boolean isStunned() {
        return stunned;
    }

    public int loseExp(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int lost = Math.min(exp, amount);
        exp -= lost;
        return lost;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
