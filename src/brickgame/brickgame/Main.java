package brickgame;

import javafx.application.Application; //base class for all javafx
import javafx.stage.Stage; //the window where the application is created
import javafx.scene.Scene; //content inside the stage which holds all UI elements
import javafx.scene.layout.Pane; //holds nodes of the UI like buttons and shapes

public class Main extends Application {
    @Override
    public void start (Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(600, 600);

        Scene scene = new Scene(root);

        Game game = new Game (root, scene);
        game.start();

        stage.setScene(scene);
        stage.setTitle("Brick Breaker");
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
