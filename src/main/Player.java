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

    Player(Vector2D pos, Circle circle){
        _pos = pos;
        _circle = circle;
        _dir = new Vector2D();
        _speed = 2;
        _rotationSpeed = 2;
        _rotationDir = 0;
        _angle = new SimpleFloatProperty();
    }

    void update(){
        move();
        rotate();
    }

    private void move() {
        if(_velocity != 0){
            _dir.set_x((float) Math.cos(Math.toRadians(_angle.get())));
            _dir.set_y((float) Math.sin(Math.toRadians(_angle.get())));
            _dir.normalize();
            _dir.mul(_speed*_velocity);
            _pos.add(_dir);
        }
    }

    private void rotate(){
        NumberBinding sum = Bindings.add(_rotationDir*_rotationSpeed, _angle);
        if(sum.floatValue() > 360) sum.subtract(360);
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