package main;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Random;

class GameManager {
    private static Player player;
    private static ArrayList<Line> walls;
    private static ArrayList<Line> rays;
    private static ArrayList<Rectangle> obstacles;
    private static final int WIDTH = Main.getSize()[0];
    private static final int HEIGHT = Main.getSize()[1];
    private static final int NUMBER_OF_RAYS = 800;
    private static final float VIEWING_ANGLE = 45;
    private static Rectangle shadow;
    private static GraphicsContext gc;

    /**
     * The main loop in which everything is called and handled.
     * It is called every frame by the JavaFX AnimationTimer in the Main class.
     */
    static void gameLoop(){
        updateShadow();
        updateRays();

        if(collisionDetection()){
            player.update();
            player.get_circle().toFront();
        }

        drawOnCanvas();
    }

    /**
     * Aggregation of all initialising methods.
     * Is called by the Main class before starting the AnimationTimer.
     * @param scene Node of the JavaFX framework. Is used to add the actionListeners.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    static void init(Scene scene, Pane root){
        initListeners(scene);
        initPlayer(root);
        initWalls(root);
        initObstacles(root);
        initRays(root);
        initShadow(root);
        initCanvas(root);
    }

    private static void drawOnCanvas(){
        int greyScale;
        float length;
        float height;
        Line r;

        gc.clearRect(0,0, WIDTH/2, HEIGHT);
        for(int i = 0; i < rays.size()-1; i++){
            r = rays.get(i);
            length = (float)Math.sqrt(Math.pow((r.getEndX() - r.getStartX()),2) + Math.pow((r.getEndY() - r.getStartY()), 2));
            height = map(length, 0, 800, 0, HEIGHT/2);
            height = HEIGHT/2 - height;
            greyScale = (int)map(length, 0, 800, 0, 254);
            greyScale = 255-greyScale;

            gc.setFill(Color.rgb(greyScale, greyScale, greyScale));
            gc.fillRect((((float)WIDTH/2)/rays.size())*i, (float)HEIGHT/4, 1, height);
        }
    }

    private static float map(float value, float minA, float maxA, float minB, float maxB) {
        return (1 - ((value - minA) / (maxA - minA))) * minB + ((value - minA) / (maxA - minA)) * maxB;
    }

    /**
     * Collision detection for the player.
     * @return a boolean to decide whether the player is able to move or not.
     */
    private static boolean collisionDetection(){
        collisionDetectionRays();

        Circle nextPosition = player.getNextMovePosition();

        for(Line l: walls){
            if(CollisionDetector.lineCircle(l, nextPosition)) return false;
        }

        for(Rectangle r: obstacles){
            if(CollisionDetector.circleRectangle(nextPosition, r)) return false;
        }

        return true;
    }

    /**
     * Collision detection for the rays.
     */
    private static void collisionDetectionRays(){
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
    }

    /**
     * Update function/method for the shot rays.
     * The rays have their start position at the position of the player.
     * With the trigonometric functions sine and cosines the rays are shot in a viewing angle of 60° in player direction.
     * (VIEWING_ANGLE/rays.size())*i is used to shot the rays in cone.
     */
    private static void updateRays(){
        for(int i = 0; i < rays.size(); i++){
            float x = (float)(rays.get(i).getStartX() + 1000*Math.sin(Math.toRadians( (90 - VIEWING_ANGLE/2) + -player.get_angle()) + Math.toRadians(( VIEWING_ANGLE/rays.size())*i)) );
            float y = (float)(rays.get(i).getStartY() + 1000*Math.cos(Math.toRadians( (90 - VIEWING_ANGLE/2) + -player.get_angle()) + Math.toRadians(( VIEWING_ANGLE/rays.size())*i)) );

            rays.get(i).setEndX(x);
            rays.get(i).setEndY(y);
        }
    }

    /**
     * Update function/method for the shadow.
     * The variable lightSource is a created polygon in the form of the shot rays in inclusion of their hit points.
     * The shadow rectangle which covers the whole screen is now clipped by the calculated lightSource by subtracting the shadow with the lightSource and using it as clip shape.
     */
    private static void updateShadow() {

        Polygon lightSource = new Polygon();
        ArrayList<Double> points = new ArrayList<>();

        for (Line l : rays) {
            points.add(l.getEndX());
            points.add(l.getEndY());
        }
        points.add((double) player.get_pos().get_x());
        points.add((double) player.get_pos().get_y());

        lightSource.getPoints().addAll(points);
        lightSource.setSmooth(true);

        Shape clip = Shape.subtract(shadow, lightSource);
        shadow.setClip(clip);
    }

    /**
     * Method/Function to initialise the actionListeners. In this case they are keyListeners.
     * @param scene Node of the JavaFX framework. It is used to add the listeners onto it.
     */
    private static void initListeners(Scene scene){
        scene.setOnKeyPressed( e ->{
            KeyCode keyCode = e.getCode();

            if(keyCode.equals(KeyCode.S)){
                player.set_velocity(-1);
            }
            if(keyCode.equals(KeyCode.W)){
                player.set_velocity(1);
            }
            if(keyCode.equals(KeyCode.A)){
                player.set_rotationDir(-1);
            }
            if(keyCode.equals(KeyCode.D)){
                player.set_rotationDir(1);
            }
        });

        scene.setOnKeyReleased( e ->{
            KeyCode keyCode = e.getCode();

            if(keyCode.equals(KeyCode.S) || keyCode.equals(KeyCode.W)){
                player.set_velocity(0);
            }
            if(keyCode.equals(KeyCode.A) || keyCode.equals(KeyCode.D)){
                player.set_rotationDir(0);
            }
        });
    }

    /**
     * Method/Function to initialise the player.
     * A Circle node is created and bound to the players position and rotation properties.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initPlayer(Pane root){
        Circle circle = new Circle();
        player = new Player(new Vector2D((float)(WIDTH/2)/2,(float)HEIGHT/2), circle);
        circle.setRadius(10);
        circle.setFill(Color.CORNFLOWERBLUE);
        circle.centerXProperty().bind(player.get_pos().get_property_x());
        circle.centerYProperty().bind(player.get_pos().get_property_y());
        circle.rotateProperty().bind(player.get_angle_property());
        root.getChildren().add(circle);
    }

    /**
     * Method/Function to initialise the walls.
     * The walls are the boundaries for the player and the shot rays.
     * They are set in the positions of screen edges.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initWalls(Pane root){
        walls = new ArrayList<>();

        //Top wall
        Line uWall = new Line(0, 0, (float)WIDTH/2, 0);
        walls.add(uWall);
        //Left wall
        Line lWall = new Line(0, 0, 0, HEIGHT);
        walls.add(lWall);
        //Right wall
        Line rWall = new Line((float)WIDTH/2, 0, (float)WIDTH/2, HEIGHT);
        walls.add(rWall);
        //Bottom wall
        Line dWall = new Line(0, HEIGHT, (float)WIDTH/2, HEIGHT);
        walls.add(dWall);

        root.getChildren().addAll(walls);
    }

    /**
     * Method/Function to initialise the obstacles.
     * The obstacles are generated with random positions and sizes.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initObstacles(Pane root){
        obstacles = new ArrayList<>();
        Random r = new Random();
        final int obstacleCount = 5;

        for(int i = 0; i < obstacleCount; i++){
            Rectangle rect = new Rectangle(r.nextInt(WIDTH/2 + 50)-100, r.nextInt(HEIGHT + 10)-10, 50 + r.nextInt(50), 50 + r.nextInt(50));
            rect.setFill(Color.RED);
            obstacles.add(rect);
        }
        root.getChildren().addAll(obstacles);
    }

    /**
     * Method/Function to initialise the rays.
     * The rays have their start position bound at the center of the player circle.
     * For debugging is is possible to set the rays visible to look at their beaming route.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initRays(Pane root){
        rays = new ArrayList<>();

        for(int i = 0; i < NUMBER_OF_RAYS; i++){
            Line line = new Line();
            line.setStroke(Color.YELLOW);
            line.startXProperty().bind(player.get_pos().get_property_x());
            line.startYProperty().bind(player.get_pos().get_property_y());
            line.toBack();
            line.setVisible(false);
            rays.add(line);
        }
        root.getChildren().addAll(rays);
    }

    /**
     * Method/Function to initialise the shadow.
     * Simple black rectangle which covers the whole screen.
     * In the updateShadow method/function it is clipped to give the player the feeling of a flashlight.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initShadow(Pane root){
        shadow = new Rectangle(0,0, (float)WIDTH/2, HEIGHT);
        root.getChildren().add(shadow);
    }

    /**
     * Method/Function to initialise the canvas.
     * The canvas is used to display the pseudo 3D perspective of the ray casting process.
     * @param root Node of the JavaFX framework. Is used to add the initialised nodes as children to see them on the screen.
     */
    private static void initCanvas(Pane root){
        Canvas canvas = new Canvas((float)WIDTH/2, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        canvas.setTranslateX((float)WIDTH/2);
        root.getChildren().add(canvas);
    }
}