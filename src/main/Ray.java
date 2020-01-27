package main;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Ray extends Line {
    private Color _colorOnHit = null;

    void updateCollision(Node node){
        Vector2D collisionPoint = null;
        if(node instanceof Line){
            Line wall = (Line)node;
            collisionPoint = CollisionDetector.lineLine(this, wall);
        }else if(node instanceof Rectangle){
            Rectangle obstacle = (Rectangle)node;
            collisionPoint = CollisionDetector.lineRectangle(this, obstacle);
        }

        if(collisionPoint != null){
            this.setEndX(collisionPoint.get_x());
            this.setEndY(collisionPoint.get_y());
            _colorOnHit = (Color) ((Shape) node).getFill();
        }
    }

    Color getColorOnHit(){
        return _colorOnHit;
    }
}
