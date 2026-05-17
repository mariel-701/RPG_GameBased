package characters;

public class Mage extends Character {

    public Mage(String name) {
        super(name, 100, 100, 40, 5);
    }

    @Override
    public int attack() {
        return getAttackPower() + (int)(Math.random() * 20);
    }

    @Override
    public int useSkill() {
        // Fireball: 3x damage
        int baseDamage = getAttackPower() * 3;
        // Arcane Surge bonus: +50% when MP > 50%
        if (getMp() > getMaxMp() / 2) {
            baseDamage = (int)(baseDamage * 1.5);
        }
        return baseDamage;
    }

    @Override
    public int getCritChance() {
        return 15; // Mages are precise
    }

    @Override
    public String getPassiveDescription() {
        return "Arcane Surge: Skills deal 50% more damage when MP is above 50%";
    }

    @Override
    public String getSkillDescription() {
        return "Fireball: Deals massive 3x fire damage. Costs 35 MP";
    }

    @Override
    public int getSkillMpCost() {
        return 35;
    }
}
