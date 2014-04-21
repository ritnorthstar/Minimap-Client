//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.itinerary;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a user's itinerary.
 */
public class Itinerary {

    private List<ItineraryPoint> itinerary;
    private int currentPoint = 0;
    private boolean[] visited;

    public Itinerary(List<ItineraryPoint> itinPoints){
        this.itinerary = itinPoints;
        visited = new boolean[itinPoints.size()];
        for(int i = 0; i < visited.length; i++){
            visited[i] = false;
        }
    }
    
    public int getCount(){
    	return itinerary.size();
    }
    
    public ItineraryPoint getItineraryPoint(int position){
    	return itinerary.get(position);
    }



}
