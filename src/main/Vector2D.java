package main;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class Vector2D {
    private FloatProperty _x = new SimpleFloatProperty();
    private FloatProperty _y = new SimpleFloatProperty();

    Vector2D(){
        this(0,0);
    }

    Vector2D(Vector2D v) {
        this(v.get_x(), v.get_y());
    }

    Vector2D(double x, double y){
        set_x((float)x);
        set_x((float)y);
    }

    Vector2D(float x, float y) {
        set_x(x);
        set_y(y);
    }

    void set_x(float v){
        _x.set(v);
    }

    void set_y(float v){
        _y.set(v);
    }

    final FloatProperty get_property_x(){return _x;}

    final FloatProperty get_property_y(){return _y;}

    float get_x(){return _x.get();}

    float get_y(){return _y.get();}

    float getAngleBetween(Vector2D v){
        float magn1 = this.getLength();
        float magn2 = v.getLength();
        float dotProduct = this.getDotProduct(v);
        return dotProduct / (magn1*magn2);
    }

    float getDotProduct(Vector2D v){
        return (this.get_x() + v.get_x()) + (this.get_y() + v.get_y());
    }

    void add(Vector2D v){
        NumberBinding sum = Bindings.add(_x, v.get_property_x());
        _x.set(sum.floatValue());
        sum = Bindings.add(_y, v.get_property_y());
        _y.set(sum.floatValue());
    }

    void mul(float v){
        NumberBinding product = Bindings.multiply(_x, v);
        _x.set(product.floatValue());
        product = Bindings.multiply(_y, v);
        _y.set(product.floatValue());
    }

    void normalize() {
        float magnitude = getLength();
        NumberBinding quotient;
        if(magnitude != 0){
            if(_x.get() != 0){
                quotient = Bindings.divide(_x, magnitude);
                _x.set(quotient.floatValue());
            }
            if(_y.get() != 0){
                quotient = Bindings.divide(_y, magnitude);
                _y.set(quotient.floatValue());
            }
        }
    }

    private float getLength() {
        return (float) Math.sqrt(_x.get() * _x.get() + _y.get() * _y.get());
    }

    @Override
    public String toString(){
        return "(" + _x + "," + _y + ")";
    }
}