package com.northstar.minimap;

public class RegisterUserCallback implements CallbackListener {

	private SelectionActivity selectAct;
	
	public RegisterUserCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		selectAct.goToMap();
	}
}
