package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("RPG BATTLE SYSTEM");

        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Start with character selection
        frame.setContentPane(new CharacterSelect(frame));

        frame.setVisible(true);
    }
}
