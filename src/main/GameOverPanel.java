package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverPanel extends JPanel implements ActionListener {

    private final boolean victory;
    private final int enemiesDefeated;
    private final int turnsTaken;
    private final int goldEarned;
    private final JFrame frame;

    public GameOverPanel(JFrame frame, boolean victory, int enemiesDefeated, int turnsTaken, int goldEarned) {
        this.frame = frame;
        this.victory = victory;
        this.enemiesDefeated = enemiesDefeated;
        this.turnsTaken = turnsTaken;
        this.goldEarned = goldEarned;

        setLayout(null);
        setBackground(new Color(20, 20, 30));

        // ========== TITLE ==========
        JLabel title = new JLabel(victory ? "VICTORY!" : "DEFEAT");
        title.setBounds(0, 60, 1200, 80);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 64));
        title.setForeground(victory ? new Color(255, 215, 0) : new Color(200, 50, 50));
        add(title);

        // ========== SUBTITLE ==========
        JLabel subtitle = new JLabel(victory
            ? "You conquered all waves!"
            : "Your party has fallen...");
        subtitle.setBounds(0, 150, 1200, 40);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 24));
        subtitle.setForeground(Color.LIGHT_GRAY);
        add(subtitle);

        // ========== SCORE PANEL ==========
        JPanel scorePanel = new JPanel();
        scorePanel.setBounds(300, 220, 600, 220);
        scorePanel.setLayout(new GridLayout(4, 1, 10, 10));
        scorePanel.setBackground(new Color(30, 30, 45));
        scorePanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 2));

        Font scoreFont = new Font("Arial", Font.BOLD, 22);

        JLabel score1 = new JLabel("Enemies Defeated: " + enemiesDefeated);
        score1.setFont(scoreFont);
        score1.setForeground(Color.WHITE);
        score1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel score2 = new JLabel("Turns Taken: " + turnsTaken);
        score2.setFont(scoreFont);
        score2.setForeground(Color.WHITE);
        score2.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel score3 = new JLabel("Gold Earned: " + goldEarned);
        score3.setFont(scoreFont);
        score3.setForeground(new Color(255, 215, 0));
        score3.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel score4 = new JLabel("Final Score: " + (enemiesDefeated * 100 + goldEarned - turnsTaken * 5));
        score4.setFont(new Font("Arial", Font.BOLD, 28));
        score4.setForeground(new Color(100, 200, 255));
        score4.setHorizontalAlignment(SwingConstants.CENTER);

        scorePanel.add(score1);
        scorePanel.add(score2);
        scorePanel.add(score3);
        scorePanel.add(score4);
        add(scorePanel);

        // ========== BUTTONS ==========
        JButton restartButton = new JButton("PLAY AGAIN");
        restartButton.setBounds(350, 480, 200, 60);
        styleButton(restartButton, new Color(50, 150, 50));
        restartButton.addActionListener(e -> {
            frame.setContentPane(new CharacterSelect(frame));
            frame.revalidate();
        });
        add(restartButton);

        JButton exitButton = new JButton("EXIT GAME");
        exitButton.setBounds(650, 480, 200, 60);
        styleButton(exitButton, new Color(150, 50, 50));
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        // ========== GIF BACKGROUND (if available) ==========
        // (optional animated decoration)
    }

    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // not used directly - buttons use lambdas
    }
}
