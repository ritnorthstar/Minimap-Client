package com.northstar.minimap;

public class MapCallback implements CallbackListener {

    private SelectionActivity selectAct;
    private boolean destroyed = false;
    
    public MapCallback(SelectionActivity selectActivity) {
        selectAct = selectActivity;
    }
    
    @Override
    public void jsonCallback() {
    	if(!destroyed){
    		selectAct.setMaps();
    	}
    }

	@Override
	public void parentDestroyed() {
		destroyed = true;
	}
}