package items;

public class Item {
    private final String name;
    private final String description;
    private final EffectType effect;
    private final int value;

    public enum EffectType {
        HEAL_HP,
        RESTORE_MP,
        REVIVE
    }

    public Item(String name, String description, EffectType effect, int value) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.value = value;
    }

    // ========== GETTERS ONLY (no setters - immutable) ==========
    public String getName() { return name; }
    public String getDescription() { return description; }
    public EffectType getEffect() { return effect; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return name + " - " + description;
    }
}
