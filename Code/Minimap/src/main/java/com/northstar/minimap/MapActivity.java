package com.northstar.minimap;

import com.northstar.minimap.beacon.BluetoothLEBeacon;
import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Position;
import com.northstar.minimap.map.Table;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
    
    
    public void processMap(){
    	
    	Map testMap = new Map();
        
        testMap.setMapID("0");
        
        Position posTab1 = new Position(100, 100);
        Table table1 = new Table(4, 2, posTab1, 300, 50);
        
        Position posTab2 = new Position(100, 250);
        Table table2 = new Table(4, 2, posTab2, 300, 50);
        
        Position posTab3 = new Position(100, 400);
        Table table3 = new Table(4, 2, posTab3, 300, 50);
        
        Position posTab4 = new Position(500, 100);
        Table table4 = new Table(2, 4, posTab4, 100, 350);
        
        Position posBcn1 = new Position(125, 125);
        IBeacon beacon1 = new BluetoothLEBeacon("nw", posBcn1);
        
        Position posBcn2 = new Position(375, 125);
        IBeacon beacon2 = new BluetoothLEBeacon("n", posBcn2);
        
        Position posBcn3 = new Position(550, 125);
        IBeacon beacon3 = new BluetoothLEBeacon("ne", posBcn3);
        
        Position posBcn4 = new Position(125, 275);
        IBeacon beacon4 = new BluetoothLEBeacon("w", posBcn4);
        
        Position posBcn5 = new Position(375, 275);
        IBeacon beacon5 = new BluetoothLEBeacon("c", posBcn5);
        
        Position posBcn6 = new Position(550, 275);
        IBeacon beacon6 = new BluetoothLEBeacon("e", posBcn6);
        
        Position posBcn7 = new Position(125, 425);
        IBeacon beacon7 = new BluetoothLEBeacon("sw", posBcn7);
        
        Position posBcn8 = new Position(375, 425);
        IBeacon beacon8 = new BluetoothLEBeacon("s", posBcn8);
        
        Position posBcn9 = new Position(550, 425);
        IBeacon beacon9 = new BluetoothLEBeacon("se", posBcn9);
        
        testMap.addTable(table1);
        testMap.addTable(table2);
        testMap.addTable(table3);
        testMap.addTable(table4);
        
        testMap.addBeacon(beacon1);
        testMap.addBeacon(beacon2);
        testMap.addBeacon(beacon3);
        testMap.addBeacon(beacon4);
        testMap.addBeacon(beacon5);
        testMap.addBeacon(beacon6);
        testMap.addBeacon(beacon7);
        testMap.addBeacon(beacon8);
        testMap.addBeacon(beacon9);
        
        CustomMapFragment mapFrag = (CustomMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        
        mapFrag.setMap(testMap);
        
    }
}
