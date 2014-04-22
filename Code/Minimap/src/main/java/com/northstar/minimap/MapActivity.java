package com.northstar.minimap;

import com.northstar.minimap.beacon.BeaconListener;
import com.northstar.minimap.beacon.BeaconManager;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;
import com.northstar.minimap.itinerary.Itinerary;
import com.northstar.minimap.itinerary.ItineraryPoint;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;
import com.northstar.minimap.map.UserPositionListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends Activity {

    public static final int MAP_HEIGHT = 600;
    public static final int MAP_WIDTH = 600;
    public static Position MAP_NE_CORNER = toMapPosition(new Position(MAP_WIDTH, 0));
    public static Position MAP_SW_CORNER = toMapPosition(new Position(0, MAP_HEIGHT));

    private BeaconManager beaconManager;
    private UserPositionListener userPositionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        beaconManager = new BeaconManager(this);
    }


    public void calibrate(Position calibrationPosition) {
        beaconManager.calibrate(calibrationPosition);
    }
    
    public void processMap(){
    	
    	Map testMap = new Map();
        
        testMap.setMapID("0");

        List<Table> tables = new ArrayList<Table>();


        Position p1 = toMapPosition(new Position(0.0, 0.0));
        StickNFindBluetoothBeacon b1 = new StickNFindBluetoothBeacon(null, 0, "FD:65:28:71:80:C0", p1);

        Position p2 = toMapPosition(new Position(PositionCalculator.GRID_WIDTH, 0.0));
        StickNFindBluetoothBeacon b2 = new StickNFindBluetoothBeacon(null, 1, "FE:CC:38:AA:BE:B5", p2);

        Position p3 = toMapPosition(new Position(0.0, PositionCalculator.GRID_HEIGHT));
        StickNFindBluetoothBeacon b3 = new StickNFindBluetoothBeacon(null, 2, "E4:3E:A0:63:BC:C6", p3);

        Position p4 = toMapPosition(new Position(
                PositionCalculator.GRID_WIDTH, PositionCalculator.GRID_HEIGHT));
        StickNFindBluetoothBeacon b4 = new StickNFindBluetoothBeacon(null, 3, "E3:BF:2E:56:BF:B9", p4);

        for (Table table: tables) {
            testMap.addTable(table);
        }
        
        testMap.addBeacon(b1);
        testMap.addBeacon(b2);
        testMap.addBeacon(b3);
        testMap.addBeacon(b4);
        
        CustomMapFragment mapFrag = (CustomMapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        
        mapFrag.setMap(testMap);
        
        processItinerary();
    }
    
    public void processItinerary(){
    	Log.w("JP", "Fake Itin being sent to frag");
    	List<ItineraryPoint> itinPoints = new ArrayList<ItineraryPoint>();
    	
    	ItineraryPoint ip1 = new ItineraryPoint("Point 1", new Position(50.0, 50.0));
    	ItineraryPoint ip2 = new ItineraryPoint("Point 2", new Position(100.0, 100.0));
    	ItineraryPoint ip3 = new ItineraryPoint("Point 3", new Position(150.0, 150.0));
    	ItineraryPoint ip4 = new ItineraryPoint("Point 4", new Position(150.0, 150.0));
    	ItineraryPoint ip5 = new ItineraryPoint("Point 5", new Position(150.0, 150.0));
    	ItineraryPoint ip6 = new ItineraryPoint("Point 6", new Position(150.0, 150.0));
    	
    	itinPoints.add(ip1);
    	itinPoints.add(ip2);
    	itinPoints.add(ip3);
    	itinPoints.add(ip4);
    	itinPoints.add(ip5);
    	itinPoints.add(ip6);
    	
    	Itinerary testItinerary = new Itinerary(itinPoints);
    	
    	ItineraryFragment itinFrag = (ItineraryFragment) getFragmentManager().findFragmentById(R.id.itinerary_fragment);
    	
    	itinFrag.setItinerary(testItinerary);
    }
    
    public void setCurrentItineraryPoint(ItineraryPoint point){
    	CustomMapFragment mapFrag = (CustomMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        
        mapFrag.setCurrentItineraryPoint(point);
    }

    public void setBeaconListener(BeaconListener beaconListener) {
        beaconManager.setBeaconListener(beaconListener);
    }

    public void setUserPositionListener(UserPositionListener userPositionListener) {
        beaconManager.setUserPositionListener(userPositionListener);
    }

    public static Position toMapPosition(Position measuredPosition) {
        double x = measuredPosition.getX() / PositionCalculator.GRID_WIDTH * MAP_WIDTH;
        double y = measuredPosition.getY() / PositionCalculator.GRID_HEIGHT * MAP_HEIGHT;

        return new Position((int) Math.round(x), (int) Math.round(y));
    }

    public static Position toMeasuredPosition(Position mapPosition) {
        double x = mapPosition.getX() / MAP_WIDTH * PositionCalculator.GRID_WIDTH;
        double y = mapPosition.getY() / MAP_HEIGHT * PositionCalculator.GRID_HEIGHT;

        return new Position(x, y);
    }
}
