package characters;

public class Warrior extends Character {

    public Warrior(String name) {
        super(name, 150, 50, 30, 15);
    }

    @Override
    public int attack() {
        return getAttackPower() + (int)(Math.random() * 15);
    }

    @Override
    public int useSkill() {
        // Shield Bash: 2x damage + ignores enemy defense
        return (getAttackPower() * 2) + 20;
    }

    @Override
    public int onTakeDamage(int damage) {
        // Iron Will passive: reduce incoming damage by 20%, then subtract defense
        int reduced = (int)(damage * 0.8);
        return Math.max(1, reduced - getDefensePower() / 2);
    }

    @Override
    public int getCritChance() {
        return 5; // Warriors are less precise but hit harder
    }

    @Override
    public String getPassiveDescription() {
        return "Iron Will: Reduces all incoming damage by 20%";
    }

    @Override
    public String getSkillDescription() {
        return "Shield Bash: Deal 2x damage ignoring enemy defense. Costs 20 MP";
    }

    @Override
    public int getSkillMpCost() {
        return 20;
    }
}
