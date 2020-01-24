package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int SIZE = 800;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = new Pane();
        root.setStyle("-fx-background-color: lightgrey;");
        Scene scene = new Scene(root, SIZE, SIZE);
        primaryStage.setTitle("Raycast2D");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        GameManager.init(scene, root);

        new AnimationTimer(){
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if(now - lastUpdate >= 10_000_000){
                    GameManager.gameLoop();
                    lastUpdate = now;
                }
            }
        }.start();
    }

    static int getSize(){
        return SIZE;
    }

    public static void main(String[] args) {
        launch(args);
    }
}