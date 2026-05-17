package characters;

public class Archer extends Character {

    public Archer(String name) {
        super(name, 120, 60, 35, 8);
    }

    @Override
    public int attack() {
        return getAttackPower() + (int)(Math.random() * 12);
    }

    @Override
    public int useSkill() {
        // Rain of Arrows: hits 3 times
        int total = 0;
        for (int i = 0; i < 3; i++) {
            total += (getAttackPower() / 2) + (int)(Math.random() * 10);
        }
        return total;
    }

    @Override
    public int getCritChance() {
        return 35; // Eagle Eye: base 10% + 25% from passive
    }

    @Override
    public String getPassiveDescription() {
        return "Eagle Eye: +25% crit chance on attacks (total 35%)";
    }

    @Override
    public String getSkillDescription() {
        return "Rain of Arrows: Hits the enemy 3 times. Costs 25 MP";
    }

    @Override
    public int getSkillMpCost() {
        return 25;
    }
}
