package snakegame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    private Image apple;
    private Image dot;
    private Image head;

    private JButton retry;

    private final int ALL_DOTS = 1600;
    private final int DOT_SIZE = 10;
    private final int RANDOM_POSITION = 38; // adjusted for 400x400
    private final int BOARD_WIDTH = 400;
    private final int BOARD_HEIGHT = 400;
    private final int HEADER_HEIGHT = 40; // ✅ reserved space for score display

    private int apple_x;
    private int apple_y;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;

    private int dots;
    private int score;
    private int applesEaten;
    private int level;

    private Timer timer;

    public Board() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());

        loadImages();
        initGame();
    }

    private void loadImages() {
        apple = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/apple.png")).getImage();
        dot = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/dot.png")).getImage();
        head = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/head.png")).getImage();
    }

    private void initGame() {
        inGame = true;
        dots = 3;
        score = 0;
        applesEaten = 0;
        level = 1;

        if (retry != null) {
            remove(retry);
            retry = null;
        }

        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;

        revalidate();
        repaint();

        for (int i = 0; i < dots; i++) {
            y[i] = HEADER_HEIGHT + 50; // ✅ snake starts below header
            x[i] = 50 - i * DOT_SIZE;
        }

        locateApple();

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(140, this);
        timer.start();

        requestFocusInWindow();
    }

    private void locateApple() {
        int rX = (int) (Math.random() * RANDOM_POSITION);
        int rY = (int) (Math.random() * (RANDOM_POSITION - 4)); // ✅ prevent apple from spawning on header
        apple_x = rX * DOT_SIZE;
        apple_y = HEADER_HEIGHT + rY * DOT_SIZE; // ✅ apple starts below header
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (inGame) {
            // ✅ Draw header background
            g.setColor(new Color(25, 25, 25));
            g.fillRect(0, 0, BOARD_WIDTH, HEADER_HEIGHT);

            // ✅ Draw score & level
            g.setColor(Color.WHITE);
            g.setFont(new Font("SAN_SERIF", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 25);
            g.drawString("Level: " + level, BOARD_WIDTH - 100, 25);

            // ✅ Draw game area
            g.drawImage(apple, apple_x, apple_y, this);
            for (int i = 0; i < dots; i++) {
                if (i == 0)
                    g.drawImage(head, x[i], y[i], this);
                else
                    g.drawImage(dot, x[i], y[i], this);
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over!";
        String scoreMsg = "Final Score: " + score;
        String levelMsg = "Level Reached: " + level;

        Font font = new Font("SAN_SERIF", Font.BOLD, 18);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (BOARD_WIDTH - metrics.stringWidth(msg)) / 2, 160);
        g.drawString(scoreMsg, (BOARD_WIDTH - metrics.stringWidth(scoreMsg)) / 2, 190);
        g.drawString(levelMsg, (BOARD_WIDTH - metrics.stringWidth(levelMsg)) / 2, 220);

        if (retry == null) {
            retry = new JButton("Retry");
            retry.setBounds((BOARD_WIDTH - 100) / 2, 250, 100, 30);
            retry.setBackground(Color.WHITE);
            retry.setForeground(Color.BLACK);
            retry.addActionListener(e -> initGame());
            setLayout(null);
            add(retry);
            revalidate();
            repaint();
        }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection) y[0] -= DOT_SIZE;
        if (downDirection) y[0] += DOT_SIZE;
    }

    private void checkApple() {
        if (x[0] == apple_x && y[0] == apple_y) {
            dots++;
            score += 10;
            applesEaten++;
            locateApple();

            if (applesEaten % 10 == 0) {
                int newDelay = Math.max(60, timer.getDelay() - 20);
                timer.setDelay(newDelay);
                level++;
                System.out.println("⚡ Speed increased! Level " + level + " | Delay: " + newDelay + "ms");
            }
        }
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (y[0] >= BOARD_HEIGHT || y[0] < HEADER_HEIGHT || x[0] >= BOARD_WIDTH || x[0] < 0) {
            // ✅ snake can't enter header
            inGame = false;
        }

        if (!inGame) timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            move();
            checkCollision();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
            if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }
}
