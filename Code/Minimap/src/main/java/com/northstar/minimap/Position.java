//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap;

/**
 * Class to represent positions on the map.
 */
public class Position {

    private float x;
    private float y;

    public Position(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

}
