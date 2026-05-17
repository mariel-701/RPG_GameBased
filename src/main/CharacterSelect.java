package main;

import characters.Warrior;
import characters.Mage;
import characters.Archer;
import items.Item;
import items.Item.EffectType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelect extends JPanel {

    private final JFrame frame;

    // Card components
    private final JLabel[] cards = new JLabel[3];
    private final String[] heroTypes = {"Warrior", "Mage", "Archer"};
    private final boolean[] selected = new boolean[3];

    // Animation
    private int[] cardX = {-250, -250, -250};
    private final int[] targetX = {150, 450, 750};
    private Timer animTimer;

    // Start button
    private JButton startButton;
    private JButton loadButton;
    private JLabel instructionLabel;

    public CharacterSelect(JFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(Color.BLACK);

        // ========== TITLE ==========
        JLabel title = new JLabel("SELECT YOUR HEROES");
        title.setBounds(0, 20, 1200, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        add(title);

        // ========== INSTRUCTION ==========
        instructionLabel = new JLabel("Click on 2 heroes to build your party");
        instructionLabel.setBounds(0, 70, 1200, 30);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(180, 180, 180));
        add(instructionLabel);

        // ========== HERO CARDS ==========
        String[] imagePaths = {
            "assets/backgrounds/characters/warrior.png",
            "assets/backgrounds/characters/mage.png",
            "assets/backgrounds/characters/archer.png"
        };

        for (int i = 0; i < 3; i++) {
            cards[i] = createHeroCard(i, heroTypes[i], imagePaths[i]);
            add(cards[i]);
        }

        // ========== START BUTTON ==========
        startButton = new JButton("START BATTLE");
        startButton.setBounds(450, 520, 300, 60);
        styleButton(startButton, new Color(50, 150, 50));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        // ========== LOAD BUTTON ==========
        loadButton = new JButton("LOAD SAVED GAME");
        loadButton.setBounds(450, 600, 300, 40);
        styleButton(loadButton, new Color(70, 70, 120));
        loadButton.setEnabled(SaveManager.saveExists());
        loadButton.addActionListener(e -> loadGame());
        add(loadButton);

        // ========== INITIAL POSITIONS ==========
        updatePositions();

        // ========== SLIDE-IN ANIMATION ==========
        animTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean allDone = true;
                for (int i = 0; i < 3; i++) {
                    if (cardX[i] < targetX[i]) {
                        cardX[i] += 12;
                        if (cardX[i] > targetX[i]) cardX[i] = targetX[i];
                        allDone = false;
                    }
                }
                updatePositions();
                repaint();
                if (allDone) animTimer.stop();
            }
        });
        animTimer.start();
    }

    private JLabel createHeroCard(int index, String name, String imagePath) {
        JLabel card = new JLabel();
        card.setLayout(new BorderLayout());

        // Try loading image
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(160, 200, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(imgLabel, BorderLayout.CENTER);
        }

        // Name label
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        card.add(nameLabel, BorderLayout.SOUTH);

        // Click to select
        int idx = index;
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                toggleSelection(idx);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!selected[idx]) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 200), 3));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!selected[idx]) {
                    card.setBorder(null);
                }
            }
        });

        return card;
    }

    private void toggleSelection(int index) {
        int selectedCount = getSelectedCount();

        if (selected[index]) {
            // Deselect
            selected[index] = false;
            cards[index].setBorder(null);
        } else {
            if (selectedCount >= 2) {
                // Already have 2 selected — show message
                instructionLabel.setText("⚠️ You can only select 2 heroes!");
                instructionLabel.setForeground(new Color(255, 200, 100));
                return;
            }
            // Select
            selected[index] = true;
            cards[index].setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));
        }

        // Update button state
        int newCount = getSelectedCount();
        startButton.setEnabled(newCount == 2);

        if (newCount == 2) {
            instructionLabel.setText("✅ 2 heroes selected! Click START or tap heroes to change");
            instructionLabel.setForeground(new Color(100, 255, 100));
        } else {
            instructionLabel.setText("Select " + (2 - newCount) + " more hero" + (newCount == 1 ? "" : "es"));
            instructionLabel.setForeground(new Color(180, 180, 180));
        }
    }

    private int getSelectedCount() {
        int count = 0;
        for (boolean s : selected) if (s) count++;
        return count;
    }

    private void startGame() {
        // Get selected hero types
        List<String> selectedHeroes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (selected[i]) {
                selectedHeroes.add(heroTypes[i]);
            }
        }

        if (selectedHeroes.size() != 2) return;

        // Ask for names
        String name1 = JOptionPane.showInputDialog(frame,
            "Name your first hero (" + selectedHeroes.get(0) + "):",
            "Character Name",
            JOptionPane.PLAIN_MESSAGE);
        if (name1 == null || name1.trim().isEmpty()) name1 = selectedHeroes.get(0);

        String name2 = JOptionPane.showInputDialog(frame,
            "Name your second hero (" + selectedHeroes.get(1) + "):",
            "Character Name",
            JOptionPane.PLAIN_MESSAGE);
        if (name2 == null || name2.trim().isEmpty()) name2 = selectedHeroes.get(1);

        // Create characters
        List<characters.Character> party = new ArrayList<>();
        party.add(createHero(selectedHeroes.get(0), name1.trim()));
        party.add(createHero(selectedHeroes.get(1), name2.trim()));

        // Starting inventory: 3 consumable items per requirements
        List<Item> startingItems = new ArrayList<>();
        startingItems.add(new Item("Health Potion", "Restores 50 HP", EffectType.HEAL_HP, 50));
        startingItems.add(new Item("Mana Elixir", "Restores 40 MP", EffectType.RESTORE_MP, 40));
        startingItems.add(new Item("Revive Scroll", "Revives a fallen ally with 50% HP", EffectType.REVIVE, 80));

        // Start game
        frame.setContentPane(new GamePanel(frame, party, 1, 0, startingItems, 0, 0));
        frame.revalidate();
    }

    private void loadGame() {
        SaveManager.SaveData data = SaveManager.loadGame();
        if (data == null || !data.isValid()) {
            JOptionPane.showMessageDialog(frame,
                "No valid save file found!",
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.setContentPane(new GamePanel(
            frame,
            data.party,
            data.wave,
            data.gold,
            data.inventory,
            data.scoreEnemies,
            data.scoreTurns
        ));
        frame.revalidate();
    }

    private characters.Character createHero(String type, String name) {
        switch (type) {
            case "Warrior": return new Warrior(name);
            case "Mage":    return new Mage(name);
            case "Archer":  return new Archer(name);
            default:        return new Warrior(name);
        }
    }

    private void updatePositions() {
        for (int i = 0; i < 3; i++) {
            // Cards centered vertically, spaced horizontally with fixed size 200x280
            cards[i].setBounds(cardX[i], 120, 200, 280);
        }
    }

    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
    }
}
