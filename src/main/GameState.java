package main;

import characters.Character;
import items.Item;
import items.Item.EffectType;
import items.EmptyInventoryException;
import java.util.ArrayList;

public class GameState {
    private ArrayList<Character> party;
    private ArrayList<Item> inventory;
    private int gold;
    private int currentWave;
    private int enemiesDefeated;
    private int turnsTaken;

    private static final String[] STARTER_ITEM_NAMES = {
        "Health Potion", "Mana Elixir", "Revive Scroll"
    };
    private static final String[] STARTER_ITEM_DESCS = {
        "Restores 30 HP", "Reduces skill cooldown by 1", "Revives a fallen party member with 50% HP"
    };
    private static final EffectType[] STARTER_ITEM_EFFECTS = {
        EffectType.HEAL_HP, EffectType.RESTORE_MP, EffectType.REVIVE
    };

    public GameState() {
        this.party = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.gold = 100;
        this.currentWave = 1;
        this.enemiesDefeated = 0;
        this.turnsTaken = 0;

            // Starter item gold values (cheaper versions of shop items)
        int[] starterValues = {30, 30, 50};
        // Start with 3 consumable items
        for (int i = 0; i < 3; i++) {
            inventory.add(new Item(
                STARTER_ITEM_NAMES[i],
                STARTER_ITEM_DESCS[i],
                STARTER_ITEM_EFFECTS[i],
                starterValues[i]
            ));
        }
    }

    // ===================== PARTY =====================
    public ArrayList<Character> getParty() { return party; }
    public void setParty(ArrayList<Character> party) { this.party = party; }

    public boolean allPartyDead() {
        for (Character c : party) {
            if (c.isAlive()) return false;
        }
        return true;
    }

    public ArrayList<Character> getAliveParty() {
        ArrayList<Character> alive = new ArrayList<>();
        for (Character c : party) {
            if (c.isAlive()) alive.add(c);
        }
        return alive;
    }

    /** Get next alive party member for a round-robin turn order */
    public Character getNextAliveCharacter(int currentIndex) {
        ArrayList<Character> alive = getAliveParty();
        if (alive.isEmpty()) return null;
        // Find index within alive list
        for (int i = 0; i < alive.size(); i++) {
            if (party.indexOf(alive.get(i)) > currentIndex) {
                return alive.get(i);
            }
        }
        return alive.get(0); // wrap around
    }

    // ===================== INVENTORY =====================
    public ArrayList<Item> getInventory() { return inventory; }
    public void setInventory(ArrayList<Item> inventory) { this.inventory = inventory; }

    public boolean hasItems() { return !inventory.isEmpty(); }

    public Item useItem(int index) throws EmptyInventoryException {
        if (inventory.isEmpty()) {
            throw new EmptyInventoryException("Your inventory is empty!");
        }
        if (index < 0 || index >= inventory.size()) {
            throw new EmptyInventoryException("Invalid item selection!");
        }
        return inventory.remove(index);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    // ===================== GOLD =====================
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }
    public void addGold(int amount) { this.gold = Math.max(0, this.gold + amount); }
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    // ===================== WAVE & SCORE =====================
    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int wave) { this.currentWave = wave; }
    public void nextWave() { this.currentWave++; }

    public int getEnemiesDefeated() { return enemiesDefeated; }
    public void addEnemyDefeated() { this.enemiesDefeated++; }

    public int getTurnsTaken() { return turnsTaken; }
    public void addTurn() { this.turnsTaken++; }
}
