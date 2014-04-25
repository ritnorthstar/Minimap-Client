package com.northstar.minimap;

public class UpdateUserCallback implements CallbackListener {

	private CustomMapFragment mapFrag;
	private boolean destroyed = false;
	
	public UpdateUserCallback(CustomMapFragment mapFragment){
		mapFrag = mapFragment;
	}
	
	@Override
	public void jsonCallback() {
		if(!destroyed){
			mapFrag.getUsersPos();
		}
	}
	
	@Override
	public void parentDestroyed() {
		destroyed = true;
	}

}