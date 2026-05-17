package enemies;

import java.util.Random;

public class Enemy {
    private final String name;
    private int hp;
    private final int maxHp;
    private final int baseAttackPower;
    private final int defense;
    private final int baseGoldDrop;
    private int attackBuff = 0;
    private boolean taunting = false;
    private final Random random = new Random();

    public enum Action {
        ATTACK,
        BUFF,
        TAUNT
    }

    public Enemy(String name, int hp, int attackPower, int defense, int goldDrop) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.baseAttackPower = attackPower;
        this.defense = defense;
        this.baseGoldDrop = goldDrop;
    }

    // ========== GETTERS ==========
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDefense() { return defense; }
    public int getAttackBuff() { return attackBuff; }
    public int getEffectiveAttack() { return baseAttackPower + attackBuff; }
    public boolean isAlive() { return hp > 0; }
    public boolean isTaunting() { return taunting; }

    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public void setTaunting(boolean taunting) { this.taunting = taunting; }

    public void takeDamage(int damage) {
        int netDamage = Math.max(1, damage - defense / 2);
        this.hp = Math.max(0, this.hp - netDamage);
    }

    public void applyBuff() {
        attackBuff += 5;
    }

    public int getGoldDrop() {
        return baseGoldDrop + random.nextInt(10);
    }

    // ========== ENEMY AI ==========
    public Action chooseAction() {
        return Action.values()[random.nextInt(3)];
    }

    public int calculateAttackDamage() {
        return getEffectiveAttack() + random.nextInt(10);
    }

    public boolean attemptTaunt() {
        return random.nextInt(100) < 60; // 60% taunt success rate
    }

    public String getStatus() {
        return name + " HP: " + hp + "/" + maxHp
            + (attackBuff > 0 ? " (+" + attackBuff + " ATK)" : "")
            + (taunting ? " [TAUNTING]" : "");
    }
}
