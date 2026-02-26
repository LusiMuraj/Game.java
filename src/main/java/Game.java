
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Application {

    private static int width = 1024;
    private static int height = 576;
    private static int playerSize = 100;
    private static int winScore = 10;

    private Image playerImageDown;
    private Image playerImageUp;
    private Image playerImageRight;
    private Image playerImageLeft;
    private Image starImage;
    private Image fireImage;  // Image for fire
    private Image backgroundImage;
    private Image groundImage;
    private Image treeImage;
    private Image currentPlayerImage;
    private double playerX = width / 2.0 - playerSize / 2.0;
    private double playerY = height - 200;
    private int score = 0;
    private ArrayList<double[]> stars = new ArrayList<>();
    private ArrayList<double[]> fires = new ArrayList<>();
    private boolean gameRunning = true;
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mini Game");

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        playerImageDown = new Image("bird-wings-flying-down.gif");
        playerImageUp = new Image("bird-wings-flying-up.gif");
        playerImageRight = new Image("bird-wings-flying-right.gif");
        playerImageLeft = new Image("bird-wings-flying-left.gif");
        currentPlayerImage = playerImageRight; // Lojtari fillon i drejtuar nga ana e djathte
        starImage = new Image("star.gif");
        fireImage = new Image("fire.png");
        backgroundImage = new Image("background.png");
        groundImage = new Image("ground.png");
        treeImage = new Image("tree.png");

        //metoda timeline per animim
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnKeyPressed(this::processKey);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void run(GraphicsContext gc) {
        if(gameRunning){
            // imazhi i background
            gc.drawImage(backgroundImage, 0, 0, width, height);
            // imazhi per token
            gc.drawImage(groundImage, 0, height - 70, width, 170);
            // imazhet per pemet
            gc.drawImage(treeImage, 65, height - 123, 100, 100); // First tree
            gc.drawImage(treeImage, 885, height - 123, 100, 100); // Second tree
            gc.drawImage(treeImage, 35, height - 123, 100, 100);
            gc.drawImage(treeImage, 10, height - 123, 100, 100);
            gc.drawImage(treeImage, 910, height - 123, 100, 100);
            gc.drawImage(treeImage, 860, height - 123, 100, 100);
            // imazhi per player
            gc.drawImage(currentPlayerImage, playerX, playerY, playerSize, playerSize);

            // per cdo star ne array stars vendos imazhin dhe
            for (double[] star : stars) {
                gc.drawImage(starImage, star[0], star[1], 80, 80);
            }
            //kodi per te levizur yjet
            for (double[] star : stars) {
                star[1] += 5; // Leviz yjet poshte
            }
            // per cdo fire ne array fires vendos imazhin
            for (double[] fire : fires) {
                gc.drawImage(fireImage, fire[0], fire[1], 100, 100);
            }
            //kodi per te levizur zjarrin
            for (double[] fire : fires) {
                fire[1] += 5; // Leviz zjarrin poshte
            }

            // kodi per te pare nqs player ka prekur yjet
            for (int i = 0; i < stars.size(); i++) {
                double[] star = stars.get(i);
                if (playerX < star[0] + 10 && playerX + playerSize > star[0] && playerY < star[1] + 10 && playerY + playerSize > star[1]) {
                    stars.remove(star);
                    score++;
                }
            }
            // kodi per te pare nqs player ka prekur yjet
            for (int i = 0; i < fires.size(); i++) {
                double[] fire = fires.get(i);
                if (playerX < fire[0] + 40 && playerX + playerSize > fire[0] + 40 && playerY < fire[1] + 40 && playerY + playerSize > fire[1] + 40) {
                    fires.remove(fire);
                    gameRunning = false;
                    timeline.stop();  // Ndalon animacionin
                    gc.setFill(Color.RED);
                    gc.setFont(new Font(40));
                    gc.fillText("You Lose! Press space to try Again!!", width / 2.0 - 330, height / 2.0);
                    return;
                }
            }

            // shto yjet
            if (Math.random() < 0.05) { // Shpejtesia e shfaqjes se yjeve
                stars.add(new double[]{new Random().nextInt(width - 80), -80});
            }

            // shto zjarret
            if (Math.random() < 0.02) {  // Shpejtesia e shfaqjes se zjarrit
                fires.add(new double[]{new Random().nextInt(width - 80), -80});
            }

            // bej display piket
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(20));
            gc.fillText("Score: " + score, 10, 20);

            // kontrollo piket
            if (score >= winScore) {
                gameRunning = false;
                timeline.stop();
                gc.setFill(Color.GOLD);
                gc.setFont(new Font(40));
                gc.fillText("You Win!Press space to try Again!!", width / 2.0 - 330, height / 2.0);
            }
        }else{
            return;
        }

    }

    private void processKey(KeyEvent event) {
        if (gameRunning) {
            if (event.getCode() == KeyCode.LEFT && playerX > 0) {
                playerX -= 20;
                currentPlayerImage = playerImageLeft;
            }
            if (event.getCode() == KeyCode.RIGHT && playerX < width - playerSize) {
                playerX += 20;
                currentPlayerImage = playerImageRight;
            }
            if (event.getCode() == KeyCode.UP && playerY > 0) {
                playerY -= 20;
                currentPlayerImage = playerImageUp;
            }
            if (event.getCode() == KeyCode.DOWN && playerY < height - playerSize) {
                playerY += 20;
                currentPlayerImage = playerImageDown;
            }
        }else{
            if (event.getCode() == KeyCode.SPACE) {
                resetGame();
            }
            return;
        }

    }

    private void resetGame() {
        playerX = width / 2.0 - playerSize / 2.0;
        playerY = height - 150;
        score = 0;
        stars.clear();
        fires.clear();
        gameRunning = true;
        timeline.play();
    }
}