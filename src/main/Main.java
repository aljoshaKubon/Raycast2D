package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = new Pane();
        root.setStyle("-fx-background-color: lightgrey;");
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Raycast2D");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        GameManager.init(scene, root);

        new AnimationTimer(){
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                GameManager.gameLoop();
                lastUpdate = now;
            }
        }.start();
    }

    static int[] getSize(){
        return new int[]{WIDTH, HEIGHT};
    }

    public static void main(String[] args) {
        launch(args);
    }
}