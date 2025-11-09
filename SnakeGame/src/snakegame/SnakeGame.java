package snakegame;

import javax.swing.*;

public class SnakeGame extends JFrame {
    public SnakeGame() {
        super("Snake Game");
        add(new Board());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new SnakeGame().setVisible(true);
    }
}
