//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap;

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

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

}
