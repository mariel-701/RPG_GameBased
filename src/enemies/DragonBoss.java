package enemies;

public class DragonBoss extends Enemy {

    public DragonBoss() {
        super("Dragon", 200, 35, 20, 100, 50);
    }

    @Override
    public int attack() {
        return getAttackPower() + 15;
    }

    @Override
    public int useSkill() {
        // Fire Breath - massive damage
        return getAttackPower() + 40;
    }

    @Override
    public int buffDamage() {
        // Dragon's Fury - devastating strike
        return getAttackPower() + 25;
    }

    @Override
    public String getSkillName() { return "Fire Breath"; }

    @Override
    public String getBuffName() { return "Dragon's Fury"; }

    @Override
    public String getTauntName() { return "Roar"; }

    @Override
    public int getTauntChance() { return 80; }
}
