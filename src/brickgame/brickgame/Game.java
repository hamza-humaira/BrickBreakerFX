package brickgame;

import javafx.animation.AnimationTimer;
// ★ ADDED
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
// ★ ADDED
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

import java.util.ArrayList;

public class Game {
    private Pane root;
    private Scene scene;
    private Ball ball;
    private Paddle paddle;
    private ArrayList<Brick> bricks = new ArrayList<>();
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int score = 0;

    private Text scoreText;
    private Text gameOverText;

    // Dashboard variables
    private Pane dashboard;
    private Text finalScoreText;
    private Text restartText;

    // Sound effects
    private AudioClip brickHitSound;
    private AudioClip gameOverSound;
    private AudioClip gameWinSound;

    public Game(Pane root, Scene scene) {
        this.root = root;
        this.scene = scene;

        root.setStyle("-fx-background-color: black;"); // color of bg

        // Load sounds (make sure the files are in your project resources)
        brickHitSound = new AudioClip(getClass().getResource("/sounds/brick_hit.mp3").toString());
        gameOverSound = new AudioClip(getClass().getResource("/sounds/game_over.mp3").toString());
        gameWinSound = new AudioClip(getClass().getResource("/sounds/game_win.mp3").toString());

        //paddle
        paddle = new Paddle(250, 550, 100, 15);
        root.getChildren().add(paddle);  //getChildren returns list of all the UI elements inside the pane and the "add" adds the paddle into the list

        // Ball
        ball = new Ball(300, 400, 10);
        ball.setFill(Color.WHITE);
        root.getChildren().add(ball);
        ball.toFront();

        // Bricks
        createBricks();

        // Score text
        scoreText = new Text(10, 30, "Score: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreText.setFill(Color.WHITE);
        root.getChildren().add(scoreText);


        gameOverText = new Text();
        gameOverText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        gameOverText.setFill(Color.RED);
        gameOverText.setX(200);
        gameOverText.setY(300);
        gameOverText.setVisible(false);
        root.getChildren().add(gameOverText);

        // --- DASHBOARD SETUP ---
        dashboard = new Pane();
        dashboard.setPrefSize(scene.getWidth(), scene.getHeight());
        dashboard.setStyle("-fx-background-color: black;"); // black overlay

        finalScoreText = new Text();
        finalScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        finalScoreText.setFill(Color.WHITE);
        finalScoreText.setY(300); // score below title

        restartText = new Text("Press R to Restart or X to Quit");
        restartText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restartText.setFill(Color.YELLOW);
        restartText.setX((scene.getWidth() - restartText.getLayoutBounds().getWidth()) / 2);
        restartText.setY(400);

        dashboard.getChildren().addAll(finalScoreText, restartText);
        dashboard.setVisible(false); // hide it initially
        root.getChildren().add(dashboard);
        dashboard.toFront();

        //controlling form keyboard
        scene.setOnKeyPressed(e -> { //this code is run as the user presses any key on the keyboard
            switch (e.getCode()) { //e represents event
                case LEFT -> paddle.moveLeft();  //left key moves the paddle to the left
                case RIGHT -> paddle.moveRight(scene.getWidth());
                case R -> { // restart game when R is pressed
                    if (gameOver || gameWon) resetGame();
                }
                case Q -> { // exit game
                    if (gameOver || gameWon) Platform.exit();
                }
            }
        });

        // controlling using the mouse
        scene.setOnMouseMoved(e -> {
            double mouseX = e.getX() - paddle.getWidth()/2; //makes the paddle at the center under the mouse
            if (mouseX < 0) mouseX = 0;
            if (mouseX > scene.getWidth() - paddle.getWidth())
                paddle.setX(scene.getWidth() - paddle.getWidth());
            else
                paddle.setX(mouseX); //makes sure it doesn't exceed the screen
        });
    }

    //creates rows and columns of bricks with each color in each column
    private void createBricks() {
        int rows = 5;
        int cols = 8;
        double brickWidth = 70;
        double brickHeight = 30;
        double spacing = 5;

        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Brick brick = new Brick(j * (brickWidth + spacing),
                        50 + i * (brickHeight + spacing),
                        brickWidth,
                        brickHeight,
                        colors[i]);
                bricks.add(brick);
                root.getChildren().add(brick);
            }
        }
    }

    public void start() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver && !gameWon) {
                    update();
                }
            }
        };
        timer.start();
    }

    private void update() {
        ball.move();

        // Wall collision
        if (ball.getCenterX() <= 0 || ball.getCenterX() >= scene.getWidth()) ball.dx *= -1; //if hits left or right walls
        if (ball.getCenterY() <= 0) ball.dy *= -1; //if hits up and down walls

        // Paddle collision
        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            ball.dy *= -1; //reverses direction if ball hits the paddle
            double hitPos = ball.getCenterX() - (paddle.getX() + paddle.getWidth()/2);  //if hits the center of the paddle, ball goes mostly straight up
            // if hits the edges = ball goes diagonally left or right
            ball.dx = hitPos * 0.1;
        }

        // Ball falls below paddle
        if (ball.getCenterY() > scene.getHeight()) {
            gameOver = true;
            gameOverText.setText("GAME OVER!");
            gameOverText.setFill(Color.RED);
            gameOverSound.play();
            showDashboardWithDelay();
        }

        // Ball hits bricks score increases
        for (Brick brick : bricks) {
            if (!brick.destroyed && ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {
                brick.destroy();
                ball.dy *= -1;
                score += 10;
                scoreText.setText("Score: " + score);
                brickHitSound.play();
                break;
            }
        }

        // Check win to display win msg
        gameWon = bricks.stream().allMatch(b -> b.destroyed);
        if (gameWon) {
            gameOverText.setText("YOU WIN!");
            gameOverText.setFill(Color.GREEN);
            gameWinSound.play();
            showDashboardWithDelay();
        }
    }

    // show dashboard after 2 seconds
    private void showDashboardWithDelay() {
        gameOverText.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(2000); // wait for 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                gameOverText.setVisible(false);
                ball.setVisible(false);
                paddle.setVisible(false);
                scoreText.setVisible(false);
                for (Brick brick : bricks) {
                    brick.setVisible(false);
                }

                // ★ DASHBOARD TITLE AND SCORE
                final String titleText = gameOver ? "GAME OVER!" : "YOU WIN!";
                finalScoreText.setText(titleText);
                finalScoreText.setFill(gameOver ? Color.RED : Color.GREEN);
                finalScoreText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
                finalScoreText.setX((scene.getWidth() - finalScoreText.getLayoutBounds().getWidth()) / 2);
                finalScoreText.setY(250);

                // Score below title in white
                Text scoreDisplay = new Text("Score: " + score);
                scoreDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 28));
                scoreDisplay.setFill(Color.WHITE);
                scoreDisplay.setX((scene.getWidth() - scoreDisplay.getLayoutBounds().getWidth()) / 2);
                scoreDisplay.setY(320);

                dashboard.getChildren().removeIf(node -> node instanceof Text && node != restartText); // remove previous texts except restart
                dashboard.getChildren().addAll(finalScoreText, scoreDisplay);

                restartText.setX((scene.getWidth() - restartText.getLayoutBounds().getWidth()) / 2); // center restart
                dashboard.setVisible(true);

                // ★ ADDED — fancy fade-in animation
                FadeTransition fade = new FadeTransition(Duration.seconds(1.2), dashboard);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();

                // ★ ADDED — smooth scale animation
                ScaleTransition scale = new ScaleTransition(Duration.seconds(1.2), dashboard);
                scale.setFromX(0.8);
                scale.setFromY(0.8);
                scale.setToX(1);
                scale.setToY(1);
                scale.play();
            });
        }).start();
    }

    // Method to reset the game when R is pressed
    private void resetGame() {
        ball.setVisible(true);
        paddle.setVisible(true);
        scoreText.setVisible(true);

        // Reset ball position and speed
        ball.setCenterX(300);
        ball.setCenterY(400);
        ball.dx = 6;  // set your desired speed
        ball.dy = -6;

        // Reset paddle position
        paddle.setX(250);

        // Reset bricks
        for (Brick brick : bricks) {
            brick.destroyed = false;
            brick.setVisible(true);
        }

        // Reset score
        score = 0;
        scoreText.setText("Score: 0");

        // Reset game state
        gameOver = false;
        gameWon = false;

        // Hide dashboard
        dashboard.setVisible(false);
    }
}
