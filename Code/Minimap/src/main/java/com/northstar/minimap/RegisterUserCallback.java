package com.northstar.minimap;

public class RegisterUserCallback implements CallbackListener {

	private SelectionActivity selectAct;
	
	public RegisterUserCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		selectAct.goToMap();
	}

}
