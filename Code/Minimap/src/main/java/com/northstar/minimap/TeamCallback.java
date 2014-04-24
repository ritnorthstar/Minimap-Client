package com.northstar.minimap;

public class TeamCallback implements CallbackListener {
    
    private SelectionActivity selectAct;
    
    public TeamCallback(SelectionActivity selectActivity) {
        selectAct = selectActivity;
    }
    
    @Override
    public void jsonCallback() {
        selectAct.setTeams();
    }
}