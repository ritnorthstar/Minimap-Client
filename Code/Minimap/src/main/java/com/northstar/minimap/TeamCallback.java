package com.northstar.minimap;

public class TeamCallback implements CallbackListener {
    
    private SelectionActivity selectAct;
    private boolean destroyed = false;
    
    public TeamCallback(SelectionActivity selectActivity) {
        selectAct = selectActivity;
    }
    
    @Override
    public void jsonCallback() {
    	if(!destroyed){
    		selectAct.setTeams();
    	}
    }
    
    @Override
	public void parentDestroyed() {
		destroyed = true;
	}
}