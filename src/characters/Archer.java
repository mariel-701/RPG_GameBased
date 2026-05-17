package characters;

public class Archer extends Character {

    public Archer(String name) {
        super(name, 120, 35);
    }

    @Override
    public int attack() {
        return getAttackPower() + 15;
    }

    @Override
    public int useSkill() {
        return getAttackPower() + 50;
    }
}