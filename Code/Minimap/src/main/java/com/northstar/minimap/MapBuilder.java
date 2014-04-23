package com.northstar.minimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;

public class MapBuilder {

	JSONObject JSONMapDef;
	
	public Map getMap(String JSONMapString, String mapID){
		Map JSONMap = new Map();
		
		Log.w("JP", "Building map");
		
		try {
			JSONArray maps = new JSONArray(JSONMapString);
			for(int i = 0; i < maps.length(); i++){
				JSONObject map = maps.getJSONObject(i);
				if(mapID.equals(map.get("Id"))){
					this.JSONMapDef = map;
					break;
				}
			}
			addTables(JSONMap);
			addBeacons(JSONMap);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("JP", "Failed");
		}
		
		return JSONMap;
	}
	
	private void addTables(Map JSONMap) throws JSONException{
		JSONArray tables = JSONMapDef.getJSONArray("Tables");
		for(int i = 0; i < tables.length(); i++){
			JSONObject JSONTable = tables.getJSONObject(i);
			Table table = new Table(JSONTable.getInt("TablesWide"), 
									JSONTable.getInt("TablesTall"), 
									new Position(JSONTable.getDouble("X"),
												 JSONTable.getDouble("Y")), 
									JSONTable.getInt("Width"), 
									JSONTable.getInt("Height"));
			JSONMap.addTable(table);
		}
	}
	
	private void addBeacons(Map JSONMap) throws JSONException{
		Log.w("JP","In beacons");
		JSONArray beacons = JSONMapDef.getJSONArray("Beacons");
		Log.w("JP","Got Array");
		for(int i = 0; i < beacons.length(); i++){
			JSONObject JSONBeacon = beacons.getJSONObject(i);
			IBeacon beacon = new StickNFindBluetoothBeacon(null,
														   i,
														   JSONBeacon.getString("BeaconId"), 
														   new Position(JSONBeacon.getDouble("X"),
																	 	JSONBeacon.getDouble("Y")));
			JSONMap.addBeacon(beacon);
		}
	}
	
}
