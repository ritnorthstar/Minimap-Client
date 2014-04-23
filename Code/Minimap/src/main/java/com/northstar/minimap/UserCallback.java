package com.northstar.minimap;

public class UserCallback implements CallbackListener {

	private SelectionActivity selectAct;
	
	public UserCallback(SelectionActivity selectActivity){
		selectAct = selectActivity;
	}
	
	@Override
	public void jsonCallback() {
		// TODO Auto-generated method stub
		selectAct.checkRegister();
	}

}