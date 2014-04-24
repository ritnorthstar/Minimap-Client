package com.northstar.minimap;

public class UpdateUserCallback implements CallbackListener {

	private CustomMapFragment mapFrag;
	
	public UpdateUserCallback(CustomMapFragment mapFragment){
		mapFrag = mapFragment;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		mapFrag.getUsersPos();
	}

}