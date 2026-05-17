package characters;

public class Mage extends Character {

    public Mage(String name) {
        super(name, 100, 40);
    }

    @Override
    public int attack() {
        return getAttackPower() + 20;
    }

    @Override
    public int useSkill() {
        return getAttackPower() + 60;
    }
}