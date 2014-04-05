//John Paul Mardelli
//Last updated November 9th, 2013

package com.northstar.minimap.map;

import java.util.ArrayList;
import java.util.List;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.itinerary.ItineraryPoint;

public class Map {

    private List<IBeacon> beacons = new ArrayList<IBeacon>();
    private List<Table> tables = new ArrayList<Table>();
    private String mapID;
    
    public void setMapID(String id){
    	mapID = id;
    }
    
    public String getMapID(){
    	return mapID;
    }
    
    public void addBeacon(IBeacon beacon){
    	beacons.add(beacon);
    }
    
    public List<IBeacon> getBeacons(){
    	return beacons;
    }
    
    public void addTable(Table table){
    	tables.add(table);
    }
    
    public List<Table> getTables(){
    	return tables;
    }

}
