import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    private JFrame displayZoneFrame;
    private RenderEngine renderEngine;
    private GameEngine gameEngine;
    private PhysicEngine physicEngine;

    private JLabel timerLabel; // Label to display the timer
    private int timeRemaining = 30; // Countdown timer in seconds
    private boolean gameOver = false; // Track game-over state

    public Main() throws Exception {
        // Create the main game window
        displayZoneFrame = new JFrame("Java Labs");
        displayZoneFrame.setSize(1000, 800);
        displayZoneFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create the start screen panel
        JPanel startScreen = new JPanel();
        startScreen.setLayout(new BorderLayout());
        JLabel title = new JLabel("Welcome to Dungeon Crawler", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        startScreen.add(title, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setFocusable(false);
        startScreen.add(startButton, BorderLayout.SOUTH);

        // Show the start screen first
        displayZoneFrame.getContentPane().add(startScreen);
        displayZoneFrame.setVisible(true);

        // Action to switch to the game screen
        startButton.addActionListener(e -> {
            try {
                startGame();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void startGame() throws Exception {
        // Reset game over flag
        gameOver = false;
        timeRemaining = 30;

        // Create hero sprite
        DynamicSprite hero = new DynamicSprite(200, 300,
                ImageIO.read(new File("./img/heroTileSheetLowRes.png")), 48, 50);

        // Initialize engines
        renderEngine = new RenderEngine(displayZoneFrame);
        physicEngine = new PhysicEngine();
        gameEngine = new GameEngine(hero);

        // Timers for updating the game
        Timer renderTimer = new Timer(50, (time) -> renderEngine.update());
        Timer gameTimer = new Timer(50, (time) -> gameEngine.update());
        Timer physicTimer = new Timer(50, (time) -> physicEngine.update());

        renderTimer.start();
        gameTimer.start();
        physicTimer.start();

        // Set up the game components
        Playground level = new Playground("./data/level1.txt");
        renderEngine.addToRenderList(level.getSpriteList());
        renderEngine.addToRenderList(hero);
        physicEngine.addToMovingSpriteList(hero);
        physicEngine.setEnvironment(level.getSolidSpriteList());

        // Create a timer label
        timerLabel = new JLabel("Time Remaining: " + timeRemaining + " seconds");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Countdown timer
        Timer countdownTimer = new Timer(1000, e -> updateTimer());
        countdownTimer.start();

        // Replace the start screen with the game screen
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(renderEngine, BorderLayout.CENTER);
        gamePanel.add(timerLabel, BorderLayout.NORTH);

        displayZoneFrame.getContentPane().removeAll();
        displayZoneFrame.getContentPane().add(gamePanel);
        displayZoneFrame.revalidate();
        displayZoneFrame.repaint();

        // Add key listener for game controls
        displayZoneFrame.addKeyListener(gameEngine);
    }

    private void updateTimer() {
        if (gameOver) return; // If the game is over, stop the countdown

        if (timeRemaining > 0) {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
        } else {
            gameOver = true;
            timerLabel.setText("Time's Up! Game Over.");
            showRestartButton(); // Show the restart button
        }
    }

    private void showRestartButton() {
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setFocusable(false);

        restartButton.addActionListener(e -> {
            try {
                startGame(); // Restart the game
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());
        gameOverPanel.add(new JLabel("Game Over!"), BorderLayout.CENTER);
        gameOverPanel.add(restartButton, BorderLayout.SOUTH);

        displayZoneFrame.getContentPane().removeAll();
        displayZoneFrame.getContentPane().add(gameOverPanel);
        displayZoneFrame.revalidate();
        displayZoneFrame.repaint();
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
    }
}
