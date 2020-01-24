package main;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

class GameManager {
    private static Player player;
    private static ArrayList<Line> walls;
    private static ArrayList<Line> rays;
    private static ArrayList<Rectangle> obstacles;
    private static final int NUMBER_OF_RAYS = 100;
    private static final float VIEWING_ANGLE = 60;

    static void gameLoop(){
        updateRays();
        if(collisionDetection()){
            player.move();
            player.get_circle().toFront();
        }
    }

    static void init(Scene scene, Pane root){
        initListeners(scene);
        initPlayer(root);
        initWalls(root);
        initObstacles(root);
        initRays(root);
    }

    private static boolean collisionDetection(){
        Circle nextPosition = player.getNextMovePosition();

        for(Line l: rays){
            for(Line w: walls) {
                Vector2D collisionPoint = CollisionDetector.lineLine(l, w);

                if(collisionPoint != null){
                    l.setEndX(collisionPoint.get_x());
                    l.setEndY(collisionPoint.get_y());
                }
            }

            for(Rectangle r: obstacles){
                Vector2D collisionPoint = CollisionDetector.lineRectangle(l, r);

                if(collisionPoint != null){
                    l.setEndX(collisionPoint.get_x());
                    l.setEndY(collisionPoint.get_y());
                }
            }
        }

        for(Line l: walls){
            if(CollisionDetector.lineCircle(l, nextPosition)) return false;
        }

        for(Rectangle r: obstacles){
            if(CollisionDetector.circleRectangle(nextPosition, r)) return false;
        }

        return true;
    }

    private static void updateRays(){
        for(int i = 0; i < rays.size(); i++){
            float x = (float)(rays.get(i).getStartX() + 1000*Math.sin(Math.toRadians((VIEWING_ANGLE/rays.size())*i)) );
            float y = (float)(rays.get(i).getStartY() + 1000*Math.cos(Math.toRadians((VIEWING_ANGLE/rays.size())*i)) );

            rays.get(i).setEndX(x);
            rays.get(i).setEndY(y);
        }
    }

    private static void initListeners(Scene scene){
        scene.setOnKeyPressed( e ->{
            KeyCode keyCode = e.getCode();

            if(keyCode.equals(KeyCode.S)){
                player.get_dir().set_y(1);
            }
            if(keyCode.equals(KeyCode.W)){
                player.get_dir().set_y(-1);
            }
            if(keyCode.equals(KeyCode.A)){
                player.get_dir().set_x(-1);
            }
            if(keyCode.equals(KeyCode.D)){
                player.get_dir().set_x(1);
            }
        });

        scene.setOnKeyReleased( e ->{
            KeyCode keyCode = e.getCode();

            if(keyCode.equals(KeyCode.S) || keyCode.equals(KeyCode.W)){
                player.get_dir().set_y(0);
            }
            if(keyCode.equals(KeyCode.A) || keyCode.equals(KeyCode.D)){
                player.get_dir().set_x(0);
            }
        });
    }

    private static void initPlayer(Pane root){
        Circle circle = new Circle();
        player = new Player(new Vector2D((float)Main.getSize()/2,(float)Main.getSize()/2), circle);
        circle.setRadius(10);
        circle.setFill(Color.CORNFLOWERBLUE);
        circle.centerXProperty().bind(player.get_pos().get_property_x());
        circle.centerYProperty().bind(player.get_pos().get_property_y());
        root.getChildren().add(circle);
    }

    private static void initWalls(Pane root){
        walls = new ArrayList<Line>();

        //Top wall
        Line uWall = new Line(0, 0, Main.getSize(), 0);
        walls.add(uWall);
        //Left wall
        Line lWall = new Line(0, 0, 0, Main.getSize());
        walls.add(lWall);
        //Right wall
        Line rWall = new Line(Main.getSize(), 0, Main.getSize(), Main.getSize());
        walls.add(rWall);
        //Bottom wall
        Line dWall = new Line(0, Main.getSize(), Main.getSize(), Main.getSize());
        walls.add(dWall);

        root.getChildren().addAll(walls);
    }

    private static void initObstacles(Pane root){
        obstacles = new ArrayList<Rectangle>();
        Random r = new Random();
        final int obstacleCount = 5;

        for(int i = 0; i < obstacleCount; i++){
            Rectangle rect = new Rectangle(r.nextInt(Main.getSize() + 10)-10, r.nextInt(Main.getSize() + 10)-10, 50 + r.nextInt(50), 50 + r.nextInt(50));
            rect.setFill(Color.RED);
            obstacles.add(rect);
        }
        root.getChildren().addAll(obstacles);
    }

    private static void initRays(Pane root){
        rays = new ArrayList<Line>();

        for(int i = 0; i < NUMBER_OF_RAYS; i++){
            Line line = new Line();
            line.setStroke(Color.YELLOW);
            line.startXProperty().bind(player.get_pos().get_property_x());
            line.startYProperty().bind(player.get_pos().get_property_y());
            line.toBack();
            rays.add(line);
        }
        root.getChildren().addAll(rays);
    }
}