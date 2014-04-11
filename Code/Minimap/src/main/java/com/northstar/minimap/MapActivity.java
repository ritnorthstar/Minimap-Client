package com.northstar.minimap;

import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;
import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.map.BluetoothLELocationSource;
import com.northstar.minimap.map.BoundaryLocationSource;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

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

    public void setUserPositionListener(UserPositionListener userPositionListener) {
        beaconManager.setUserPositionListener(userPositionListener);
    }
    
    public void processMap(){
    	
    	Map testMap = new Map();
        
        testMap.setMapID("0");
        
//        Position posTab1 = new Position(100, 100);
//        Table table1 = new Table(4, 2, posTab1, 300, 50);
//
//        Position posTab2 = new Position(100, 250);
//        Table table2 = new Table(4, 2, posTab2, 300, 50);
//
//        Position posTab3 = new Position(100, 400);
//        Table table3 = new Table(4, 2, posTab3, 300, 50);
//
//        Position posTab4 = new Position(500, 100);
//        Table table4 = new Table(2, 4, posTab4, 100, 350);

        Position p1 = toMapPosition(new Position(0.0, 0.0));
        StickNFindBluetoothBeacon b1 = new StickNFindBluetoothBeacon(null, "FD:65:28:71:80:C0", p1);

        Position p2 = toMapPosition(new Position(PositionCalculator.GRID_WIDTH, 0.0));
        StickNFindBluetoothBeacon b2 = new StickNFindBluetoothBeacon(null, "FE:CC:38:AA:BE:B5", p2);

        Position p3 = toMapPosition(new Position(0.0, PositionCalculator.GRID_HEIGHT));
        StickNFindBluetoothBeacon b3 = new StickNFindBluetoothBeacon(null, "E4:3E:A0:63:BC:C6", p3);

        Position p4 = toMapPosition(new Position(
                PositionCalculator.GRID_WIDTH, PositionCalculator.GRID_HEIGHT));
        StickNFindBluetoothBeacon b4 = new StickNFindBluetoothBeacon(null, "E3:BF:2E:56:BF:B9", p4);
        
//        testMap.addTable(table1);
//        testMap.addTable(table2);
//        testMap.addTable(table3);
//        testMap.addTable(table4);
        
        testMap.addBeacon(b1);
        testMap.addBeacon(b2);
        testMap.addBeacon(b3);
        testMap.addBeacon(b4);
        
        CustomMapFragment mapFrag = (CustomMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        
        mapFrag.setMap(testMap);
    }

    public static Position toMapPosition(Position measuredPosition) {
        double x = measuredPosition.getX() / PositionCalculator.GRID_WIDTH * MAP_WIDTH;
        double y = measuredPosition.getY() / PositionCalculator.GRID_HEIGHT * MAP_HEIGHT;

        return new Position((int) Math.round(x), (int) Math.round(y));
    }
}
