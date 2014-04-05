//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.map;

/**
 * Class to represent positions on the map.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

}
