package main;

import characters.Warrior;
import characters.Mage;
import characters.Archer;
import enemies.*;
import items.Item;
import items.Item.EffectType;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading game state to/from a text file.
 */
public class SaveManager {

    private static final String SAVE_FILE = "rpg_save.txt";

    // ========== SAVE ==========
    public static boolean saveGame(
        int wave,
        int gold,
        int scoreEnemiesDefeated,
        int scoreTurnsTaken,
        List<characters.Character> party,
        List<Item> inventory
    ) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("wave:" + wave);
            writer.println("gold:" + gold);
            writer.println("scoreEnemies:" + scoreEnemiesDefeated);
            writer.println("scoreTurns:" + scoreTurnsTaken);

            writer.println("party:");
            for (characters.Character c : party) {
                // Format: ClassName:Name:hp:mp:alive
                String className = c.getClass().getSimpleName();
                writer.println(className + ":" + c.getName() + ":" + c.getHp() + ":" + c.getMp() + ":" + c.isAlive());
            }

            writer.println("inventory:");
            for (Item item : inventory) {
                // Format: Name:EffectType:Value
                writer.println(item.getName() + ":" + item.getEffect().name() + ":" + item.getValue());
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ========== LOAD ==========
    public static SaveData loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return null;

        SaveData data = new SaveData();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("party:")) {
                    section = "party";
                    continue;
                } else if (line.equals("inventory:")) {
                    section = "inventory";
                    continue;
                }

                switch (section) {
                    case "":
                        if (line.startsWith("wave:")) data.wave = parseInt(line, "wave:");
                        else if (line.startsWith("gold:")) data.gold = parseInt(line, "gold:");
                        else if (line.startsWith("scoreEnemies:")) data.scoreEnemies = parseInt(line, "scoreEnemies:");
                        else if (line.startsWith("scoreTurns:")) data.scoreTurns = parseInt(line, "scoreTurns:");
                        break;

                    case "party":
                        String[] parts = line.split(":");
                        if (parts.length >= 5) {
                            String className = parts[0];
                            String charName = parts[1];
                            int hp = Integer.parseInt(parts[2]);
                            int mp = Integer.parseInt(parts[3]);
                            boolean alive = Boolean.parseBoolean(parts[4]);

                            characters.Character character = createCharacter(className, charName);
                            if (character != null) {
                                character.setHp(hp);
                                character.setMp(mp);
                                if (!alive) character.setAlive(false);
                                data.party.add(character);
                            }
                        }
                        break;

                    case "inventory":
                        String[] invParts = line.split(":");
                        if (invParts.length >= 3) {
                            String itemName = invParts[0];
                            EffectType effect = EffectType.valueOf(invParts[1]);
                            int value = Integer.parseInt(invParts[2]);
                            // Reconstruct the Item — we store description generically
                            String desc = getDefaultDescription(itemName, effect);
                            data.inventory.add(new Item(itemName, desc, effect, value));
                        }
                        break;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return data;
    }

    private static int parseInt(String line, String prefix) {
        try {
            return Integer.parseInt(line.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static characters.Character createCharacter(String className, String name) {
        switch (className) {
            case "Warrior": return new Warrior(name);
            case "Mage":    return new Mage(name);
            case "Archer":  return new Archer(name);
            default:        return null;
        }
    }

    private static String getDefaultDescription(String itemName, EffectType effect) {
        switch (effect) {
            case HEAL_HP:            return "Restores " + (itemName.contains("50") ? "50" : "some") + " HP";
            case RESTORE_MP:            return "Restores " + (itemName.contains("40") ? "40" : "some") + " MP";
            case REVIVE:    return "Revives a fallen ally with 50% HP";
            default:        return "A consumable item";
        }
    }

    // ========== SAVE DATA CONTAINER ==========
    public static class SaveData {
        public int wave = 1;
        public int gold = 0;
        public int scoreEnemies = 0;
        public int scoreTurns = 0;
        public List<characters.Character> party = new ArrayList<>();
        public List<Item> inventory = new ArrayList<>();

        public boolean isValid() {
            return !party.isEmpty();
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }

    public static void deleteSave() {
        new File(SAVE_FILE).delete();
    }
}
