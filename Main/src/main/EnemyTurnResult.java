package main;

//To return the result of an enemy action
public class EnemyTurnResult {

    private final String message;
    private final boolean takesExtraTurn;
    private final boolean attackedTarget;
    private final int attemptedDamage;

    public EnemyTurnResult(String message, boolean takesExtraTurn, boolean attackedTarget, int attemptedDamage) {
        this.message = message;
        this.takesExtraTurn = takesExtraTurn;
        this.attackedTarget = attackedTarget;
        this.attemptedDamage = attemptedDamage;
    }

    public String getMessage() {
        return message;
    }

    public boolean takesExtraTurn() {
        return takesExtraTurn;
    }

    public boolean attackedTarget() {
        return attackedTarget;
    }

    public int getAttemptedDamage() {
        return attemptedDamage;
    }
}
