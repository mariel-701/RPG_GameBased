package enemies;

public class DragonBoss extends Enemy {

    public DragonBoss() {
        super("Dragon", 200, 35, 20, 100);
    }

    public int attack() {
        return getEffectiveAttack() + 15;
    }

    public int useSkill() {
        // Fire Breath - massive damage
        return getEffectiveAttack() + 40;
    }

    public int buffDamage() {
        // Dragon's Fury - devastating strike
        return getEffectiveAttack() + 25;
    }

    public String getSkillName() { return "Fire Breath"; }

    public String getBuffName() { return "Dragon's Fury"; }

    public String getTauntName() { return "Roar"; }

    public int getTauntChance() { return 80; }
}
