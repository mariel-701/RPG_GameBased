package characters;

public class Warrior extends Character {

    public Warrior(String name) {
        super(name, 150, 30);
    }

    @Override
    public int attack() {
        return getAttackPower() + 10;
    }

    @Override
    public int useSkill() {
        return getAttackPower() + 40;
    }
}