package com.northstar.minimap;

public class MapCallback implements CallbackListener {

	private MapActivity mapAct;
	
	public MapCallback(MapActivity mapActivity){
		mapAct = mapActivity;
	}
	
	@Override
	public void mapJsonCallback() {
		// TODO Auto-generated method stub
		mapAct.setMap();
	}

}
