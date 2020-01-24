package main;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class CollisionDetector {

    static Vector2D lineLine(Line l1, Line l2){
        float x1 = (float)l1.getStartX();
        float y1 = (float)l1.getStartY();
        float x2 = (float)l1.getEndX();
        float y2 = (float)l1.getEndY();

        float x3 = (float)l2.getStartX();
        float y3 = (float)l2.getStartY();
        float x4 = (float)l2.getEndX();
        float y4 = (float)l2.getEndY();

        float v = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        float uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / v;
        float uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / v;

        if(uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1){
            float intersectionX = x1 + (uA * (x2-x1));
            float intersectionY = y1 + (uA * (y2-y1));
            return new Vector2D(intersectionX, intersectionY);
        }

        return null;
    }

    static boolean lineCircle(Line l, Circle c){
        float x1 = (float)l.getStartX();
        float y1 = (float)l.getStartY();
        float x2 = (float)l.getEndX();
        float y2 = (float)l.getEndY();
        float cx = (float)c.getCenterX();
        float cy = (float)c.getCenterY();
        float r = (float)c.getRadius();

        boolean inside1 = pointCircle(c, new Vector2D((float)l.getStartX(), (float)l.getStartY()));
        boolean inside2 = pointCircle(c, new Vector2D((float)l.getEndX(), (float)l.getEndY()));
        if(inside1 || inside2) return true;

        float distX = x1 - x2;
        float distY = y1 - y2;
        float distance = (float)Math.sqrt((distX*distX) + (distY*distY));
        float dotProduct = (float)( ( ((cx-x1)*(x2-x1)) + ((cy-y1)*(y2-y1)) ) / Math.pow(distance, 2) );
        float closestX = x1 + (dotProduct * (x2-x1));
        float closestY = y1 + (dotProduct * (y2-y1));
        boolean onSegment = linePoint(l, new Vector2D(closestX,closestY));

        if(!onSegment) return false;

        distX = closestX - cx;
        distY = closestY - cy;
        distance = (float)Math.sqrt((distX*distX) + (distY*distY));

        return distance <= r;
    }

    static boolean circleRectangle(Circle c, Rectangle r){
        float cx = (float)c.getCenterX();
        float cy = (float)c.getCenterY();
        float radius = (float)c.getRadius();
        float rx = (float)r.getX();
        float ry = (float)r.getY();
        float rw = (float)r.getWidth();
        float rh = (float)r.getHeight();
        float testX = cx;
        float testY = cy;

        if(cx < rx)         testX = rx;
        else if(cx > rx+rw) testX = rx + rw;
        if(cy < ry)         testY = ry;
        else if(cy > ry+rh) testY = ry + rh;

        float distX = cx - testX;
        float distY = cy - testY;
        float distance = (float)Math.sqrt((distX*distX) + (distY*distY));

        return distance <= radius;
    }

    static Vector2D lineRectangle(Line l, Rectangle r){
        ArrayList<Vector2D> hitPositions = new ArrayList<>();
        float rx = (float)r.getX();
        float ry = (float)r.getY();
        float rw = (float)r.getWidth();
        float rh = (float)r.getHeight();
        Vector2D left = lineLine(l, new Line(rx, ry, rx, ry+rh));
        Vector2D right = lineLine(l, new Line(rx+rw, ry, rx+rw,ry+rh));
        Vector2D top = lineLine(l, new Line(rx, ry, rx+rw, ry));
        Vector2D bottom = lineLine(l, new Line(rx, ry+rh, rx+rw, ry+rh));

        if(left != null) hitPositions.add(left);
        if(right != null) hitPositions.add(right);
        if(top != null) hitPositions.add(top);
        if(bottom != null)hitPositions.add(bottom);

        if (!hitPositions.isEmpty()) return getNearest(l, hitPositions);
        return null;
    }

    private static Vector2D getNearest(Line l, ArrayList<Vector2D> hitPositions){
        float shortestDistance = Float.POSITIVE_INFINITY;
        Vector2D tempVector = new Vector2D();

        for(Vector2D v: hitPositions){
            float distX = (float)l.getStartX() - v.get_x();
            float distY = (float)l.getStartY() - v.get_y();
            float distance = (float)Math.sqrt((distX*distX) + (distY*distY));

            if(distance < shortestDistance){
                shortestDistance = distance;
                tempVector = v;
            }
        }

        return tempVector;
    }

    private static boolean pointCircle(Circle c, Vector2D p){
        float px = p.get_x();
        float py = p.get_y();
        float cx = (float)c.getCenterX();
        float cy = (float)c.getCenterY();
        float r = (float)c.getRadius();
        float distX = px - cx;
        float distY = py - cy;
        float distance = (float)Math.sqrt((distX*distX) + (distY*distY));

        return distance <= r;
    }

    private static boolean linePoint(Line l, Vector2D p){
        float x1 = (float)l.getStartX();
        float y1 = (float)l.getStartY();
        float x2 = (float)l.getEndX();
        float y2 = (float)l.getEndY();
        float px = p.get_x();
        float py = p.get_y();
        float distX1 = px - x1;
        float distX2 = px - x2;
        float distX3 = x1 - x2;
        float distY1 = py - y1;
        float distY2 = py - y2;
        float distY3 = y1 - y2;
        float dist1 = (float)Math.sqrt((distX1*distX1) + (distY1*distY1));
        float dist2 = (float)Math.sqrt((distX2*distX2) + (distY2*distY2));
        float lineLength = (float)Math.sqrt((distX3*distX3) + (distY3*distY3));
        float buffer = 0.1f;

        return dist1 + dist2 >= lineLength - buffer && dist1 + dist2 <= lineLength + buffer;
    }
}
