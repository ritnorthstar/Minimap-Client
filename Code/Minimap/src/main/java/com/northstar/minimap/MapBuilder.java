package com.northstar.minimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;

public class MapBuilder {

	JSONObject JSONMapDef;
	
	public Map getMap(String JSONMapString){
		Map JSONMap = new Map();
		
		try {
			this.JSONMapDef = new JSONObject(JSONMapString);
			addTables(JSONMap);
			addBeacons(JSONMap);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		JSONArray beacons = JSONMapDef.getJSONArray("Beacons");
		for(int i = 0; i < beacons.length(); i++){
			JSONObject JSONBeacon = beacons.getJSONObject(i);
			IBeacon beacon = new StickNFindBluetoothBeacon(null,
														   JSONBeacon.getString("BeaconID"), 
														   new Position(JSONBeacon.getDouble("X"),
																	 	JSONBeacon.getDouble("Y")));
			JSONMap.addBeacon(beacon);
		}
	}
	
}
