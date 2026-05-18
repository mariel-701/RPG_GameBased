package main;

import characters.Warrior;
import characters.Mage;
import characters.Archer;
import enemies.*;
import items.Item;
import items.EmptyInventoryException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private final JFrame frame;
    private final Random random = new Random();

    // ========== PARTY ==========
    private final List<characters.Character> party;
    private int currentMemberIndex = 0;

    // ========== WAVE SYSTEM ==========
    private int currentWave;
    private static final int MAX_WAVES = 6;
    private List<Enemy> enemies;

    // ========== INVENTORY & ECONOMY ==========
    private final List<Item> inventory;
    private int gold;

    // ========== SCORE ==========
    private int enemiesDefeated = 0;
    private int turnsTaken = 0;

    // ========== UI COMPONENTS ==========
    private final JButton attackButton;
    private final JButton skillButton;
    private final JButton itemButton;
    private final JButton fleeButton;
    private final JButton saveButton;
    private final JLabel statusLabel;
    private final JLabel turnLabel;
    private final JLabel waveLabel;
    private final JLabel goldLabel;

    // ========== VISUAL ASSETS ==========
    private Image background;
    private final Image[] playerImages = new Image[2];
    private Image enemyImage;

    // ========== FLOATING TEXT ==========
    private String floatingText = "";
    private int floatingY = 0;
    private Timer floatTimer;

    // ========== STATE ==========
    private boolean playerTurn = true;
    private boolean taunted = false;
    private boolean battleActive = true;
    private boolean waitingForAction = false;

    // ========== ANIMATION SYSTEM ==========
    private Timer animLoopTimer;
    private long animTick = 0;

    // Hero attack lunge (lunges toward enemy and back)
    private final boolean[] heroAttacking = new boolean[2];
    private final long[] heroAttackStartMs = new long[2];
    private static final int ATTACK_ANIM_MS = 300;

    // Hero skill (bigger lunge)
    private final boolean[] heroSkill = new boolean[2];
    private final long[] heroSkillStartMs = new long[2];
    private static final int SKILL_ANIM_MS = 450;

    // Hero hurt flash (red overlay)
    private final boolean[] heroHurt = new boolean[2];
    private final long[] heroHurtStartMs = new long[2];
    private static final int HURT_ANIM_MS = 350;

    // Enemy hit shake
    private float[] enemyShakeAmount;
    private long[] enemyShakeStartMs;
    private static final int SHAKE_ANIM_MS = 250;

    // ========== COLORS =
    private static final Color DARK_BG = new Color(20, 20, 30);
    private static final Color HP_GREEN = new Color(50, 200, 50);
    private static final Color HP_RED = new Color(200, 50, 50);
    private static final Color MP_BLUE = new Color(50, 100, 255);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color GOLD_COLOR = new Color(255, 215, 0);

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public GamePanel(JFrame frame, List<characters.Character> party, int wave, int gold,
                     List<Item> inventory, int enemiesDefeated, int turnsTaken) {
        this.frame = frame;
        this.party = party;
        this.currentWave = wave;
        this.gold = gold;
        this.inventory = inventory != null ? inventory : new ArrayList<>();
        this.enemiesDefeated = enemiesDefeated;
        this.turnsTaken = turnsTaken;

        setLayout(null);
        setBackground(DARK_BG);

        loadImages();

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 540, 900, 30);
        statusLabel.setForeground(new Color(255, 220, 100));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel);

        turnLabel = new JLabel("");
        turnLabel.setBounds(20, 560, 900, 25);
        turnLabel.setForeground(new Color(150, 200, 255));
        turnLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(turnLabel);

        waveLabel = new JLabel("Wave " + wave + "/" + MAX_WAVES);
        waveLabel.setBounds(1050, 20, 150, 25);
        waveLabel.setForeground(new Color(255, 180, 100));
        waveLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(waveLabel);

        goldLabel = new JLabel("\uD83D\uDCB0 " + gold);
        goldLabel.setBounds(1050, 45, 150, 25);
        goldLabel.setForeground(GOLD_COLOR);
        goldLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(goldLabel);

        attackButton = createButton("\u2694\uFE0F ATTACK", 50, 600);
        skillButton = createButton("\u2728 SKILL", 280, 600);
        itemButton = createButton("\uD83C\uDF92 ITEM", 510, 600);
        fleeButton = createButton("\uD83C\uDFC3 FLEE", 740, 600);

        saveButton = new JButton("\uD83D\uDCBE SAVE");
        saveButton.setBounds(970, 600, 130, 50);
        styleButton(saveButton, new Color(50, 80, 120));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveGame());
        add(saveButton);

        floatTimer = new Timer(50, e -> {
            floatingY -= 1;
            if (floatingY < 150) {
                floatTimer.stop();
                floatingText = "";
            }
            repaint();
        });

        // Initialize enemy shake arrays
        enemyShakeAmount = new float[4];
        enemyShakeStartMs = new long[4];

        // ========== ANIMATION LOOP (30fps) ==========
        animLoopTimer = new Timer(33, e -> {
            animTick++;
            // Clear finished hero hurt states each tick to prevent flicker
            for (int i = 0; i < 2; i++) {
                if (heroHurt[i]) {
                    long elapsed = System.currentTimeMillis() - heroHurtStartMs[i];
                    if (elapsed >= HURT_ANIM_MS) heroHurt[i] = false;
                }
                if (heroAttacking[i]) {
                    long elapsed = System.currentTimeMillis() - heroAttackStartMs[i];
                    if (elapsed >= ATTACK_ANIM_MS) heroAttacking[i] = false;
                }
                if (heroSkill[i]) {
                    long elapsed = System.currentTimeMillis() - heroSkillStartMs[i];
                    if (elapsed >= SKILL_ANIM_MS) heroSkill[i] = false;
                }
            }
            if (enemyShakeAmount != null) {
                for (int i = 0; i < enemyShakeAmount.length; i++) {
                    if (enemyShakeAmount[i] > 0) {
                        long elapsed = System.currentTimeMillis() - enemyShakeStartMs[i];
                        if (elapsed >= SHAKE_ANIM_MS) enemyShakeAmount[i] = 0;
                    }
                }
            }
            repaint();
        });
        animLoopTimer.start();

        setupWave(currentWave);

        startPlayerTurn();
    }

    // ============================================================
    // IMAGE LOADING
    // ============================================================
    private void loadImages() {
        try { background = new ImageIcon(getClass().getResource("/backgrounds/battle.jpg")).getImage(); }
        catch (Exception e) { background = null; }

        try { enemyImage = new ImageIcon(getClass().getResource("/backgrounds/characters/enemy.png")).getImage(); }
        catch (Exception e) { enemyImage = null; }

        for (int i = 0; i < party.size() && i < 2; i++) {
            String heroType = party.get(i).getClass().getSimpleName().toLowerCase();
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("/backgrounds/characters/" + heroType + ".png"));
                playerImages[i] = icon.getImage();
            } catch (Exception e) {
                playerImages[i] = null;
            }
        }
    }

    // ============================================================
    // WAVE SETUP
    // ============================================================
    private void setupWave(int wave) {
        enemies = new ArrayList<>();
        // Reset enemy shake arrays for new wave
        if (enemyShakeAmount == null) enemyShakeAmount = new float[4];
        if (enemyShakeStartMs == null) enemyShakeStartMs = new long[4];
        for (int i = 0; i < enemyShakeAmount.length; i++) enemyShakeAmount[i] = 0;
        switch (wave) {
            case 1: enemies.add(new Goblin()); enemies.add(new Goblin()); break;
            case 2: enemies.add(new Orc()); enemies.add(new Goblin()); break;
            case 3: enemies.add(new Skeleton()); enemies.add(new Skeleton()); break;
            case 4: enemies.add(new DarkKnight()); break;
            case 5: enemies.add(new Orc()); enemies.add(new DarkKnight()); break;
            case 6: enemies.add(new DragonBoss()); break;
            default: enemies.add(new Goblin());
        }
        waveLabel.setText("Wave " + wave + "/" + MAX_WAVES);
        showFloatingText("WAVE " + wave + "!", 350, new Color(255, 215, 0));
        statusLabel.setText("Wave " + wave + " begins! " + enemies.size() + " enemies appear!");
    }

    // ============================================================
    // TURN SYSTEM
    // ============================================================
    private void startPlayerTurn() {
        if (!battleActive) return;

        if (allPartyDead()) { gameOver(false); return; }

        // Allow the player to choose which alive hero will act this turn.
        // If everyone is dead, game over.
        int aliveCount = 0;
        for (characters.Character c : party) if (c.isAlive()) aliveCount++;
        if (aliveCount == 0) { gameOver(false); return; }

        // If currently taunted or an enemy is taunting, keep forced behavior (no choice)
        boolean enemyTaunting = false;
        for (Enemy e : enemies) {
            if (e.isAlive() && e.isTaunting()) { enemyTaunting = true; break; }
        }

        if (!taunted && !enemyTaunting) {
            // Build selection list of alive heroes
            List<String> choices = new ArrayList<>();
            for (int i = 0; i < party.size(); i++) {
                characters.Character c = party.get(i);
                if (c.isAlive()) choices.add(i + ": " + c.getShortStatus());
            }
            String choice = (String) JOptionPane.showInputDialog(this,
                "Choose a hero to act:", "Select Hero",
                JOptionPane.PLAIN_MESSAGE, null, choices.toArray(new String[0]), choices.get(0));
            if (choice != null) {
                // parse chosen index from the string "idx: Name"
                try {
                    int idx = Integer.parseInt(choice.split(":")[0].trim());
                    if (idx >= 0 && idx < party.size() && party.get(idx).isAlive()) {
                        currentMemberIndex = idx;
                    }
                } catch (Exception ex) {
                    // keep currentMemberIndex if parsing fails
                }
            }
            // If user cancelled the dialog, fallthrough and use currentMemberIndex as-is
        } else {
            // Find next alive member if forced (taunt) behaviour or no choice allowed
            int attempts = 0;
            while (!party.get(currentMemberIndex).isAlive() && attempts < party.size()) {
                currentMemberIndex = (currentMemberIndex + 1) % party.size();
                attempts++;
            }
            if (attempts >= party.size()) { gameOver(false); return; }
        }

        characters.Character current = party.get(currentMemberIndex);
        playerTurn = true;
        waitingForAction = true;

        if (taunted || enemyTaunting) {
            taunted = true;
            turnLabel.setText("\uD83D\uDD34 " + current.getName() + " is TAUNTED! Forced to attack (high miss chance)");
            Timer t = new Timer(600, e -> { performAttack(); ((Timer)e.getSource()).stop(); });
            t.setRepeats(false);
            t.start();
            return;
        }

        turnLabel.setText("\uD83D\uDFE2 " + current.getName() + "'s turn - Choose an action");
        enableButtons(true);
        repaint();
    }

    // ============================================================
    // ATTACK
    // ============================================================
    private void performAttack() {
        if (!waitingForAction && !taunted) return;
        waitingForAction = false;
        enableButtons(false);

        characters.Character current = party.get(currentMemberIndex);
        // Determine target: if taunted prefer taunter, otherwise ask player to choose
        Enemy target = null;
        if (taunted) {
            Enemy taunter = getTauntingEnemy();
            target = (taunter != null && taunter.isAlive()) ? taunter : getFirstAliveEnemy();
        } else {
            target = chooseEnemyTarget();
        }
        if (target == null) { waveComplete(); return; }

        // === ANIMATION: Attack lunge ===
        heroAttacking[currentMemberIndex] = true;
        heroAttackStartMs[currentMemberIndex] = System.currentTimeMillis();

        turnsTaken++;

        int hitChance = taunted ? 30 : current.getHitChance();

        if (random.nextInt(100) >= hitChance) {
            statusLabel.setText(current.getName() + " missed!");
            showFloatingText("MISS!", 300, Color.GRAY);
            afterPlayerAction();
            return;
        }

        int damage = current.attack();
        damage = current.onAttackDamage(damage);

        boolean crit = random.nextInt(100) < current.getCritChance();
        if (crit) damage = current.onCritDamage(damage);

        target.takeDamage(damage);

        // === ANIMATION: Enemy shake on hit ===
        triggerEnemyShake(target, crit ? 14 : 7);

        if (crit) {
            statusLabel.setText(current.getName() + " landed a CRITICAL hit for " + damage + " damage!");
            showFloatingText("CRIT! -" + damage, 300, new Color(255, 200, 0));
        } else {
            statusLabel.setText(current.getName() + " attacked for " + damage + " damage!");
            showFloatingText("-" + damage, 300, Color.YELLOW);
        }

        taunted = false;
        for (Enemy e : enemies) e.setTaunting(false);

        checkEnemyDefeated(target);
        afterPlayerAction();
        repaint();
    }

    // ============================================================
    // SKILL
    // ============================================================
    private void performSkill() {
        if (!waitingForAction) return;
        waitingForAction = false;
        enableButtons(false);

        characters.Character current = party.get(currentMemberIndex);
        // Determine target: if taunted prefer taunter, otherwise ask player to choose
        Enemy target = null;
        if (taunted) {
            Enemy taunter = getTauntingEnemy();
            target = (taunter != null && taunter.isAlive()) ? taunter : getFirstAliveEnemy();
        } else {
            target = chooseEnemyTarget();
        }
        if (target == null) { waveComplete(); return; }

        if (current.getMp() < current.getSkillMpCost()) {
            statusLabel.setText("\u274C Not enough MP! Need " + current.getSkillMpCost() + " MP");
            showFloatingText("NO MP!", 300, Color.RED);
            waitingForAction = true;
            enableButtons(true);
            return;
        }

        // === ANIMATION: Skill lunge ===
        heroSkill[currentMemberIndex] = true;
        heroSkillStartMs[currentMemberIndex] = System.currentTimeMillis();

        turnsTaken++;
        current.setMp(current.getMp() - current.getSkillMpCost());
        int damage = current.useSkill();
        target.takeDamage(damage);

        // === ANIMATION: Big enemy shake on skill ===
        triggerEnemyShake(target, 20);

        statusLabel.setText(current.getName() + " used " + getSkillName(current) + " for " + damage + " damage!");
        showFloatingText(getSkillName(current) + " -" + damage, 280, new Color(200, 100, 255));

        checkEnemyDefeated(target);
        afterPlayerAction();
        repaint();
    }

    private String getSkillName(characters.Character c) {
        if (c instanceof Warrior) return "Shield Bash";
        if (c instanceof Mage) return "Fireball";
        if (c instanceof Archer) return "Rain of Arrows";
        return "Skill";
    }

    // ============================================================
    // ITEM
    // ============================================================
    private void useItem() {
        if (!waitingForAction) return;

        if (inventory.isEmpty()) {
            try {
                throw new EmptyInventoryException();
            } catch (EmptyInventoryException e) {
                statusLabel.setText("\u274C " + e.getMessage());
                JOptionPane.showMessageDialog(this, e.getMessage(), "Empty Inventory", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String[] itemNames = new String[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            itemNames[i] = inventory.get(i).getName() + " (" + inventory.get(i).getDescription() + ")";
        }

        String choice = (String) JOptionPane.showInputDialog(this,
            "Choose an item to use:", "Inventory",
            JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);
        if (choice == null) return;

        int idx = -1;
        for (int i = 0; i < inventory.size(); i++) {
            if (itemNames[i].equals(choice)) { idx = i; break; }
        }
        if (idx < 0) return;

        waitingForAction = false;
        enableButtons(false);
        turnsTaken++;

        Item item = inventory.remove(idx);
        characters.Character target = party.get(currentMemberIndex);

        switch (item.getEffect()) {
            case HEAL_HP:
                target.restoreHp(item.getValue());
                statusLabel.setText(target.getName() + " used " + item.getName() + "! +" + item.getValue() + " HP");
                showFloatingText("+" + item.getValue() + " HP", 350, HP_GREEN);
                break;
            case RESTORE_MP:
                target.restoreMp(item.getValue());
                statusLabel.setText(target.getName() + " used " + item.getName() + "! +" + item.getValue() + " MP");
                showFloatingText("+" + item.getValue() + " MP", 350, MP_BLUE);
                break;
            case REVIVE:
                characters.Character fallen = null;
                for (characters.Character c : party) {
                    if (!c.isAlive()) { fallen = c; break; }
                }
                if (fallen != null) {
                    fallen.revive(50);
                    statusLabel.setText(fallen.getName() + " has been revived! +" + fallen.getHp() + " HP");
                    showFloatingText("REVIVED!", 350, new Color(100, 255, 100));
                } else {
                    statusLabel.setText("No fallen allies to revive. Item wasted...");
                    showFloatingText("NO EFFECT", 350, Color.GRAY);
                }
                break;
        }

        afterPlayerAction();
        repaint();
    }

    // ============================================================
    // FLEE
    // ============================================================
    private void attemptFlee() {
        if (!waitingForAction) return;
        waitingForAction = false;
        enableButtons(false);
        turnsTaken++;

        if (random.nextInt(100) < 50) {
            statusLabel.setText("You fled successfully!");
            showFloatingText("FLED!", 300, Color.CYAN);
            battleActive = false;
            Timer t = new Timer(1000, e -> {
                frame.setContentPane(new CharacterSelect(frame));
                frame.revalidate();
            });
            t.setRepeats(false);
            t.start();
        } else {
            statusLabel.setText("Flee failed! All party members take damage!");
            showFloatingText("FLEE FAILED!", 300, Color.RED);
            for (characters.Character c : party) {
                if (c.isAlive()) {
                    c.setHp(c.getHp() - (15 + random.nextInt(10)));
                }
            }
            checkPartyStatus();
            afterPlayerAction();
            repaint();
        }
    }

    // ============================================================
    // AFTER PLAYER ACTION
    // ============================================================
    private void afterPlayerAction() {
        if (allEnemiesDefeated()) { waveComplete(); return; }
        if (allPartyDead()) { gameOver(false); return; }
        // After any player action, enemies act immediately, then control returns
        // to the next alive party member (round-robin).
        playerTurn = false;

        // Compute next alive party member index (round-robin)
        int nextIdx = (currentMemberIndex + 1) % party.size();
        int attempts = 0;
        while (!party.get(nextIdx).isAlive() && attempts < party.size()) {
            nextIdx = (nextIdx + 1) % party.size();
            attempts++;
        }
        // If no other alive members found, keep current index (will be validated when player turn starts)
        if (attempts < party.size()) {
            currentMemberIndex = nextIdx;
        }

        // Start enemy phase immediately
        startEnemyTurn();
    }

    // ============================================================
    // ENEMY TURN
    // ============================================================
    private void startEnemyTurn() {
        playerTurn = false;
        enableButtons(false);
        turnLabel.setText("\uD83D\uDD34 Enemy turn!");
        battleActive = true;
        processNextEnemy(0);
    }

    private void processNextEnemy(int enemyIdx) {
        if (!battleActive) return;

        int idx = enemyIdx;
        while (idx < enemies.size() && !enemies.get(idx).isAlive()) idx++;

        if (idx >= enemies.size()) {
            if (allEnemiesDefeated()) { waveComplete(); }
            else {
                // Do not forcefully reset to first party member. currentMemberIndex
                // was precomputed before the enemy phase in afterPlayerAction().
                int attempts = 0;
                while (!party.get(currentMemberIndex).isAlive() && attempts < party.size()) {
                    currentMemberIndex = (currentMemberIndex + 1) % party.size();
                    attempts++;
                }
                startPlayerTurn();
            }
            return;
        }

        Enemy enemy = enemies.get(idx);
        Enemy.Action action = enemy.chooseAction();
        final int currentIdx = idx;

        Timer delay = new Timer(700, null);
        delay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!battleActive) { delay.stop(); return; }

                switch (action) {
                    case ATTACK: {
                        List<characters.Character> aliveMembers = new ArrayList<>();
                        for (characters.Character c : party) {
                            if (c.isAlive()) aliveMembers.add(c);
                        }
                        if (aliveMembers.isEmpty()) { gameOver(false); delay.stop(); return; }

                        characters.Character target = aliveMembers.get(random.nextInt(aliveMembers.size()));
                        int rawDmg = enemy.calculateAttackDamage();
                        int finalDmg = target.onTakeDamage(rawDmg);
                        target.setHp(target.getHp() - finalDmg);

                        // === ANIMATION: Hero hurt flash ===
                        int hurtIdx = party.indexOf(target);
                        if (hurtIdx >= 0 && hurtIdx < 2) {
                            heroHurt[hurtIdx] = true;
                            heroHurtStartMs[hurtIdx] = System.currentTimeMillis();
                            // Enemy also does a small forward lurch visually via shake
                            triggerEnemyShake(enemy, 3);
                        }

                        statusLabel.setText(enemy.getName() + " attacks " + target.getName() + " for " + finalDmg + " damage!");
                        showFloatingText("-" + finalDmg, 200, HP_RED);
                        break;
                    }
                    case BUFF:
                        enemy.applyBuff();
                        statusLabel.setText(enemy.getName() + " buffs itself! ATK increased by 5!");
                        showFloatingText("BUFFED!", 200, new Color(100, 200, 255));
                        break;
                    case TAUNT:
                        if (enemy.attemptTaunt()) {
                            enemy.setTaunting(true);
                            taunted = true;
                            statusLabel.setText(enemy.getName() + " taunts your party! Next attack forced and inaccurate!");
                            showFloatingText("TAUNT!", 200, new Color(255, 100, 100));
                        } else {
                            statusLabel.setText(enemy.getName() + " tries to taunt but fails!");
                            showFloatingText("MISS!", 200, Color.GRAY);
                        }
                        break;
                }

                checkPartyStatus();
                repaint();
                delay.stop();
                processNextEnemy(currentIdx + 1);
            }
        });
        delay.setRepeats(false);
        delay.start();
    }

    // ============================================================
    // CHECKS
    // ============================================================
    private boolean allEnemiesDefeated() {
        for (Enemy e : enemies) { if (e.isAlive()) return false; }
        return true;
    }

    private boolean allPartyDead() {
        for (characters.Character c : party) { if (c.isAlive()) return false; }
        return true;
    }

    private Enemy getFirstAliveEnemy() {
        for (Enemy e : enemies) { if (e.isAlive()) return e; }
        return null;
    }

    private Enemy getTauntingEnemy() {
        for (Enemy e : enemies) { if (e.isAlive() && e.isTaunting()) return e; }
        return null;
    }

    private Enemy chooseEnemyTarget() {
        List<String> options = new ArrayList<>();
        List<Enemy> alive = new ArrayList<>();
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.isAlive()) {
                options.add(i + ": " + e.getStatus());
                alive.add(e);
            }
        }
        if (alive.isEmpty()) return null;
        String choice = (String) JOptionPane.showInputDialog(this,
            "Choose an enemy target:", "Select Target",
            JOptionPane.PLAIN_MESSAGE, null, options.toArray(new String[0]), options.get(0));
        if (choice == null) return alive.get(0); // if cancelled, default to first alive
        try {
            int idx = Integer.parseInt(choice.split(":")[0].trim());
            // find the Alive enemy with that original index
            for (Enemy e : alive) {
                if (enemies.indexOf(e) == idx) return e;
            }
        } catch (Exception ex) {
            // fallthrough
        }
        return alive.get(0);
    }

    private void checkEnemyDefeated(Enemy enemy) {
        if (!enemy.isAlive()) {
            enemiesDefeated++;
            gold += enemy.getGoldDrop();
            goldLabel.setText("\uD83D\uDCB0 " + gold);
            statusLabel.setText(statusLabel.getText() + " " + enemy.getName() + " defeated! (+" + enemy.getGoldDrop() + "g)");
        }
    }

    private void checkPartyStatus() {
        if (allPartyDead()) { gameOver(false); }
    }

    // ============================================================
    // ANIMATION HELPERS
    // ============================================================
    private void triggerEnemyShake(Enemy enemy, float intensity) {
        int idx = enemies.indexOf(enemy);
        if (idx >= 0 && enemyShakeAmount != null && idx < enemyShakeAmount.length) {
            enemyShakeAmount[idx] = intensity;
            enemyShakeStartMs[idx] = System.currentTimeMillis();
        }
    }

    // ============================================================
    // WAVE COMPLETE
    // ============================================================
    private void waveComplete() {
        battleActive = false;
        enableButtons(false);
        statusLabel.setText("Wave " + currentWave + " cleared! \uD83C\uDF89");
        showFloatingText("WAVE CLEAR!", 300, new Color(100, 255, 100));

        SaveManager.saveGame(currentWave + 1, gold, enemiesDefeated, turnsTaken, party, inventory);

        if (currentWave >= MAX_WAVES) {
            Timer t = new Timer(1500, e -> {
                SaveManager.deleteSave();
                frame.setContentPane(new GameOverPanel(frame, true, enemiesDefeated, turnsTaken, gold));
                frame.revalidate();
            });
            t.setRepeats(false);
            t.start();
        } else {
            Timer t = new Timer(1000, e -> {
                ShopPanel shop = new ShopPanel(frame, inventory, gold);
                shop.setVisible(true);
                gold = shop.getRemainingGold();
                goldLabel.setText("\uD83D\uDCB0 " + gold);
                currentWave++;
                setupWave(currentWave);
                currentMemberIndex = 0;
                battleActive = true;
                startPlayerTurn();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // ============================================================
    // GAME OVER
    // ============================================================
    private void gameOver(boolean victory) {
        battleActive = false;
        enableButtons(false);
        turnLabel.setText(victory ? "VICTORY!" : "GAME OVER");
        SaveManager.deleteSave();

        Timer t = new Timer(1500, e -> {
            frame.setContentPane(new GameOverPanel(frame, victory, enemiesDefeated, turnsTaken, gold));
            frame.revalidate();
        });
        t.setRepeats(false);
        t.start();
    }

    // ============================================================
    // SAVE GAME
    // ============================================================
    private void saveGame() {
        boolean success = SaveManager.saveGame(currentWave, gold, enemiesDefeated, turnsTaken, party, inventory);
        statusLabel.setText(success ? "\uD83D\uDCBE Game saved! (Wave " + currentWave + ")" : "\u274C Save failed!");
    }

    // ============================================================
    // FLOATING TEXT
    // ============================================================
    private void showFloatingText(String text, int y, Color color) {
        floatingText = text;
        floatingY = y;
        floatTimer.start();
        repaint();
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 55);
        styleButton(button, new Color(40, 40, 55));
        button.addActionListener(this);
        button.setEnabled(false);
        add(button);
        return button;
    }

    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120), 2));
    }

    private void enableButtons(boolean enabled) {
        attackButton.setEnabled(enabled);
        skillButton.setEnabled(enabled);
        itemButton.setEnabled(enabled);
        fleeButton.setEnabled(enabled);
    }

    // ============================================================
    // PAINT COMPONENT
    // ============================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(DARK_BG);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // --- Party title at top left ---
        g2d.setColor(TEXT_WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        String title = party.size() >= 2 ? party.get(0).getName() + " & " + party.get(1).getName() : "Battle";
        g2d.drawString(title, 20, 30);

        // ========== HEROES (lower left, big) ==========
        int[] heroImgX = {20, 225};
        int[] heroImgY = {230, 270};
        int heroImgW = 180;
        int heroImgH = 220;

        for (int i = 0; i < party.size() && i < 2; i++) {
            characters.Character c = party.get(i);

            // Character name + status
            g2d.setColor(TEXT_WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String status = c.getName();
            if (!c.isAlive()) status += " [DOWN]";
            if (i == currentMemberIndex && playerTurn && c.isAlive()) {
                status += " \u25C0 ACTIVE";
                g2d.setColor(new Color(100, 255, 100));
            }
            g2d.drawString(status, heroImgX[i] - 5, heroImgY[i] - 8);

            // HP bar background
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRect(heroImgX[i], heroImgY[i] + heroImgH + 5, 200, 20);

            // HP bar
            int hpWidth = (int)(200.0 * c.getHp() / c.getMaxHp());
            g2d.setColor(c.getHp() > c.getMaxHp() / 3 ? HP_GREEN : HP_RED);
            g2d.fillRect(heroImgX[i], heroImgY[i] + heroImgH + 5, hpWidth, 20);

            // MP bar background
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRect(heroImgX[i], heroImgY[i] + heroImgH + 28, 200, 10);

            // MP bar
            int mpWidth = (int)(200.0 * c.getMp() / c.getMaxMp());
            g2d.setColor(MP_BLUE);
            g2d.fillRect(heroImgX[i], heroImgY[i] + heroImgH + 28, mpWidth, 10);

            // HP/MP text
            g2d.setColor(TEXT_WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString("HP: " + c.getHp() + "/" + c.getMaxHp(), heroImgX[i] + 5, heroImgY[i] + heroImgH + 19);
            g2d.drawString("MP: " + c.getMp() + "/" + c.getMaxMp(), heroImgX[i] + 5, heroImgY[i] + heroImgH + 36);

            // Hero sprite
            if (i < playerImages.length && playerImages[i] != null) {
                int baseX = heroImgX[i];
                int baseY = heroImgY[i];

                // --- IDLE BOB ---
                float bobY = (float)(Math.sin(animTick * 0.08 + i * Math.PI) * 2.5);

                // --- ACTIVE PULSE ---
                if (i == currentMemberIndex && playerTurn && party.get(i).isAlive()) {
                    bobY += (float)(Math.sin(animTick * 0.15) * 2);
                }

                // --- ATTACK/SKILL LUNGE ---
                float lungeX = 0, lungeY = 0;
                if (heroSkill[i]) {
                    long elapsed = System.currentTimeMillis() - heroSkillStartMs[i];
                    float progress = Math.min(1f, elapsed / (float)SKILL_ANIM_MS);
                    lungeX = (float)(Math.sin(progress * Math.PI) * 50);
                    lungeY = (float)(-Math.abs(Math.sin(progress * Math.PI * 2)) * 10);
                } else if (heroAttacking[i]) {
                    long elapsed = System.currentTimeMillis() - heroAttackStartMs[i];
                    float progress = Math.min(1f, elapsed / (float)ATTACK_ANIM_MS);
                    lungeX = (float)(Math.sin(progress * Math.PI) * 35);
                    lungeY = (float)(-Math.abs(Math.sin(progress * Math.PI * 2)) * 6);
                }

                int drawX = (int)(baseX + lungeX);
                int drawY = (int)(baseY + bobY + lungeY);

                g2d.drawImage(playerImages[i], drawX, drawY, heroImgW, heroImgH, this);

                // --- HURT FLASH ---
                if (heroHurt[i]) {
                    long elapsed = System.currentTimeMillis() - heroHurtStartMs[i];
                    float progress = elapsed / (float)HURT_ANIM_MS;
                    if (progress < 1f) {
                        float alpha = (float)(Math.sin(progress * Math.PI * 5) * 0.35f + 0.35f);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        g2d.setColor(Color.RED);
                        g2d.fillRect(drawX, drawY, heroImgW, heroImgH);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    }
                }
            }
        }

        // ========== ENEMIES (right side) ==========
        int enemyStartX = 820;
        int enemyY = 60;
        int enemyImgW = 130;
        int enemyImgH = 150;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            int x = enemyStartX - i * 60;

            if (e.isAlive()) {
                // HP bar
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRect(x, enemyY, 180, 20);

                int hpWidth = (int)(180.0 * e.getHp() / e.getMaxHp());
                g2d.setColor(HP_RED);
                g2d.fillRect(x, enemyY, hpWidth, 20);

                // Name + HP text
                g2d.setColor(TEXT_WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(e.getName() + " HP: " + e.getHp() + "/" + e.getMaxHp(), x, enemyY + 14);

                // Buff/taunt indicators
                if (e.getAttackBuff() > 0) {
                    g2d.setColor(new Color(255, 200, 100));
                    g2d.setFont(new Font("Arial", Font.BOLD, 10));
                    g2d.drawString("+ATK", x + 140, enemyY + 14);
                }
                if (e.isTaunting()) {
                    g2d.setColor(HP_RED);
                    g2d.setFont(new Font("Arial", Font.BOLD, 10));
                    g2d.drawString("TAUNT", x + 140, enemyY + 4);
                }

                // Enemy sprite
                if (enemyImage != null) {
                    float shakeX = 0, shakeY = 0;
                    if (enemyShakeAmount != null && i < enemyShakeAmount.length && enemyShakeAmount[i] > 0) {
                        long elapsed = System.currentTimeMillis() - enemyShakeStartMs[i];
                        if (elapsed < SHAKE_ANIM_MS) {
                            float decay = 1f - elapsed / (float)SHAKE_ANIM_MS;
                            shakeX = (float)((Math.random() - 0.5) * enemyShakeAmount[i] * decay);
                            shakeY = (float)((Math.random() - 0.5) * enemyShakeAmount[i] * decay);
                        }
                    }
                    g2d.drawImage(enemyImage, (int)(x + shakeX), (int)(enemyY + 25 + shakeY), enemyImgW, enemyImgH, this);
                }
                enemyY += 200;
            }
        }

        // ========== FLOATING TEXT ==========
        if (!floatingText.isEmpty() && floatTimer.isRunning()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.setColor(new Color(255, 255, 200));
            int textWidth = g2d.getFontMetrics().stringWidth(floatingText);
            g2d.drawString(floatingText, (getWidth() - textWidth) / 2, floatingY);
        }
    }

    // ============================================================
    // ACTION PERFORMED
    // ============================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!battleActive || !playerTurn || !waitingForAction) return;
        if (e.getSource() == attackButton) performAttack();
        else if (e.getSource() == skillButton) performSkill();
        else if (e.getSource() == itemButton) useItem();
        else if (e.getSource() == fleeButton) attemptFlee();
    }
}
