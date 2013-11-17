//John Paul Mardelli
//Last updated November 9th, 2013

package com.northstar.minimap.map;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.itinerary.ItineraryPoint;

public class Map {

    private IBeacon IBeacon[];
    private ItineraryPoint currentItin;

    public void setDestination(ItineraryPoint destination){
        currentItin = destination;
    }

    public void drawItinerary(){

    }

}
