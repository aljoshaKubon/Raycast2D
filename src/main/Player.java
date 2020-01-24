package main;

import javafx.scene.shape.Circle;

class Player {
    private Vector2D _pos;
    private Vector2D _dir;
    private Circle _circle;
    private float _speed;

    Player(Vector2D pos, Circle circle){
        _pos = pos;
        _circle = circle;
        _dir = new Vector2D();
        _speed = 2;
    }

    Circle getNextMovePosition(){
        Vector2D nextPos = new Vector2D(_pos);
        Vector2D nextDir = new Vector2D(_dir);
        nextDir.normalize();
        nextDir.mul(_speed);
        nextPos.add(nextDir);

        return new Circle(nextPos.get_x(), nextPos.get_y(), _circle.getRadius(), _circle.getFill());
    }

    void move() {
        _dir.normalize();
        _dir.mul(_speed);
        _pos.add(_dir);
    }

    Vector2D get_pos(){
        return _pos;
    }

    Vector2D get_dir(){
        return _dir;
    }

    Circle get_circle(){
        return _circle;
    }
}