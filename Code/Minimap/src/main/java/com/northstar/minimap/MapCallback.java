package com.northstar.minimap;

public class MapCallback implements CallbackListener {

	private SelectionActivity selectAct;
	
	public MapCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		selectAct.setMaps();
	}

}
