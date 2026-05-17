package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private Image background;
    private Image playerImage;
    private Image enemyImage;

    JButton attackButton;
    JButton skillButton;
    JButton itemButton;
    JButton exitButton;

    JLabel statusLabel;

    // =========================
    // LEVEL SYSTEM
    // =========================
    int level = 1;
    int exp = 0;
    int expNeeded = 100;

    // =========================
    // TURN SYSTEM
    // =========================
    boolean playerTurn = true;

    // =========================
    // SKILL COOLDOWN (NEW)
    // =========================
    int skillCooldown = 0;

    int playerHP = 100;
    int enemyHP = 100;
    int playerDamage = 20;

    String heroType;

    Random random = new Random();

    String floatingDamage = "";

    public GamePanel(String heroType) {

        this.heroType = heroType;

        setLayout(null);

        // =========================
        // HERO STATS
        // =========================
        if(heroType.equals("Warrior")) {
            playerDamage = 25;
            playerImage = new ImageIcon("assets\\characters\\warrior.png").getImage();
        }
        else if(heroType.equals("Mage")) {
            playerDamage = 35;
            playerImage = new ImageIcon("assets\\characters\\mage.png").getImage();
        }
        else if(heroType.equals("Archer")) {
            playerDamage = 20;
            playerImage = new ImageIcon("assets\\characters\\archer.png").getImage();
        }

        // =========================
        // ENEMY IMAGE
        // =========================
        enemyImage = new ImageIcon("assets\\characters\\enemy.png").getImage();

        // =========================
        // BACKGROUND
        // =========================
        background = new ImageIcon("assets\\backgrounds\\battle.jpg").getImage();

        // =========================
        // STATUS LABEL
        // =========================
        statusLabel = new JLabel(heroType + " entered battle!");
        statusLabel.setBounds(350, 520, 900, 40);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(statusLabel);

        // =========================
        // BUTTONS
        // =========================
        attackButton = new JButton("ATTACK");
        attackButton.setBounds(250, 600, 180, 60);
        styleButton(attackButton);
        attackButton.addActionListener(this);
        add(attackButton);

        skillButton = new JButton("SKILL");
        skillButton.setBounds(470, 600, 180, 60);
        styleButton(skillButton);
        skillButton.addActionListener(this);
        add(skillButton);

        itemButton = new JButton("ITEM");
        itemButton.setBounds(690, 600, 180, 60);
        styleButton(itemButton);
        itemButton.addActionListener(this);
        add(itemButton);

        exitButton = new JButton("EXIT");
        exitButton.setBounds(910, 600, 180, 60);
        styleButton(exitButton);
        exitButton.addActionListener(this);
        add(exitButton);
    }

    // =========================
    // BUTTON STYLE
    // =========================
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(40,40,40));
        button.setForeground(Color.WHITE);
    }

    // =========================
    // DRAW SCREEN
    // =========================
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString(heroType + " VS ENEMY", 450, 50);

        // PLAYER
        g.drawImage(playerImage, 100, 220, 250, 250, this);

        // ENEMY
        g.drawImage(enemyImage, 850, 180, 300, 300, this);

        // HP BARS
        g.setColor(Color.GREEN);
        g.fillRect(50, 100, playerHP * 3, 30);

        g.setColor(Color.RED);
        g.fillRect(850, 100, enemyHP * 3, 30);

        // TEXT
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        g.drawString("HP: " + playerHP + " | LVL: " + level + " | EXP: " + exp, 50, 90);
        g.drawString("ENEMY HP: " + enemyHP, 850, 90);

        // COOLDOWN INFO
        g.drawString("SKILL CD: " + skillCooldown, 50, 130);

        // DAMAGE TEXT
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(floatingDamage, 500, 200);
    }

    // =========================
    // ACTIONS
    // =========================
    @Override
    public void actionPerformed(ActionEvent e) {

        // =========================
        // ATTACK
        // =========================
        if(e.getSource() == attackButton) {

            if(!playerTurn) {
                statusLabel.setText("Wait your turn!");
                return;
            }

            int damage = playerDamage + random.nextInt(10);

            enemyHP -= damage;

            floatingDamage = "-" + damage;

            statusLabel.setText("You attacked!");

            checkEnemy();

            playerTurn = false;

            enemyTurn();
        }

        // =========================
        // SKILL (WITH COOLDOWN FIX)
        // =========================
        if(e.getSource() == skillButton) {

            if(!playerTurn) {
                statusLabel.setText("Wait your turn!");
                return;
            }

            if(skillCooldown > 0) {
                statusLabel.setText("Skill cooling down: " + skillCooldown);
                return;
            }

            int damage = playerDamage + 25;

            enemyHP -= damage;

            floatingDamage = "SKILL -" + damage;

            statusLabel.setText("Skill used!");

            skillCooldown = 3;

            checkEnemy();

            playerTurn = false;

            enemyTurn();
        }

        // =========================
        // ITEM
        // =========================
        if(e.getSource() == itemButton) {

            if(!playerTurn) {
                statusLabel.setText("Wait your turn!");
                return;
            }

            playerHP += 20;

            if(playerHP > 100) playerHP = 100;

            floatingDamage = "+20 HP";

            statusLabel.setText("Healed!");

            playerTurn = false;

            enemyTurn();
        }

        // EXIT
        if(e.getSource() == exitButton) {
            System.exit(0);
        }

        repaint();
    }

    // =========================
    // ENEMY TURN
    // =========================
    private void enemyTurn() {

        Timer timer = new Timer(800, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int damage = 10 + random.nextInt(10);

                playerHP -= damage;

                floatingDamage = "-" + damage;

                statusLabel.setText("Enemy attacked!");

                playerTurn = true;

                if(skillCooldown > 0) {
                    skillCooldown--;
                }

                checkPlayer();

                repaint();

                ((Timer)e.getSource()).stop();
            }
        });

        timer.start();
    }

    // =========================
    // CHECK ENEMY
    // =========================
    private void checkEnemy() {

        if(enemyHP <= 0) {

            enemyHP = 0;

            exp += 50;

            statusLabel.setText("Enemy defeated! +50 EXP");

            levelUp();

            attackButton.setEnabled(false);
            skillButton.setEnabled(false);
        }
    }

    // =========================
    // CHECK PLAYER
    // =========================
    private void checkPlayer() {

        if(playerHP <= 0) {

            playerHP = 0;

            statusLabel.setText("YOU LOST!");

            attackButton.setEnabled(false);
            skillButton.setEnabled(false);
        }
    }

    // =========================
    // LEVEL UP SYSTEM
    // =========================
    private void levelUp() {

        while(exp >= expNeeded) {

            exp -= expNeeded;

            level++;

            expNeeded += 50;

            playerHP = 100;

            playerDamage += 5;

            statusLabel.setText("LEVEL UP! Now Level " + level);
        }
    }
}