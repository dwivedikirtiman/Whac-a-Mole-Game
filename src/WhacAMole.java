import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class WhacAMole {
    final int BOARD_WIDTH = 600;
    final int BOARD_HEIGHT = 700; // Increased for restart button

    JFrame frame = new JFrame("Mario: Whac A Mole");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    ArrayList<JButton> currPlantTiles = new ArrayList<>();

    Random random = new Random();
    javax.swing.Timer setMoleTimer;
    javax.swing.Timer setPlantTimer;

    int score = 0;
    int highScore = 0;
    boolean gameActive = true; // Track if the game is active

    WhacAMole() {
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: " + score + " | High Score: " + highScore);
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        frame.add(boardPanel);

        Image plantImg = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        Image moleImg = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);

            tile.addActionListener(e -> {
                if (!gameActive) return; // Ignore clicks if the game is over

                JButton tileClicked = (JButton) e.getSource();
                if (tileClicked == currMoleTile) {
                    score += 10;
                    textLabel.setText("Score: " + score + " | High Score: " + Math.max(score, highScore));
                    highScore = Math.max(score, highScore);
                    animateTile(tileClicked, Color.GREEN);
                } else if (currPlantTiles.contains(tileClicked)) {
                    textLabel.setText("Game Over: " + score);
                    highScore = Math.max(score, highScore);
                    gameActive = false; // Game stops until restart is clicked
                    if (setMoleTimer != null) setMoleTimer.stop();
                    if (setPlantTimer != null) setPlantTimer.stop();
                    for (JButton tileBtn : board) {
                        tileBtn.setEnabled(false);
                    }
                }
            });
        }

        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.addActionListener(e -> restartGame());
        frame.add(restartButton, BorderLayout.SOUTH);

        setMoleTimer = new javax.swing.Timer(1000, e -> {
            if (!gameActive) return; // Prevent mole from appearing if the game is inactive

            if (currMoleTile != null) {
                currMoleTile.setIcon(null);
                currMoleTile = null;
            }

            int num;
            do {
                num = random.nextInt(9);
            } while (board[num] == currMoleTile || currPlantTiles.contains(board[num]));

            currMoleTile = board[num];
            currMoleTile.setIcon(moleIcon);
        });

        setPlantTimer = new javax.swing.Timer(1500, e -> {
            if (!gameActive) return; // Prevent plants from appearing if the game is inactive

            for (JButton plant : currPlantTiles) {
                plant.setIcon(null);
            }
            currPlantTiles.clear();

            int numberOfPlants = random.nextInt(3) + 1; // 1 to 3 plants
            while (currPlantTiles.size() < numberOfPlants) {
                int num = random.nextInt(9);
                JButton tile = board[num];
                if (tile != currMoleTile && !currPlantTiles.contains(tile)) {
                    currPlantTiles.add(tile);
                    tile.setIcon(plantIcon);
                }
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();
        frame.setVisible(true);
    }

    private void restartGame() {
        score = 0;
        textLabel.setText("Score: " + score + " | High Score: " + highScore);
        gameActive = true; // Reactivate the game

        for (JButton tile : board) {
            tile.setEnabled(true);
            tile.setIcon(null);
        }

        currMoleTile = null;
        currPlantTiles.clear();

        if (setMoleTimer != null) setMoleTimer.start();
        if (setPlantTimer != null) setPlantTimer.start();
    }

    private void animateTile(JButton tile, Color color) {
        tile.setBackground(color);
        javax.swing.Timer flashTimer = new javax.swing.Timer(200, e -> tile.setBackground(null));
        flashTimer.setRepeats(false);
        flashTimer.start();
    }

    public static void main(String[] args) {
        new WhacAMole();
    }
}
