import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    JFrame displayZoneFrame;
    RenderEngine renderEngine;
    GameEngine gameEngine;
    PhysicEngine physicEngine;
    JPanel titleScreen;

    public Main() throws Exception {
        // Initialize the JFrame
        displayZoneFrame = new JFrame("Dungeon Crawler");
        displayZoneFrame.setSize(400, 600);
        displayZoneFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        displayZoneFrame.setLayout(new CardLayout()); // Use CardLayout to switch between screens

        // Create the title screen
        createTitleScreen();

        // Initialize the render and physics engines but don't start timers yet
        renderEngine = new RenderEngine(displayZoneFrame);
        physicEngine = new PhysicEngine();

        // Add the title screen panel to the frame
        displayZoneFrame.add(titleScreen, "TitleScreen");
        displayZoneFrame.setVisible(true);
    }

    private void createTitleScreen() {
        titleScreen = new JPanel();
        titleScreen.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Dungeon Crawler", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleScreen.add(titleLabel, BorderLayout.CENTER);

        // Create the Start Game button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.addActionListener(e -> {
            try {
                showGameScreen();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        titleScreen.add(startButton, BorderLayout.SOUTH);
    }

    private void showGameScreen() throws Exception {
        // Remove the title screen panel
        displayZoneFrame.remove(titleScreen);

        // Initialize game components
        DynamicSprite hero = new DynamicSprite(200, 300,
                ImageIO.read(new File("./img/heroTileSheetLowRes.png")), 48, 50);
        gameEngine = new GameEngine(hero);

        Playground level = new Playground("./data/level1.txt");

        // Add the hero and level to the render and physics engines
        renderEngine.addToRenderList(level.getSpriteList());
        renderEngine.addToRenderList(hero);
        physicEngine.addToMovingSpriteList(hero);
        physicEngine.setEnvironment(level.getSolidSpriteList());

        // Add the render engine's display panel to the JFrame
        displayZoneFrame.add(renderEngine, "GameScreen");

        // Add key listener for game controls
        displayZoneFrame.addKeyListener(gameEngine);
        displayZoneFrame.setFocusable(true);
        displayZoneFrame.requestFocusInWindow();  // Ensure JFrame captures key events

        // Start timers for rendering, game logic, and physics updates
        Timer renderTimer = new Timer(50, time -> renderEngine.update());
        Timer gameTimer = new Timer(50, time -> gameEngine.update());
        Timer physicTimer = new Timer(50, time -> physicEngine.update());

        renderTimer.start();
        gameTimer.start();
        physicTimer.start();

        // Add key bindings for up and down as an alternative to KeyListener
        setupKeyBindings();

        // Switch to game screen
        CardLayout layout = (CardLayout) displayZoneFrame.getContentPane().getLayout();
        layout.show(displayZoneFrame.getContentPane(), "GameScreen");

        // Refresh the frame to apply changes
        displayZoneFrame.revalidate();
        displayZoneFrame.repaint();
    }

    private void setupKeyBindings() {
        InputMap inputMap = displayZoneFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = displayZoneFrame.getRootPane().getActionMap();

        // Bind "UP" key to an action
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Up key pressed"); // Debugging
                // Move character up or call appropriate method in gameEngine
            }
        });

        // Bind "DOWN" key to an action
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Down key pressed"); // Debugging
                // Move character down or call appropriate method in gameEngine
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}