//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.itinerary;

import java.util.ArrayList;

/**
 * Class to represent a user's itinerary.
 */
public class Itinerary {

    private ArrayList<ItineraryPoint> itinerary;
    private int currentPoint = 0;
    private boolean[] visited;

    public Itinerary(ArrayList<ItineraryPoint> itinerary){
        this.itinerary = itinerary;
        visited = new boolean[itinerary.size()];
        for(int i = 0; i < visited.length; i++){
            visited[i] = false;
        }
    }



}
