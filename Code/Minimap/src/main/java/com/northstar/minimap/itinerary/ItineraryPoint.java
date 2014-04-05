//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.itinerary;

import com.northstar.minimap.map.Position;

public class ItineraryPoint {

    private Position pos;
    private String name;
    //Maybe put like a thumbnail or something


    public ItineraryPoint(String name, Position location){
        this.name = name;
        pos = location;
    }

    public String getName(){
        return name;
    }

    public Position getPos(){
        return pos;
    }

}
