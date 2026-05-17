package characters;

public abstract class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int attackPower;
    private int defensePower;
    private boolean alive;

    public Character(String name, int hp, int mp, int attackPower, int defensePower) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.mp = mp;
        this.maxMp = mp;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.alive = true;
    }

    // ========== GETTERS ==========
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }
    public boolean isAlive() { return alive; }

    // ========== SETTERS ==========
    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
        if (this.hp <= 0) {
            this.alive = false;
        }
    }

    public void setMp(int mp) {
        this.mp = Math.max(0, Math.min(mp, maxMp));
    }

    public void setDefensePower(int defensePower) {
        this.defensePower = Math.max(0, defensePower);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        if (alive && this.hp <= 0) {
            this.hp = 1;
        }
    }

    public void revive(int percentHp) {
        this.alive = true;
        this.hp = (int)(maxHp * percentHp / 100.0);
        if (this.hp <= 0) this.hp = 1;
    }

    public void restoreHp(int amount) {
        setHp(this.hp + amount);
    }

    public void restoreMp(int amount) {
        setMp(this.mp + amount);
    }

    // ========== ABSTRACT METHODS ==========
    public abstract int attack();
    public abstract int useSkill();
    public abstract String getPassiveDescription();
    public abstract String getSkillDescription();
    public abstract int getSkillMpCost();

    // ========== PASSIVE HOOKS (overridable) ==========
    /** Modify outgoing attack damage */
    public int onAttackDamage(int damage) { return damage; }

    /** Modify incoming damage (for defense passives) */
    public int onTakeDamage(int damage) {
        return Math.max(1, damage - defensePower / 2);
    }

    /** Base crit chance override */
    public int getCritChance() { return 10; }

    /** Base hit chance override */
    public int getHitChance() { return 90; }

    /** Called when crit lands */
    public int onCritDamage(int damage) { return damage * 2; }

    // ========== UTILITY ==========
    @Override
    public String toString() {
        return name + " [HP: " + hp + "/" + maxHp + " MP: " + mp + "/" + maxMp + "]";
    }

    public String getShortStatus() {
        String status = name + " HP:" + hp + "/" + maxHp + " MP:" + mp + "/" + maxMp;
        if (!alive) status += " [DOWN]";
        return status;
    }
}
