package main;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.shape.Circle;

class Player {
    private Vector2D _pos;
    private Vector2D _dir;
    private float _velocity;
    private float _speed;
    private float _rotationSpeed;
    private float _rotationDir;
    private final FloatProperty _angle;
    private final Circle _circle;

    /**
     * Constructor of the player class
     * @param pos Object of the class Vector2D. A simple self written class with a few functions to calculate.
     * @param circle Node of the JavaFX framework. It is used to display the player on the screen.
     */
    Player(Vector2D pos, Circle circle){
        _pos = pos;
        _circle = circle;
        _dir = new Vector2D();
        _speed = 2;
        _rotationSpeed = 2;
        _rotationDir = 0;
        _angle = new SimpleFloatProperty();
    }

    /**
     * Update method for moving and rotating the player.
     * This method is called every frame in the gameLoop by the static GameManager class.
     */
    void update(){
        move();
        rotate();
    }

    /**
     * Move method/function of the player.
     * This method is called every frame by the update function.
     * The method/function is checking the _velocity variable which is modified by the ActionListeners with the keys W and S.
     * x and y of the _dir Vector2 object are set by using trigonometric functions.
     */
    private void move() {
        if(_velocity != 0){
            _dir.set_x((float) Math.cos(Math.toRadians(_angle.get())));
            _dir.set_y((float) Math.sin(Math.toRadians(_angle.get())));
            _dir.normalize();
            _dir.mul(_speed*_velocity);
            _pos.add(_dir);
        }
    }

    /**
     * Rotate method/function of the player.
     * The current angle is summed by the product of the rotation direction and the rotation speed.
     * The angle is clamped between 0 and 360.
     */
    private void rotate(){
        NumberBinding sum = Bindings.add(_rotationDir*_rotationSpeed, _angle);
        if(sum.floatValue() > 360) sum.subtract(360);
        else if(sum.floatValue() < 0) sum.add(360);
        _angle.set(sum.floatValue());
    }

    void set_rotationDir(float rotationDir){
        _rotationDir = rotationDir;
    }

    void set_velocity(float vel){
        _velocity = vel;
    }

    float get_angle(){
        return _angle.get();
    }

    /**
     * Method/Function to calculate the position of the next frame.
     * This method is used by the collision detection in the GameManager.
     * @return a new declared circle which is used by the CollisionDetector.
     */
    final Circle getNextMovePosition(){
        Vector2D nextPos = new Vector2D(_pos);
        Vector2D nextDir = new Vector2D(_dir);
        nextDir.normalize();
        nextDir.mul(_speed*_velocity);
        nextPos.add(nextDir);

        return new Circle(nextPos.get_x(), nextPos.get_y(), _circle.getRadius(), _circle.getFill());
    }

    final Circle get_circle(){
        return _circle;
    }

    Vector2D get_pos(){
        return _pos;
    }

    final FloatProperty get_angle_property(){
        return _angle;
    }
}