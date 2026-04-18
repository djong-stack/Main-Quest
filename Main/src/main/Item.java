package main;

//Usable inventory items
public class Item implements Usable {

    private final String name;
    private final int healAmount;
    private final boolean restoreAllSkillPoints;

    public Item(String name, int healAmount) {
        this(name, healAmount, false);
    }

    public Item(String name, int healAmount, boolean restoreAllSkillPoints) {
        this.name = name;
        this.healAmount = healAmount;
        this.restoreAllSkillPoints = restoreAllSkillPoints;
    }

    @Override
    public void use(Character target) {
        //SP potions fully restore SP; HP items restore health only
        if (restoreAllSkillPoints) {
            target.restoreAllSkillPoints();
            return;
        }

        target.restoreHealth(healAmount);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (restoreAllSkillPoints) {
            return name + " restores all skill points.";
        }
        return name + " heals for " + healAmount + " HP.";
    }

    public static Item createLesserHpPotion() {
        //Method for the weakest healing potion
        return new Item("Lesser HP Potion", 20);
    }

    public static Item createHpPotion() {
        return new Item("HP Potion", 50);
    }

    public static Item createSpPotion() {
        return new Item("SP Potion", 0, true);
    }
}
