package com.northstar.minimap;

import android.widget.Toast;

public class MapCallback implements CallbackListener {

    private SelectionActivity selectAct;
    private boolean destroyed = false;
    
    public MapCallback(SelectionActivity selectActivity) {
        selectAct = selectActivity;
    }
    
    @Override
    public void jsonCallback() {
        Globals state = (Globals) selectAct.getApplicationContext();
        if (state.data.mapsJson.length() > 0) {
            if (!destroyed) {
                selectAct.setMaps();
            }
        } else {
            Toast.makeText(selectAct, R.string.no_server_data, Toast.LENGTH_SHORT).show();
            selectAct.finish();
        }
    }

	@Override
	public void parentDestroyed() {
		destroyed = true;
	}
}