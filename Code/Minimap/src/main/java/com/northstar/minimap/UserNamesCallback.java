package com.northstar.minimap;

public class UserNamesCallback implements CallbackListener {

	private SelectionActivity selectAct;
	
	public UserNamesCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		selectAct.checkRegister();
	}

}