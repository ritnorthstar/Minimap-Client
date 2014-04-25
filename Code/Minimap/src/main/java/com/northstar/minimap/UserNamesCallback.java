package com.northstar.minimap;

public class UserNamesCallback implements CallbackListener {

	private SelectionActivity selectAct;
	private boolean destroyed = false;
	
	public UserNamesCallback(SelectionActivity selectActivity){
		if(!destroyed){
			selectAct = selectActivity;
		}
	}
	
	@Override
	public void jsonCallback() {
		if(!destroyed){
			selectAct.checkRegister();
		}
	}

	@Override
	public void parentDestroyed() {
		destroyed = true;
	}

}