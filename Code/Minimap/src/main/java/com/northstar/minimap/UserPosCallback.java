package com.northstar.minimap;

public class UserPosCallback implements CallbackListener {

	private CustomMapFragment mapFrag;
	private boolean destroyed = false;
	
	public UserPosCallback(CustomMapFragment mapFragment){
		mapFrag = mapFragment;
	}
	
	@Override
	public void jsonCallback() {
		if(!destroyed){
			mapFrag.updateUsersPos();
		}
	}

	@Override
	public void parentDestroyed() {
		destroyed = true;
	}

}