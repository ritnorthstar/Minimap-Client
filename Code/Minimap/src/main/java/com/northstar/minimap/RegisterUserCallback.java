package com.northstar.minimap;

public class RegisterUserCallback implements CallbackListener {

	private SelectionActivity selectAct;
	private boolean destroyed = false;
	
	public RegisterUserCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		if(!destroyed){
			selectAct.goToMap();
		}
	}
	
	@Override
	public void parentDestroyed() {
		destroyed = true;
	}
}
