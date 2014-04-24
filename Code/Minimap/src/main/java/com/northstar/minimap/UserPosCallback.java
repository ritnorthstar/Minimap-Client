package com.northstar.minimap;

public class UserPosCallback implements CallbackListener {

	private CustomMapFragment mapFrag;
	
	public UserPosCallback(CustomMapFragment mapFragment){
		mapFrag = mapFragment;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		mapFrag.updateUsersPos();
	}

}