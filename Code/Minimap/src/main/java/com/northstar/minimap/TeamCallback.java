package com.northstar.minimap;

import android.widget.Toast;

public class TeamCallback implements CallbackListener {
    
    private SelectionActivity selectAct;
    
    public TeamCallback(SelectionActivity selectActivity) {
        selectAct = selectActivity;
    }
    
    @Override
    public void jsonCallback() {
        Globals state = (Globals) selectAct.getApplicationContext();

        if (state.data.teamsJson.length() > 0) {
            selectAct.setTeams();
        } else {
            Toast.makeText(selectAct, R.string.no_server_data, Toast.LENGTH_SHORT).show();
            selectAct.finish();
        }
    }
}