package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterSelect extends JPanel implements ActionListener {

    JFrame frame;

    JLabel warriorCard;
    JLabel mageCard;
    JLabel archerCard;

    int warriorX = -250;
    int mageX = -250;
    int archerX = -250;

    Timer timer;

    public CharacterSelect(JFrame frame) {

        this.frame = frame;

        setLayout(null);
        setBackground(Color.BLACK);

        JLabel title = new JLabel("SELECT YOUR HERO");
        title.setBounds(450, 20, 400, 50);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        add(title);

        // =========================
        // SAFE IMAGE LOADING
        // =========================
        warriorCard = new JLabel();
        mageCard = new JLabel();
        archerCard = new JLabel();

        setImage(warriorCard, "assets/characters/warrior.png");
        setImage(mageCard, "assets/characters/mage.png");
        setImage(archerCard, "assets/characters/archer.png");

        add(warriorCard);
        add(mageCard);
        add(archerCard);

        // CLICK EVENTS
        warriorCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.setContentPane(new GamePanel("Warrior"));
                frame.revalidate();
            }
        });

        mageCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.setContentPane(new GamePanel("Mage"));
                frame.revalidate();
            }
        });

        archerCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.setContentPane(new GamePanel("Archer"));
                frame.revalidate();
            }
        });

        // INITIAL POSITION (IMPORTANT)
        updatePositions();

        // ANIMATION TIMER
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (warriorX < 200) warriorX += 10;
                if (mageX < 500) mageX += 10;
                if (archerX < 800) archerX += 10;

                updatePositions();

                repaint(); // 🔥 IMPORTANT FIX

                if (warriorX >= 200 && mageX >= 500 && archerX >= 800) {
                    timer.stop();
                }
            }
        });

        timer.start();
    }

    // =========================
    // SAFE IMAGE LOADER
    // =========================
    private void setImage(JLabel label, String path) {

        ImageIcon icon = new ImageIcon(path);

        Image img = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);

        label.setIcon(new ImageIcon(img));
    }

    // =========================
    // UPDATE POSITIONS
    // =========================
    private void updatePositions() {

        warriorCard.setBounds(warriorX, 200, 200, 250);
        mageCard.setBounds(mageX, 200, 200, 250);
        archerCard.setBounds(archerX, 200, 200, 250);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // not used
    }
}