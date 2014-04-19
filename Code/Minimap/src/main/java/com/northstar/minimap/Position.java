//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap;

import android.graphics.Point;

/**
 * Class to represent positions on the map.
 */
public class Position {

    private double x;
    private double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Position(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public double distance(Position p) {
        return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public Point toPoint() {
        return new Point((int) Math.round(x), (int) Math.round(y));
    }
}
